package com.ht.feignapi.bpmn.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class FormPropertyData implements Serializable {

    private String name;

    private String propCN;

    private String value;

    private String id;

    private String type;

    private String readable;

    private String writeable;

    private String required;

}
