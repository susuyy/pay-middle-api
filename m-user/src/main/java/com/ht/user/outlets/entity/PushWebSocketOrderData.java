package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PushWebSocketOrderData implements Serializable {

    private String type;

    /**
     * 订单 支付 数据
     */
    private ResponseOutletsPosOrderData responseOutletsPosOrderData;

    /**
     * 订单 关闭 数据
     */
    private PushOutletsPosOrderCloseData pushOutletsPosOrderCloseData;
}
