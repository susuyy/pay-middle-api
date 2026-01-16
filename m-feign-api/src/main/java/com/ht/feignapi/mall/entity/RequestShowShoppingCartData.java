package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestShowShoppingCartData implements Serializable {

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 用户openId
     */
    private String openId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 分页数据 当前页
     */
    private Integer pageNo;

    /**
     * 分页数据 每页数据量
     */
    private Integer pageSize;
}
