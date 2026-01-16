package com.ht.feignapi.tonglian.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MisOrderCouponData implements Serializable {

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 优惠券卡号
     */
    private String cardNo;

    /**
     * 订单金额
     */
    private Integer amount;



}
