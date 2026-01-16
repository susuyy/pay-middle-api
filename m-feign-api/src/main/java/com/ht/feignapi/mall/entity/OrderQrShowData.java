package com.ht.feignapi.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderQrShowData implements Serializable {

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 手机号
     */
    private String tel;

    /**
     * 付款时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date payTime;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 订单总价
     */
    private Integer amount;

    /**
     * 促销优惠
     */
    private Integer discount;

    /**
     * 实际付款
     */
    private Integer actualPayment;
}
