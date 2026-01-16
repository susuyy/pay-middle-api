package com.ht.feignapi.mall.constant;

/**
 * @Author: Liwg
 * @Date: 2020/9/4 10:23
 */
public class MallConstant {
    public static final String MODELTYPE = "modelType";
    public static final String PRODUCTION = "PRODUCTION";
    public static final String SIZE = "size";
    public static final String SHOP = "SHOP";

    public static final String REDIS_HOST = "r-wz94xv6hwoo9gxxwmjpd.redis.rds.aliyuncs.com";
    public static final Integer REDIS_PORT = 6379;
    public static final String REDIS_PASSWORD = "p_liwengang:lC1LrGnMCwbV";
    public static final Integer REDIS_TIMEOUT = 10000;

    /**
     * 商品状态，1为启用，0为禁用
     */
    public static final String MALL_PRODUCTION_ENABLE = "1";

    public static final String MALL_PRODUCTION_DISABLE = "0";
    /**
     * 库存为空，或者特价到期
     */
    public static final Integer SOLD_OUT = 999;
    public static final String SOLD_OUT_STR = "soldOut";
    public static final String NORMAL = "normal";
}
