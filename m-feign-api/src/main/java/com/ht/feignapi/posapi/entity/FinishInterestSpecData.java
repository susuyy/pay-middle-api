package com.ht.feignapi.posapi.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class FinishInterestSpecData implements Serializable {

    private String account;

    private String merchantCode;

    private String interestsSpecNo;
}
