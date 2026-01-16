package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReturnWayBillDetail implements Serializable {

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 单派送费
     */
    private Integer wayBillFee;
}
