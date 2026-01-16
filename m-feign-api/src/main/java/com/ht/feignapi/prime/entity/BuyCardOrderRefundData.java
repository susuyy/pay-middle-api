package com.ht.feignapi.prime.entity;

import lombok.Data;
import org.apache.ibatis.annotations.Delete;

import java.io.Serializable;
import java.util.List;

@Data
public class BuyCardOrderRefundData implements Serializable {

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 操作员
     */
    private String operator;

    /**
     * 卡号
     */
    private List<String> cardNoList;
}
