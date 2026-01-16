package com.ht.user.config;

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
     * 撤销
     */
    public static final String CANCEL = "cancel";

    /**
     * 订单关闭
     */
    public static final String CLOSE = "close";
}
