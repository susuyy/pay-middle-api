package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PayOrderData implements Serializable {
    /**
     * 同步回调地址
     */
    private String returl;

    /**
     * 异步回调地址
     */
    private String notifyUrl;

    /**
     * 订单号
     */
    private String orderCode;


    /**
     * 金额
     */
    private Integer trxamt;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 商品描述
     */
    private String paytype;




}
