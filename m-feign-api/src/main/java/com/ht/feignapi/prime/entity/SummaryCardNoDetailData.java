package com.ht.feignapi.prime.entity;

import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import lombok.Data;

import java.io.Serializable;

@Data
public class SummaryCardNoDetailData implements Serializable {

    private CardOrderDetails cardOrderDetails;

    private String batchCode;

    private Long faceValue;

    private String cardType;
}
