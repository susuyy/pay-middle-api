package com.ht.feignapi.mall.entity;

import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserCardDiscountData implements Serializable {

    /**
     * 折扣金额
     */
    private Integer discount;

    /**
     * 使用的优惠券对象
     */
    private CardMapUserCards cardMapUserCards;
}
