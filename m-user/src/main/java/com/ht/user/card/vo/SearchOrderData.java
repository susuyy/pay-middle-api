package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchOrderData implements Serializable {

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 终端号
     */
    private String posSerialNum;

    /**
     * 支付来源
     */
    private String paySource;

    /**
     * 用户手机号
     */
    private String userTel;

    /**
     * 收银员
     */
    private String sale;

    /**
     * 金额范围
     */
    private String amountScope;

    /**
     * 订单来源
     */
    private String orderSource;

    /**
     * 订单状态
     */
    private String orderType;

    /**
     * 订单状态
     */
    private String orderState;

    /**
     * 提交时间
     */
    private String timeScope;



}
