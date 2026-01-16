package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReturnCheckInventoryMsg implements Serializable {

    /**
     * 库存是否满足
     */
    private boolean hasInventory;

    /**
     * 封装 库存不足的商品名称
     */
    private String message;
}
