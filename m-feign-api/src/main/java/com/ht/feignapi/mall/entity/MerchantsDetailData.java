package com.ht.feignapi.mall.entity;

import com.ht.feignapi.tonglian.merchant.entity.Merchants;

import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import lombok.Data;

import java.io.Serializable;

@Data
public class MerchantsDetailData implements Serializable {

    /**
     * 门店 商户信息
     */
    private Merchants merchants;


    /**
     * 购买的商品名称
     */
    private String productionsName;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 合计价格 金额 分
     */
    private Integer amount;




}
