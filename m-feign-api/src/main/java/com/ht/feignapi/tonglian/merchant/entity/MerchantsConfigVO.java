package com.ht.feignapi.tonglian.merchant.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
@Data
public class MerchantsConfigVO implements Serializable {


    /**
     * 商家编码
     */
    private String merchantCode;

    private String vipGolden;
    private String vipPlatinum;
    private String vipCrown;
    private String appId;
    private String md5key;
    private String mchId;
    private String wxAppid;
    private String wxAppsecret;
    private String chargeType;
    private String payType;
    private String rsaPublic;
    private String rsaPrivate;
    private String c;

    /**
     * 商家配置
     */
    private String key;

    /**
     * 商家配置值
     */
    private String value;



}
