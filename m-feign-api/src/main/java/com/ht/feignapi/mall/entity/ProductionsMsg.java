package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductionsMsg implements Serializable {

    /**
     * 商品编码
     */
    private String productionCode;

    /**
     * 商品分类编码
     */
    private String productionCategoryCode;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 具体的商铺编码
     */
    private String subMerchantCode;

}
