package com.ht.feignapi.higo.entity;

import com.ht.feignapi.mall.entity.OrderOrderDetails;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShowPayOrderMessage implements Serializable {

    private Merchants merchants;

    private List<OrderOrderDetails> orderOrderDetailsList;
}
