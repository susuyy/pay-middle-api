package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRefundData implements Serializable {

    /**
     * 通联商户号
     */
    private String cusid;

    /**
     * 通联appid
     */
    private String appid;

    /**
     * 系统平台商户编码
     */
    private String merchantCode;

    /**
     * 退款金额
     */
    private long trxamt;

    /**
     * 退款单号 唯一标识
     */
    private String reqsn;

    /**
     * 原订单号
     */
    private String oldreqsn;
}
