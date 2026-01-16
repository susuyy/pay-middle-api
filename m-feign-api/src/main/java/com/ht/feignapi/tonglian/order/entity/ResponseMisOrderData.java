package com.ht.feignapi.tonglian.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseMisOrderData implements Serializable {

    /**
     * 业务编码
     */
    private String businessId;

    /**
     * 金额 单位分
     */
    private Integer amount;

    /**
     * 订单编号
     */
    private String orderNo;
}
