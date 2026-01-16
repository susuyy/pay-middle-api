package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetSettlement implements Serializable {

    private Long userId;

    private SettlementMoneyData settlementMoneyData;
}
