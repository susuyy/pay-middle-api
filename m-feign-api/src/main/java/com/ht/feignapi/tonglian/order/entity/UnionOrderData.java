package com.ht.feignapi.tonglian.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnionOrderData implements Serializable {
    /**
     * 回调地址
     */
    private String returl;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 商户编码 具体购买 商品的门店编码
     */
    private String merchantCode;

    /**
     * 金额
     */
    private Integer trxamt;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * appid
     */
    private String appId;

    /**
     * MD5Key
     */
    private String MD5Key;
}
