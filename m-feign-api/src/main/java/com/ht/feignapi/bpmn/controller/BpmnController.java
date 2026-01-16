package com.ht.feignapi.bpmn.controller;

import com.ht.feignapi.bpmn.client.ReimbursementClient;
import com.ht.feignapi.bpmn.entity.*;
import com.ht.feignapi.bpmn.client.BpmnClient;
import com.ht.feignapi.bpmn.utils.FormDataUpdate;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.util.ElUtils;
import com.ht.feignapi.util.GetErrorInfoFromException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/bpmn")
//@CrossOrigin(allowCredentials = "true")
@Api(value = "bpmn流程类", description = "bpmn流程流转类")
public class BpmnController {

    private static final Logger logger = LoggerFactory.getLogger(BpmnController.class);

    @Autowired
    private BpmnClient bpmnClient;

    @Autowired
    private ReimbursementClient reimbursementClient;


    /**
     * 权限测试
     * @return
     */
  //  @PreAuthorize("hasAnyAuthority('2001','2002')")
    @GetMapping("/test")
    public List test(){
        bpmnClient.test();
        return new ArrayList();
    }

    /**
     * 用户创建一个流程(用户第一次提交自己的申请单)
     * @param createActivityDataVo
     * @throws Exception
     */
    @PostMapping(value = "/create")
    public void startLeaveProcess(@RequestBody CreateActivityDataVo createActivityDataVo) throws Exception {
        try {
            //根据部署的Id获取部署的信息
            Map<String,Object> processMap = reimbursementClient.selectByProcessId(createActivityDataVo.getProcessId());
            Map<String,String> formUserRootMap = (Map<String,String>) processMap.get("data");
            String key = formUserRootMap.get("processKey");
            String processVersion = formUserRootMap.get("processVersion");
            String userKey = createActivityDataVo.getUserId();
            String userTel = createActivityDataVo.getUserTel();
            CreateActivityData createActivityData = new CreateActivityData();
            createActivityData.setProcessDefinitionKey(key);
            createActivityData.setUserKey(userKey);
            //发起一个流程实例
            Map<String,String> map = (Map<String, String>)bpmnClient.startLeaveProcess(createActivityData).get("data");
            String processInstanceId = map.get("processInstanceId");
            String taskId = map.get("taskId");
            String assignee = map.get("assignee");



            //根据taskId获取下一个环节的id
            Map<String,String> nextTaskMap = bpmnClient.queryNextIdByTaskId(taskId,"1");
            String linkId = nextTaskMap.get("data");

            //根据部署id和环节id获取下一级审批组
            Map<String,Object> nextUserMap = (Map<String, Object>) reimbursementClient.getUserId(createActivityDataVo.getProcessId(),linkId).get("data");
            String approve = (String) nextUserMap.get("userId");
            String linkName = (String) nextUserMap.get("linkName");
            approve = (String) reimbursementClient.selectGroupCode(userKey,approve).get("data");

            //给bpmn提交数据
            Map<String,Object> submitBpmnMap = new HashMap<>();
            //下一级批准人
            submitBpmnMap.put("approve",approve);
            //当前审批人
            submitBpmnMap.put("approver",userKey);
            //审核  1通过    0驳回   -1结束
            submitBpmnMap.put("audit","1");
            //任务,业务ID
            submitBpmnMap.put("taskId",taskId);
            //绑定表单Key
            submitBpmnMap.put("formKey",processInstanceId);
            //事前审批单Key
            submitBpmnMap.put("approvalFormKey",processInstanceId);
            //审批意见Key
            submitBpmnMap.put("opinionKey","");
            //绑定附件Key
            submitBpmnMap.put("enclosureKey",createActivityDataVo.getEnclosureKeyValue());
            //流程实例id
            submitBpmnMap.put("processInstanceId",processInstanceId);
            bpmnClient.completeTask(submitBpmnMap);

            List<Map<String,String>> formProcessFormConfigList = (List<Map<String, String>>)
                    reimbursementClient.selectConfigByTypeCode(createActivityDataVo.getTypeCode()).get("data");
            Map<String,Object> dataMap = createActivityDataVo.getDataMap();

            List<Map<String,Object>> updateFormList = FormDataUpdate
                    .formDataUpdate(formProcessFormConfigList,dataMap,taskId,userKey,processInstanceId);

            for(int i=0;i<updateFormList.size();i++){
                Map<String,String> resUpdateFormFieldMap = reimbursementClient.update(updateFormList.get(i));
                logger.info("提交表单数据："+resUpdateFormFieldMap.toString());
            }


            //获取总金额
            List totalList = (List) reimbursementClient.selectTotalKey(createActivityDataVo.getTypeCode()).get("data");
            Map<String,Object> totalMap = (Map<String, Object>) totalList.get(0);
            logger.info("总金额的Map："+totalMap);
            String total = (String) totalMap.get("value");
            logger.info("总金额的Key："+total);
            String fieldValue = "";
            if(createActivityDataVo.getTypeCode() == "CL-001" || "CL-001".equals(createActivityDataVo.getTypeCode())){
                fieldValue = reimbursementClient.getFormFieldValue(processInstanceId,total,"formKey").get("data");
            }else if(createActivityDataVo.getTypeCode() == "CL-002" || "CL-002".equals(createActivityDataVo.getTypeCode())){
                fieldValue = reimbursementClient.getFormFieldValue(processInstanceId,total,"missMealFormKey").get("data");
            }

            //提交审批过程(给业务)
            Map<String,Object> resMap = new HashMap<>();
            resMap.put("processInstanceId",processInstanceId);
            resMap.put("taskId",taskId);
            resMap.put("opinionKeyValue","");
            resMap.put("enclosureKeyValue",createActivityDataVo.getEnclosureKeyValue());
            String listName = createActivityDataVo.getDepartmentName()+createActivityDataVo.getTypeName();
            resMap.put("listName",listName);
            resMap.put("listType",createActivityDataVo.getTypeCode());
            resMap.put("relationListCode",createActivityDataVo.getRelationListCode());
            resMap.put("tel",userTel);
            resMap.put("currentStepId", linkId);
            resMap.put("currentStepName", linkName);
            resMap.put("version", processVersion);
            resMap.put("amount",fieldValue);
            resMap.put("submitter",userKey);
            String applicantName = (String) reimbursementClient.selectUserName(assignee).get("data");
            String userGroup = (String) reimbursementClient.selectGroupCode(userKey,"WBJ-01-JBR").get("data");
            resMap.put("applicant",assignee);
            resMap.put("applicantName",applicantName);
            resMap.put("submitterName",applicantName);
            resMap.put("submitterGroup",userGroup);
            resMap.put("reviewer",approve);
            resMap.put("status","1");
            logger.info("提交数据给业务："+resMap.toString());
            reimbursementClient.submitForFirst(resMap);

        }catch (Exception e){
            logger.error(GetErrorInfoFromException.getErrorInfoFromException(e));
            throw new ResultException(ResultTypeEnum.SERVICE_ERROR);
        }
    }

