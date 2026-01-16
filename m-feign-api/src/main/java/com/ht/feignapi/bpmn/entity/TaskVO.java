package com.ht.feignapi.bpmn.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.security.core.parameters.P;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class TaskVO implements Serializable {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date taskCreateTime;

    /**
     * 任务办理人
     */
    private String taskAssignee;

    /**
     * 活动实例id
     */
    private String processInstanceId;

    /**
     * 流程完结状态 已完结  未完结
     */
    private String processState;

    /**
     * 表单信息
     */
    private Map tableMap;


//
//    private String executionId;
//
    /**
     * 流程定义Id
     */
    private String processDefinitionId;

    /**
     * 流程定义Id
     */
    private String taskDefinitionKey;

    /**
     * 流程定义版本
     */
    private String version;
}
