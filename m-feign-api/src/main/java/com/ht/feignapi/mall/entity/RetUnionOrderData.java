package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetUnionOrderData implements Serializable {

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 是否去支付 前端判断是否直接调用支付接口
     */
    private Boolean isToPay;

    /**
     * 下单返回信息
     */
    private String unionOrderMessage;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 是否去绑定手机
     */
    private Boolean toBindTel;
}
