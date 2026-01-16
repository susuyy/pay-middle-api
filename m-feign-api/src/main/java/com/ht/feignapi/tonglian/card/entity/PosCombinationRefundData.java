package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PosCombinationRefundData implements Serializable {

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 操作人员标识
     */
    private String operator;

    /**
     * 商户号
     */
    private String merchantCode;

    /**
     * 商户退款单号 , 海旅上送
     */
    private String refundCode;

    /**
     * 退款金额 单位分
     */
    private int refundAmount;

    /**
     * 流水号
     */
    private String payTraceNo;

    /**
     * 退款密码
     */
    private String refundPassword;


}
