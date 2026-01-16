package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetCheckRefund implements Serializable {

    private List<RefundCardDetail> refundCardDetails;

    private boolean checkFlag;
}
