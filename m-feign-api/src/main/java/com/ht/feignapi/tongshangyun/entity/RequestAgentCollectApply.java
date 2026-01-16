package com.ht.feignapi.tongshangyun.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestAgentCollectApply implements Serializable {

    /**
     * 金额,单位分
     */
    private Long amount;

    /**
     * 收款方的 bizUserId
     */
    private String collectionBizUserId;

    /**
     * 支付方的 bizUserId
     */
    private String payerId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品描述
     */
    private String goodsDesc;

    /**
     * 商户订单编码
     */
    private String orderCode;
}