    /**
     * 功能描述: <根据实例Id获取流程审批历史及表单信息>
     * @param processInstanceId
     *
     * @return java.util.Map
     * @author liwg
     * @creed: Talk is cheap,show me the code
     * @date 2020/10/16 11:37
     */

    @GetMapping("getFormAndProcessData")
    public Map getFormAndProcessData(@RequestParam("processInstanceId") String processInstanceId
            ,@RequestParam("listType")String listType
            ,@RequestParam("userGroup")String userGroup) throws Exception {
        try {
            Map<String,Object> resMap = new HashMap<>();
            //获取审批历史
            List<Map<String,Object>> historyProcessMap = (List<Map<String, Object>>) bpmnClient.selectHistoryByProcessInstanceId(processInstanceId).get("data");
            resMap.put("processHistory",historyProcessMap);
            //表单结构与内容值
            Map<String,Object> formData = (Map<String, Object>) reimbursementClient.getPromDate(processInstanceId,listType).get("data");
            resMap.put("formData",formData);
            Map<String,Object> processBpmnType = (Map<String, Object>) reimbursementClient.selectByTypeCode(listType).get("data");
            resMap.put("processBpmnType",processBpmnType);
            Map<String,Object> printeConfig = (Map<String, Object>) reimbursementClient.selectPrinteConfig((String) processBpmnType.get("processId"),userGroup).get("data");
            resMap.put("printeConfig",printeConfig);
            return resMap;
        }catch (Exception e){
            logger.error(GetErrorInfoFromException.getErrorInfoFromException(e));
            throw new ResultException(ResultTypeEnum.SERVICE_ERROR);
        }
    }

