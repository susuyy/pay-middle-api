package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RefundCardDetail implements Serializable {

    private String CardNo;

    private Long refundAmount;
}
