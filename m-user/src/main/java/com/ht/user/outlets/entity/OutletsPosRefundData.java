package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutletsPosRefundData implements Serializable {

    /**
     * 原交易订单号
     */
    private String orderCode;

    /**
     * 操作人员标识
     */
    private String operator;

    /**
     * 操作人员标识
     */
    private String posSerialNumber;


    /**
     * 单号
     */
    private String refundCode;

    /**
     * 退款金额 单位分
     */
    private String refundAmount;


}
