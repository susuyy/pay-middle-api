package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class PosCombinationPay implements Serializable {

    /**
     * 用户标识
     */
    private String userFlagCode;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 金额 单位分
     */
    private Integer amount;

    /**
     * 商户编码
     */
    private String merchantCode;
}
