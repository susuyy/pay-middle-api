package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetCardElectronicPosCashier implements Serializable {

    private Long userId;

    private PosCashierData posCashierData;
}
