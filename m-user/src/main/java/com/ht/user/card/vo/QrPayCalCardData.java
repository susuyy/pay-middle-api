package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QrPayCalCardData implements Serializable {

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 支付金额
     */
    private String amount;

    /**
     * 商户编码
     */
    private String merchantCode;


}
