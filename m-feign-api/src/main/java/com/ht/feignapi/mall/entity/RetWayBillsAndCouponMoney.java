package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class RetWayBillsAndCouponMoney implements Serializable {

    /**
     * 总运费
     */
    private Double totalWayBillsMoney;


    /**
     * 商家编码 对应 商家运费
     */
    private Map<String, Double> merchantWayBillMoneyMap;


}
