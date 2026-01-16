package com.ht.feignapi.prime.entity;

import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import lombok.Data;

import java.io.Serializable;

@Data
public class SummaryCardNoRefundData implements Serializable {

    private CardRefundOrder cardRefundOrder;

    private String cardNo;

    private String cardType;
}
