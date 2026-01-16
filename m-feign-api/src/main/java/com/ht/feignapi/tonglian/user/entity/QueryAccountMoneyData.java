package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryAccountMoneyData implements Serializable {

    private String tel;

    private String icCardId;

    private String merchantCode;

    private String authCode;

    private String userFlagCode;
}
