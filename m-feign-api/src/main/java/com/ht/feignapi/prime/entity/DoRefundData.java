package com.ht.feignapi.prime.entity;

import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class DoRefundData implements Serializable {

    private CardOrdersVO cardOrdersVO;

    private RefundCardDetail refundCardDetail;
}
