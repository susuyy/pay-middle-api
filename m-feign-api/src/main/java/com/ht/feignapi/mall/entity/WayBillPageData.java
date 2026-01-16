package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WayBillPageData implements Serializable {

    /**
     * 派送单数据
     */
    private OrderWayBills orderWayBills;

    /**
     * 商品详情集合
     */
    private List<OrderOrderDetails> orderOrderDetailsList;

    /**
     * 订单数据
     */
    private OrderOrders orderOrders;

}
