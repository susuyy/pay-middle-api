package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CardConsumeDetails implements Serializable {

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 卡支付金额
     */
    private int cardPaidAmount;

    /**
     * 主体 电子卡
     */
    private CardElectronic cardElectronic;

    /**
     * 合作机构电子卡
     */
    private PartyCardElectronic partyCardElectronic;

    /**
     * 使用类型
     */
    private String partyOrSelf;
}
