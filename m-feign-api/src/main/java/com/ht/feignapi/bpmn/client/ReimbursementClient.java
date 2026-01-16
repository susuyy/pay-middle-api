package com.ht.feignapi.bpmn.client;

import com.ht.feignapi.bpmn.entity.FormUserRoot;
import com.ht.feignapi.bpmn.entity.FormUserRootVo;
import com.ht.feignapi.bpmn.entity.ProcessLinkConditionConfig;
import com.ht.feignapi.bpmn.entity.ProcessLinkConditionConfigVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "${custom.client.reimbursement.name}",contextId = "reimbursement")
public interface ReimbursementClient {

    @PostMapping("/formUserRoot/addFormUserRoot")
    Object addFormUserRoot(@RequestBody FormUserRootVo formUserRootVo) throws Exception;

    //获取流程流转的El表达式
    @PostMapping("/process-link-condition-config/getEl")
    Map<String,Object> getEl(@RequestBody ProcessLinkConditionConfigVo processLinkConditionConfigVo) throws Exception;

    @GetMapping("/reimbursement/form-making-field/field/value")
    Map<String,String> getFormFieldValue(@RequestParam("formKey")String formKey
            ,@RequestParam("formFieldName") String formFieldName
            ,@RequestParam("formType") String formType);

    @GetMapping("/formUserRoot/selectByProcessId")
    Map<String,Object> selectByProcessId(@RequestParam("processId") String processId);

    @PostMapping("/reimbursement/process/submit")
    Map<String,String> submitForFirst(@RequestBody Map<String,Object> paraMap);

    //用户更新新增表单数据
    @PostMapping("/reimbursement/form-making-field/batch/update")
    Map<String,String> update(@RequestBody Map<String,Object> paraMap);

    //用户需要审批的表单信息内容
    @GetMapping("reimbursement/form-making/getByProcessInstanceId")
    Map<String,Object> getPromDate(@RequestParam("processInstanceId")String processInstanceId
            ,@RequestParam("formTemplateKey")String formTemplateKey);

    //根据processId和环节Id查询权限id
    @GetMapping("/formUserRoot/getUserId")
    Map<String,Object> getUserId(@RequestParam("processId")String processId,@RequestParam("linkId")String linkId);

    //根据processId和环节Id查询权限id
    @GetMapping("/per-user-config/selectGroupCode")
    Map<String,Object> selectGroupCode(@RequestParam("userId")String userId,@RequestParam("bpmnLinkCode")String bpmnLinkCode);

    //根据processId和环节Id查询权限id
    @GetMapping("/per-user-config/selectUserName")
    Map<String,Object> selectUserName(@RequestParam("userId")String userId);

    //根据用户id获取用户所在分局的局长组
    @GetMapping("/perGroup/selectDirectorCode")
    Map<String,Object> selectDirectorCode(@RequestParam("userId")String userId);

    //根据processId和审批组查询打印配置
    @GetMapping("/formUserRoot/selectPrinteConfig")
    Map<String,Object> selectPrinteConfig(@RequestParam("processId")String processId,@RequestParam("userGroup")String userGroup);

    //根据typeCode查找数据
    @GetMapping("/process-bpmn-type/selectByTypeCode")
    Map<String,Object> selectByTypeCode(@RequestParam("typeCode")String typeCode);

    //根据groupCode查找表单填写用户名称的字段key数据
    @GetMapping("/disConstant/groupCode/form_name_get_rank")
    Map<String,Object> selectUserKeyByGroupCode(@RequestParam("groupCode")String groupCode);

    //根据typeCode获取流程绑定的表单code
    @GetMapping("/form-process-form-config/selectConfigByTypeCode")
    Map<String,Object> selectConfigByTypeCode(@RequestParam("typeCode")String typeCode);

    //根据groupCode查找表单填写用户名称的字段key数据
    @GetMapping("/disConstant/groupCode/{groupCode}")
    Map<String,Object> selectTotalKey(@PathVariable(value = "groupCode")String groupCode);
    //获取表单的编号
    @GetMapping("/sequence/dic-sequence/getSequence")
    Map<String,Object> getFormSequence();


}
