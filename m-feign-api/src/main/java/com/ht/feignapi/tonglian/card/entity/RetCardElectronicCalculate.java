package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetCardElectronicCalculate implements Serializable {

    private int oriCardAmount;

    private int afterCardAmount;

    private int payAmount;
}
