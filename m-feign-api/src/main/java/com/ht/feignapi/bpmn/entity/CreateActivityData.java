package com.ht.feignapi.bpmn.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateActivityData implements Serializable {

    private String userKey;

    private String processDefinitionKey;

}
