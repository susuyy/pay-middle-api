package com.ht.user.card.entity;

import lombok.Data;
import org.apache.ibatis.annotations.Delete;

import java.io.Serializable;

@Data
public class RequestQueryOrderData implements Serializable {

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 订单类型
     */
    private String type;

    /**
     * 订单类型
     */
    private String state;

    /**
     * 分页参数 当前页码
     */
    private Integer pageNo;

    /**
     * 每页数据量
     */
    private Integer pageSize;
}
