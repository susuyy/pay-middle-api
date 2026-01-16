package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CalculationMoneyData implements Serializable {

    /**
     * 用户手机号
     */
    private String openid;

    /**
     * 会员卡卡号
     */
    private String icCardId;

    /**
     * 原始的支付金额
     */
    private Integer amount;

    /**
     * 用户选择的使用卡券
     */
    private List<PosSelectCardNo> cardNoList;

    /**
     * 商户编码
     */
    private String merchantCode;

}
