package com.ht.feignapi.prime.entity;

import com.ht.feignapi.tonglian.card.limit.LimitStrategy;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SummaryCardNoData implements Serializable {

    private CardOrderPayTrace cardOrderPayTrace;

    private String cardNo;

    private String cardType;
}
