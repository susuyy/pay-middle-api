package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SettlementMoneyData implements Serializable {

    /**
     * 核算金额
     */
    private Integer amount;

    /**
     * 是否需要继续支付,跳转微信或支付宝标识
     */
    private Boolean isToPay;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 提示信息
     */
    private Boolean msgFlag;

    /**
     * 用户余额
     */
    private Integer userAccount;

    /**
     * 用户余额 扣除后剩下
     */
    private Integer afterUserAccount;

    /**
     * 卡券扣除余额
     */
    private Integer cardDiscountMoney;
}
