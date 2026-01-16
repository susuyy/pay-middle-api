package com.ht.feignapi.mall.entity;

import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetStatementDiscountData implements Serializable {

    /**
     * 使用标识
     */
    private boolean useFlag;

    /**
     * 信息
     */
    private String message;

    /**
     * 优惠金额
     */
    private Integer discount;

    /**
     * 使用的优惠券
     */
    private List<CardMapUserCards> cardMapUserCardsList;

    /**
     * 优惠 数据封装
     */
    private List<UserCardDiscountData> userCardDiscountDataList;
}
