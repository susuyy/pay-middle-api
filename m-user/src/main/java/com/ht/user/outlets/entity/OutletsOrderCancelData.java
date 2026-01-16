package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutletsOrderCancelData implements Serializable {

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 操作人员标识
     */
    private String operator;


    /**
     * 商户撤销单号
     */
    private String cancelCode;


//    /**
//     * 商户退款单号
//     */
//    private String refundCode;


}