    /**
     * 查询 用户 userKey 所在的流程列表
     * @param assignee
     * @return
     * @throws Exception
     */
    @GetMapping("/queryMyList/{assignee}")
    public List queryLeaveProcessING(@PathVariable("assignee") String assignee) throws Exception {
        List leaveProcessING = bpmnClient.queryLeaveProcessING(assignee);
        return leaveProcessING;
    }

    /**
     * 功能描述: 流程提交
     * @Author: liwg
     * @Date: 2020/10/13 11:17
     */
    @PostMapping(value = "/submit")
    public void completeTask(@RequestBody SubmitActivityData submitActivityData) throws Exception {
        try {
            logger.info("用户提交自己的审批:"+submitActivityData);
            //当前审批人
            String userKey = submitActivityData.getUserId();
            //当前审批人电话
            String userTel = submitActivityData.getUserTel();
            //当前审批组
            String userGroup = submitActivityData.getUserGroup();
            //撤销-1，驳回0，提交1
            String audit = submitActivityData.getAudit();

            //实例Id
            String processInstanceId = submitActivityData.getProcessInstanceId();
            Map<String,Object> taskMap = new HashMap<>();
            if(audit == "-1" || "-1".equals(audit)){
                 taskMap = (Map<String, Object>) bpmnClient.selectTaskByUser(userKey,processInstanceId).get("data");
            }else{
                //正常提交
                 taskMap = (Map<String, Object>) bpmnClient.selectTaskByUser(userGroup,processInstanceId).get("data");
                 //若是被驳回的流程，用分组的则查不到，需要用用户查询
                 if(taskMap == null){
                     taskMap = (Map<String, Object>) bpmnClient.selectTaskByUser(userKey,processInstanceId).get("data");
                 }
            }
            //taskId
            String taskId = (String) taskMap.get("taskId");
            //部署id
            String processDefinitionId = (String) taskMap.get("processDefinitionId");
            //linkId
            String taskDefinitionKey = (String) taskMap.get("taskDefinitionKey");
            //环节名称
            String taskName = (String) taskMap.get("taskName");
            //发起人
            String user = ((Map<String,String>)taskMap.get("tableMap")).get("user");
            String applicantName = (String) reimbursementClient.selectUserName(user).get("data");
            String status = "";

            //根据部署的Id获取部署的信息
            Map<String,Object> processMap = reimbursementClient.selectByProcessId(processDefinitionId);
            Map<String,String> formUserRootMap = (Map<String,String>) processMap.get("data");
            String processVersion = formUserRootMap.get("processVersion");

            String linkId = "";

            //根据taskId获取下一个环节的id
            if(audit == "0" || "0".equals(audit)){
                linkId = bpmnClient.queryNextIdByTaskId(taskId,"0").get("data");
            }else if(audit == "-1" || "-1".equals(audit)){
                linkId = "-1";
            }else{
                linkId = bpmnClient.queryNextIdByTaskId(taskId,"1").get("data");
            }

            String approve = "";
            String linkName = "";

            //根据部署id和环节id获取下一级审批组(判断是否有下个环节)
            if(!"-1".equals(linkId)){
                Map<String,Object> nextUserMap = (Map<String, Object>) reimbursementClient.getUserId(processDefinitionId,linkId).get("data");
                 approve = (String) nextUserMap.get("userId");
                 linkName = (String) nextUserMap.get("linkName");
                approve = (String) reimbursementClient.selectGroupCode(userKey,approve).get("data");
            }
            //给bpmn提交数据
            Map<String,Object> submitBpmnMap = new HashMap<>();
            //下一级批准人
            if(linkId == "-1" || "-1".equals(linkId)){
                submitBpmnMap.put("approve","");
            }else {
                submitBpmnMap.put("approve",approve);
            }
            //当前审批人
            submitBpmnMap.put("approver",userKey);
            //审核
            submitBpmnMap.put("audit",audit);
            //任务,业务ID
            submitBpmnMap.put("taskId", taskId);
            //绑定表单Key
            submitBpmnMap.put("formKey",processInstanceId);
            //事前审批单Key
            submitBpmnMap.put("approvalFormKey",processInstanceId);
            //审批意见Key
            submitBpmnMap.put("opinionKey",processInstanceId);
            //绑定附件Key
            submitBpmnMap.put("enclosureKey",submitActivityData.getEnclosureKeyValue());
            //流程实例id
            submitBpmnMap.put("processInstanceId",processInstanceId);


            //获取总金额
            List totalList = (List) reimbursementClient.selectTotalKey(submitActivityData.getTypeCode()).get("data");
            Map<String,Object> totalMap = (Map<String, Object>) totalList.get(0);
            //获取总金额的key
            String total = (String) totalMap.get("value");
            //获取总金额
            String fieldValue = "";
            if(submitActivityData.getTypeCode() == "CL-001" || "CL-001".equals(submitActivityData.getTypeCode())){
                fieldValue = reimbursementClient.getFormFieldValue(processInstanceId,total,"formKey").get("data");
            }else if(submitActivityData.getTypeCode() == "CL-002" || "CL-002".equals(submitActivityData.getTypeCode())){
                fieldValue = reimbursementClient.getFormFieldValue(processInstanceId,total,"missMealFormKey").get("data");
            }


            //声明用户所在分局的局长组code
            String sendGroupCode = "";
            //根据条件判断gateway的分支走向
            if(submitActivityData.getAudit() == "1" || "1".equals(submitActivityData.getAudit())){
            //设置审核状态为通过
            status = "1";
            ProcessLinkConditionConfigVo processLinkConditionConfigVo = new ProcessLinkConditionConfigVo();
            processLinkConditionConfigVo.setProcessId(processDefinitionId);
            processLinkConditionConfigVo.setLinkId(taskDefinitionKey);
            Map<String,String>  processLinkConditionConfigMap = (Map<String, String>) reimbursementClient.getEl(processLinkConditionConfigVo).get("data");
            if(processLinkConditionConfigMap != null){
            //如果processLinkConditionConfig != null则表示这个环节没有设置条件判断
                boolean flag = ElUtils.isCondition(processLinkConditionConfigMap.get("conditionEl")
                        ,processLinkConditionConfigMap.get("formFieldName"),fieldValue);
                if(flag == true){
                    //如果下个环节Id是-1的话则表示审批人为站长
                    if(linkId == "-1" || "-1".equals(linkId)){
                        submitBpmnMap.put("audit","1");
                        //获取当前人员所在分局的局长组code
                        sendGroupCode = (String) reimbursementClient.selectDirectorCode(userKey).get("data");
                        bpmnClient.completeTask(submitBpmnMap);
                        linkId = "-1";
                    }else {
                        submitBpmnMap.put("audit","1");
                        bpmnClient.completeTask(submitBpmnMap);
                    }
                }else{
                    submitBpmnMap.put("audit","-1");
                    bpmnClient.completeTask(submitBpmnMap);
                    linkId = "-1";
                }
            }else{
                bpmnClient.completeTask(submitBpmnMap);
            }
            }else{
                //驳回情况，不用判断
                bpmnClient.completeTask(submitBpmnMap);
                //设置审核状态为驳回
                status = "-1";
            }

            List<Map<String,String>> formProcessFormConfigList = (List<Map<String, String>>)
                    reimbursementClient.selectConfigByTypeCode(submitActivityData.getTypeCode()).get("data");
            Map<String,Object> dataMap = submitActivityData.getDataMap();
            List<Map<String,Object>> updateFormList = FormDataUpdate
                    .formDataUpdate(formProcessFormConfigList,dataMap,taskId,userKey,processInstanceId);
            for(int i=0;i<updateFormList.size();i++){
                Map<String,String> resUpdateFormFieldMap = reimbursementClient.update(updateFormList.get(i));
                logger.info("提交表单数据结果："+resUpdateFormFieldMap.toString());
            }

            //提交审批过程
            Map<String,Object> resMap = new HashMap<>();
            resMap.put("processInstanceId",submitActivityData.getProcessInstanceId());
            resMap.put("taskId",taskId);
            resMap.put("opinionKeyValue",submitActivityData.getOpinionKeyValue());
            resMap.put("enclosureKeyValue",submitActivityData.getEnclosureKeyValue());
            resMap.put("relationListCode","");
            String listName = submitActivityData.getDepartmentName()+submitActivityData.getTypeName();
            resMap.put("listName",listName);
            resMap.put("submitterGroup",userGroup);
            resMap.put("amount",fieldValue);
            resMap.put("listType",submitActivityData.getTypeCode());
            resMap.put("tel", userTel);
            //判断下个环节是否为空，为空则流程结束
            if(linkId == "-1" || "-1".equals(linkId)){
                resMap.put("currentStepId", "");
                resMap.put("status","2");
                resMap.put("reviewer","");
                if(!sendGroupCode.equals("")){
                    resMap.put("sendGroupCode",sendGroupCode);
                }
            }else {
                resMap.put("status",status);
                resMap.put("reviewer",approve);
                resMap.put("currentStepId", linkId);
            }
            resMap.put("currentStepName", linkName);
            resMap.put("version", processVersion);
            String submitterName = (String) reimbursementClient.selectUserName(userKey).get("data");
            resMap.put("submitter",userKey);
            resMap.put("submitterName",submitterName);
            resMap.put("applicant",user);
            resMap.put("applicantName",applicantName);

            Map<String,String> submitMap = reimbursementClient.submitForFirst(resMap);
            logger.info("用户提交审批过程："+submitMap.toString());

        }catch (Exception e ){
            logger.error(GetErrorInfoFromException.getErrorInfoFromException(e));
            throw new ResultException(ResultTypeEnum.SERVICE_ERROR);
        }
    }


