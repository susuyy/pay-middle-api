package com.ht.feignapi.tonglian.merchant.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrhMerchantData implements Serializable {

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 机构号
     */
    private String brhMerchantCode;
}
