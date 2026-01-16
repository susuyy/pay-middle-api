package com.ht.feignapi.bpmn.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AuditData implements Serializable {

    /**
     * 批准人
     */
    private String approve;

    /**
     * 活动id
     */
    private String taskId;

    /**
     * 审核  1通过    2不通过
     */
    private Integer audit;


    /**
     * 表单数据
     */
    private List tableList;

    /**
     * 流程实例id
     */
    private String processInstanceId;
}
