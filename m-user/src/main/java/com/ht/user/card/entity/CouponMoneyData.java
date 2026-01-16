package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CouponMoneyData implements Serializable {

    /***
     * 卡编号
     */
    private String cardNo;

    /**
     * 优惠的金额  单位分 , 正数
     */
    private Integer couponMoney;
}
