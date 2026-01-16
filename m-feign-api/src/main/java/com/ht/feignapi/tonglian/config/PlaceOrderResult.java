package com.ht.feignapi.tonglian.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlaceOrderResult implements Serializable {

    private String orderCode;

    /**
     * 金额：默认单位：分
     */
    private Integer amount;

    /**
     * 折扣：默认单位：分
     */
    private Integer discount;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 是否需要去支付
     */
    private Boolean isToPay;

    /**
     * 账户余额
     */
    private Integer userAccount;

    /**
     * 使用卡券信息
     */
    private String useCardMessage;

    /**
     * 使用卡券标识
     */
    private Boolean useCardFlag;

}
