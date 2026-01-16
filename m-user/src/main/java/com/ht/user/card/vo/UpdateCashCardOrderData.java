package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateCashCardOrderData implements Serializable {

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 用户openid
     */
    private String openid;

    /**
     *  商户编码
     */
    private String merchantCode;

    /**
     * 金额 单位分
     */
    private Integer amount;



}
