package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WayBillsAndCouponMoney implements Serializable {

    /**
     * 优惠券 cardNo 集合
     */
    private List<String> cardNoList;

    /**
     * 购物车 编号 orderCode
     */
    private List<String> shoppingCartOrderCodeList;

    /**
     * 派送方式
     */
    private String orderWayBillsType;

    //以下 直接购买 计算派送运单费使用

    /**
     * 直接购买使用 字段 商品编码
     */
    private String productionCode;

    /**
     * 商品品类编码
     */
    private String categoryCode;

    /**
     * 具体门店编码
     */
    private String storeMerchantCode;

    /**
     * 数量
     */
    private Integer quantity;


}
