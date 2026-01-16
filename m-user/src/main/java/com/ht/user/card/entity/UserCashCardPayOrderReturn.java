package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserCashCardPayOrderReturn implements Serializable {

    /**
     * 是否需要去支付
     */
    private Boolean isToPay;

    /**
     * 仍需支付金额
     */
    private Integer amount;

    /**
     * 使用卡券信息
     */
    private String useCardMessage;

    /**
     * 使用卡券标识
     */
    private Boolean useCardFlag;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 账户余额
     */
    private Integer userAccount;
}
