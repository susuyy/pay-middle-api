package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShowMyWayBillsData implements Serializable {

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
     * 派送单状态 un_delivered 未发货  delivered 已发货
     */
    private String state;
}