    /**
     * 流程审核通过
     * @param taskEndData
     * @throws Exception
     */
    @PostMapping("/audit")
    public void taskEnd(@RequestBody AuditData taskEndData) throws Exception {
        System.out.println("提交的数据"+taskEndData.getTableList());
        bpmnClient.taskEnd(taskEndData);
    }

    /**
     * 查询历史流程
     * @param userKey
     * @return
     */
    @GetMapping("/queryHis/{userKey}")
    public List queryHistoryTaskByUser(@PathVariable("userKey") String userKey) throws Exception {
        List historicTaskInstances = bpmnClient.queryHistoryTaskByUser(userKey);
        return historicTaskInstances;
    }


    /**
     * 查询表单信息
     * @return
     * @throws Exception
     */
    @GetMapping("/queryTable")
    public List queryTable() throws Exception {
        List dataList = bpmnClient.queryTable();
        return dataList;
    }

    /**
     * 绑定分组
     * @param bindTaskGroup
     * @throws Exception
     */
    @PostMapping("/bindTaskGroup")
    public void bindTaskGroup(@RequestBody BindTaskGroup bindTaskGroup) throws Exception {
        bpmnClient.bindTaskGroup(bindTaskGroup);
    }

    /**
     * 根据key 获取bpmn xml字符串
     * @param key
     * @return
     * @throws Exception
     */
    @GetMapping("/getXmlStr")
    public String getXmlStrByKey(String key) throws Exception {
        String xmlStr = bpmnClient.getXmlStr(key);
        return xmlStr;
    }

