package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PushOutletsPosOrderCloseData implements Serializable {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 限制支付方式
     */
    private String state;
}
