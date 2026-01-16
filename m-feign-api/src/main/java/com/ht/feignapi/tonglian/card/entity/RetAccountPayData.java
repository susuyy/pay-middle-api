package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetAccountPayData implements Serializable {

    private String orderCode;

    private Integer amount;
}
