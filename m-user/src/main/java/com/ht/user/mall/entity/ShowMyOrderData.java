package com.ht.user.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShowMyOrderData implements Serializable {

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * openid
     */
    private String openId;

    /**
     * 分页数据 pageNo 当前页
     */
    private Integer pageNo;

    /**
     * 分页数据 pageSize 每页大小
     */
    private Integer pageSize;

    /**
     * 订单状态 all全部  unpaid未付款  un_use待使用  used已完成 invalid失效
     */
    private String state;
}
