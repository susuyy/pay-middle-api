package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutletsRefundData implements Serializable {

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 操作人员标识
     */
    private String operator;


    /**
     * 商户退款单号 , 对接方上送
     */
    private String refundCode;

    /**
     * 退款金额 单位分
     */
    private String refundAmount;


    /**
     * 退款密码
     */
    private String refundPassword;


}
