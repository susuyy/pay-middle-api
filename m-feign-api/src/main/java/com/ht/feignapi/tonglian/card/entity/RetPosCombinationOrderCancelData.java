package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetPosCombinationOrderCancelData implements Serializable {

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 商户号
     */
    private String merchantCode;

    /**
     * 商户退款单号
     */
    private String refundCode;


    /**
     * 操作人
     */
    private String operator;

    /**
     * 状态
     */
    private String state;

}
