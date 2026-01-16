package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PosCombinationPaySuccess implements Serializable {

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 商户号
     */
    private String merchantCode;

    /**
     * 支付金额
     */
    private String amount;

    /**
     * 用户 标识码
     */
    private String userFlagCode;

    /**
     * 富基上送payCode
     */
    private String payCode;
}
