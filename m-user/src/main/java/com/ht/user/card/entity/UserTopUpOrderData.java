package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserTopUpOrderData implements Serializable {

    private String openId;

    private int amount;

    private Long userId;

    private String objectMerchantCode;
}
