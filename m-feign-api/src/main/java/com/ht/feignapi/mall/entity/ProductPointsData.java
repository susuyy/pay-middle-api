package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProductPointsData implements Serializable {

    /**
     * 具体门店编码
     */
    private String storeMerchantCode;

    /**
     * 商品编码
     */
    private String productionCode;

    /**
     * 商品名称
     */
    private String productionName;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 数量
     */
    private Integer quantity;
}
