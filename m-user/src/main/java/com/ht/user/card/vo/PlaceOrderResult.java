package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlaceOrderResult implements Serializable {

    private String orderCode;

    /**
     * 金额：默认单位：分
     */
    private Integer amount;

    /**
     * 折扣：默认单位：分
     */
    private Integer discount;

    /**
     * 响应信息
     */
    private String message;

}