    /**
     * 功能描述: <部署流程，并将部署流程的信息保存起来>
     * @Author: liwg
     * @Date: 2020/10/13 10:26
     */
    @PostMapping("/deployment")
    @ApiOperation(value ="部署流程",notes = "部署流程，并将部署流程的信息保存起来")
    public Result deployment(@RequestBody BpmnMessageVo bpmnMessageVo) throws Exception{
        try {
            logger.info("部署流程："+bpmnMessageVo.getBpmnName());
            Map<String,String> resMap = (Map<String, String>) bpmnClient.deployment(bpmnMessageVo).get("data");
            List<Map<String,String>> taskList = (List<Map<String,String>>)bpmnClient
                    .getBpmnTaskIdList(resMap.get("id")).get("data");
            String id = resMap.get("id");
            String deploymentId = resMap.get("deploymentId");
            String name = resMap.get("name");
            String version = resMap.get("version");
            String key = resMap.get("key");
            //新增该流程与环节id的关系
            for(int i = 0; i<taskList.size(); i++){
                FormUserRootVo formUserRootVo = new FormUserRootVo();
                formUserRootVo.setProcessId(id);
                formUserRootVo.setDeploymentId(deploymentId);
                formUserRootVo.setProcessName(name);
                formUserRootVo.setProcessVersion(version);
                formUserRootVo.setProcessKey(key);
                formUserRootVo.setLinkId(taskList.get(i).get("id"));
                formUserRootVo.setLinkName(taskList.get(i).get("name"));
                reimbursementClient.addFormUserRoot(formUserRootVo);
            }
            logger.info(resMap.toString());
        }catch (Exception e){
            logger.error(GetErrorInfoFromException.getErrorInfoFromException(e));
            throw  new ResultException(ResultTypeEnum.SERVICE_ERROR);
        }
        return Result.success(ResultTypeEnum.SERVICE_ERROR);
    }

}
