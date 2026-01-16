package com.ht.feignapi.bpmn.client;


import com.ht.feignapi.bpmn.entity.*;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("${custom.client.bpmn.name}")
public interface BpmnClient {


    @GetMapping("/activity/test")
    void test();

    /**
     * 开启一个流程
     *
     * @param createActivityData
     */
    @PostMapping("/activity/create")
    Map<String,Object> startLeaveProcess(@RequestBody CreateActivityData createActivityData) throws Exception;

    /**
     * 查询 用户 userKey 所在的流程列表
     *
     * @param assignee
     * @return
     */
    @GetMapping("/activity/queryMyList/{assignee}")
    List queryLeaveProcessING(@PathVariable("assignee") String assignee) throws Exception;

    /**
     * 流程提交
     *
     * @param submitBpmnData
     */
    @PostMapping("/activity/submit")
    void completeTask(@RequestBody Map<String,Object> submitBpmnData) throws Exception;

    /**
     * 流程审核通过
     */
    @PostMapping("/activity/audit")
    void taskEnd(@RequestBody AuditData taskEndData) throws Exception;

    /**
     * 查询历史流程
     *
     * @param userKey
     * @return
     */
    @GetMapping("/activity/queryHis/{userKey}")
    List queryHistoryTaskByUser(@PathVariable("userKey") String userKey) throws Exception;

    /**
     * 查询表单信息
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/activity/queryTable")
    List queryTable() throws Exception;


    /**
     * 流程节点绑定 分组
     * @param bindTaskGroup
     */
    @PostMapping("/busMapTaskOrg/bindTaskGroup")
    void bindTaskGroup(@RequestBody BindTaskGroup bindTaskGroup);

    /**
     * 获取bpm xml字符串
     * @param key
     * @return
     */
    @GetMapping("/actReProcdef/getXmlStr")
    String getXmlStr(@RequestParam("key") String key);

    /**
     * 功能描述: 部署流程，获取流程id，流程名称，流程版本，流程key值
     * @Author: liwg
     * @Date: 2020/9/28 18:01
     */
    @PostMapping("/activity/deployment")
    Map<String,Object> deployment(@RequestBody BpmnMessageVo bpmnMessageVo) throws Exception;

    @GetMapping("/activity/getBpmnTaskIdList")
    Map<String,Object> getBpmnTaskIdList(@RequestParam("id") String id) throws Exception;

    @GetMapping("/activity/queryLeaveByTaskId")
    Map<String,Object> queryLeaveByTaskId(@RequestParam("taskId") String taskId) throws Exception;

    @GetMapping("/activity/queryNextIdByTaskId")
   Map<String,String> queryNextIdByTaskId(@RequestParam("taskId") String taskId,@RequestParam("audit") String audit) throws Exception;

    @GetMapping("/activity/selectHistoryByProcessInstanceId")
    Map<String,Object> selectHistoryByProcessInstanceId(@RequestParam("processInstanceId") String processInstanceId) throws Exception;


    @GetMapping("/activity/selectTaskByUser")
    Map<String,Object> selectTaskByUser(@RequestParam("assignee") String assignee
            ,@RequestParam("processInstanceId") String processInstanceId) throws Exception;
}
