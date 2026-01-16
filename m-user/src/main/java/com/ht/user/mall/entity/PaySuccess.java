package com.ht.user.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PaySuccess implements Serializable {


    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 用户userId
     */
    private String userId;

    /**
     * 通联的支付商户订单号 (通联)
     */
    private String payCode;

    /**
     * 支付时间
     */
    private String payDate;

    /**
     * 验证token
     */
    private String token;

    /**
     * 商户编码
     */
    private String merchantCode;

}
