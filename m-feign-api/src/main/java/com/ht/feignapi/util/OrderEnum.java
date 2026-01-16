package com.ht.feignapi.util;

/**
 * suyangyu
 */

public enum OrderEnum {
    ShopOrder("SP"),//购物订单
    RefundOrder("RF"),//退款订单
    WayBillOrder("WB"),//派送订单
    ShoppingCartOrder("SC");//购物车订单



    // 成员变量
    private String category;


    OrderEnum(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
