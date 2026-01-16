package com.ht.feignapi.tonglian.config;

public class CardOrdersTypeConfig {
    /**
     * 全部
     */
    public static final String ALL = "all";

    /**
     * 购物订单
     */
    public static final String SHOP = "shop";
    /**
     * 消费订单
     */
    public static final String CONSUME = "consume";

    /**
     * 购卡订单
     */
    public static final String PRIME_BUY_CARD = "prime_buy_card";

    /**
     * pos端用户充值
     */
    public static final String POS_USER_TOP_UP = "pos_top_up";

    /**
     * C端扫码下单
     */
    public static final String QR_PAY = "qr_pay";

    /**
     * pos端收银
     */
    public static final String POS_CASH = "pos_cash";

    /**
     * pos端会员收银余额支出
     */
    public static final String POS_ACCOUNT_PAY = "pos_account_pay";

    /**
     * pos端组合支付
     */
    public static final String POS_VIP_PAY = "pos_vip_pay";


    /**
     * 云mis订单
     */
    public static final String POS_MIS_ORDER = "pos_mis_order";

}
