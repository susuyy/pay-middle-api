package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserPlaceOrderData implements Serializable {


    /**
     * 订单类型
     */
    private String type;


    /**
     * 商户编号
     */
    private String merchantCode;

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 数量
     */
    private Integer quantity;

//    /**
//     * 金额：默认单位：分
//     */
//    private Integer amount;
//
//    /**
//     * 折扣：默认单位：分
//     */
//    private Integer discount;

//    /**
//     * 订单描述
//     */
//    private String comments;

    /**
     * 商品名称
     */
    private String productionName;

    /**
     * 商品编码
     */
    private String productionCode;

    /**
     * 批次号
     */
    private String batchCode;
}
