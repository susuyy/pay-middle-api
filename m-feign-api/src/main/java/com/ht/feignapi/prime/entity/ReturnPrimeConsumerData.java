package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReturnPrimeConsumerData implements Serializable {

    /**
     * 消费标识 true成功 false失败
     */
    private boolean consumerFlag;

    /**
     * 消费掉的金额 扣除的金额 (单位分)
     */
    private Integer usedAmount;

    /**
     * 扣除后 剩余的用户金额 (单位分)
     */
    private Integer afterUserMoney;

    /**
     * 返回的订单号(流水号,唯一标识)
     */
    private String merOrderId;
}
