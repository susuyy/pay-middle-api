package com.ht.feignapi.mall.constant;

public class OrderConstant {

    public static final String SHOP_TYPE = "mall_shop";

    //unpaid未付款  un_use待使用  used已完成 invalid失效
    public static final String UNPAID_STATE = "unpaid";

    public static final String PAID_UN_USE_STATE = "paid_un_use";

    public static final String PAID_USED_STATE = "paid_used";

    public static final String INVALID_STATE = "invalid";

    public static final String ALL_STATE = "all";

    public static final String NORMAL = "normal";

    /**
     * 退款中
     */
    public static final String REFUND_ING = "refund_ing";

    /**
     * 已退款
     */
    public static final String REFUND = "refund";

    /**
     * 退款失败
     */
    public static final String REFUND_NOT = "refund_not";
}
