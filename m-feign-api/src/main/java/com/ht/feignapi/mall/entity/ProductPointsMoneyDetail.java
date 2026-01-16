package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductPointsMoneyDetail implements Serializable {

    private String productionCode;

    private String productionName;

    private int usePoints;

    private int reduceMoney;
}
