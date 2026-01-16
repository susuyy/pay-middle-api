package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BindTelData implements Serializable {

    private String authCode;
    private String phoneNum;
    private String openid;
    private String merchantCode;
}
