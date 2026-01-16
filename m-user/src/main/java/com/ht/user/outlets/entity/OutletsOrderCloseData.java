package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutletsOrderCloseData implements Serializable {

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 操作人员标识
     */
    private String operator;

    /**
     * 商户号
     */
    private String merchantCode;


}
