package com.ht.feignapi.tonglian.merchant.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterBrhMerchantData implements Serializable {

    /**
     * 合作机构名称
     */
    private String merchantName;

    /**
     * 合作机构地址
     */
    private String location;

    /**
     * 主体或是商户
     */
    private String type;

    /**
     * 主体编码
     */
    private String businessSubjects;

    /**
     * 合作机构联系方式 (电话)
     */
    private String merchantContact;

    /**
     * 注册账号
     */
    private String account;

    /**
     * 注册密码
     */
    private String password;

}
