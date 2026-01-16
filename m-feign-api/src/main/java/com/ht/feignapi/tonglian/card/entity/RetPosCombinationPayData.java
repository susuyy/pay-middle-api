package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetPosCombinationPayData implements Serializable {

    /**
     * 订单状态
     */
    private String state;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 已核销金额
     */
    private Integer paidAmount;

    /**
     * 仍需支付金额
     */
    private Integer needPaidAmount;
}
