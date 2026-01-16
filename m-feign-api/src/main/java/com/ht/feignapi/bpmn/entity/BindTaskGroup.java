package com.ht.feignapi.bpmn.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BindTaskGroup implements Serializable {

    /**
     * 流程id
     */
    private String processId;

    /**
     * 节点id
     */
    private String userTaskId;

    /**
     * 分组编码
     */
    private List<String> orgCode;


}
