package com.ht.feignapi.bpmn.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class HisTaskVO implements Serializable {
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
     * 任务结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date taskEndTime;

    /**
     * 任务办理人
     */
    private String taskAssignee;

    /**
     * 活动实例id
     */
    private String processInstanceId;

    /**
     * 流程状态
     */
    private String processState;

    /**
     * 活动状态
     */
    private String status;

    /**
     * 表单map
     */
    private Map tableMap;
}
