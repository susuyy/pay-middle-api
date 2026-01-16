package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadOrderDetails implements Serializable {

    /**
     * 柜组编码
     */
    private String goodsGroupCode;

    /**
     * 品类编码
     */
    private String categoryCode;

    /**
     * 品牌编码
     */
    private String brandCode;

    /**
     * 商品编码
     */
    private String goodsCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品件数
     */
    private String goodsCount;

    /**
     * 商品价格
     */
    private String goodsPrice;

    /**
     * 商品优惠金额
     */
    private String goodsDiscount;

    /**
     * 商品实付金额
     */
    private String goodsPayPrice;

    /**
     * 商品促销活动类型
     */
    private String goodsActivityType;



}
