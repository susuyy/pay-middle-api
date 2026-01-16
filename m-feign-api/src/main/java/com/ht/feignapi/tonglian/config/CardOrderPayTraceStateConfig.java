package com.ht.feignapi.tonglian.config;

public class CardOrderPayTraceStateConfig {


    /**
     * 待支付
     */
    public static final String UNPAID = "unpaid";
    /**
     * 已支付
     */
    public static final String PAID = "paid";
    /**
     * 退款中
     */
    public static final String REFUNDING = "refunding";

    /**
     * 已退款
     */
    public static final String REFUND = "refund";

    /**
     * 可继续支付,等待支付
     */
    public static final String PAID_HOLD = "payhold";


    /**
     * 已退款
     */
    public static final String CANCEL = "cancel";
}
