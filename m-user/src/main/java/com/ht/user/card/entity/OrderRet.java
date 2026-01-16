package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRet implements Serializable {

    private String orderCode;

    private Long orderDetailId;

    private Long payTraceId;
}
