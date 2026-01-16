package com.ht.user.card.common;

import lombok.Getter;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/4 14:18
 */
public enum CardType {
    COUPON("coupon","优惠券"),
    MONEY("money","现金券"),
    DISCOUNT("discount","折扣券"),
    NUMBER("number","计次券");

    @Getter
    private String key;

    @Getter
    private String desc;

    CardType(String key, String desc){
        this.key = key;
        this.desc = desc;
    }
}
