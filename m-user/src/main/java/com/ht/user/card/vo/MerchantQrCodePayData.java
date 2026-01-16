package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MerchantQrCodePayData implements Serializable {

    /**
     * 支付金额 单位分
     */
    private Integer amount;

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 支付类型 (收款)
     */
    private String payType;

    /**
     * 支付环境(微信,支付宝)
     */
    private String paySource;
}
