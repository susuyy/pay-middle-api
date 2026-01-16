package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

@Data
public class RetCalculationData {

    private Integer amount;

    private Integer cardDiscountMoney;

    private Long userId;

    private int oriUserMoneyInt;
}
