package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RefundListData implements Serializable {

    /**
     * 退款列表
     */
    private List<OrderOrderDetails> orderOrderDetailsList;

    /**
     * 退款原因
     */
    private String refundMessage;

    /**
     * 联系电话
     */
    private String tel;

}
