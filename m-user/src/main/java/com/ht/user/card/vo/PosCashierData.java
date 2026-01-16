package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PosCashierData implements Serializable {

    /**
     * 核算后的金额
     */
    private Integer amount;


    /**
     * 用户的卡券列表
     */
    private List<PosUserCardVO> posUserCardVOS;

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
