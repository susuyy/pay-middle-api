package com.ht.feignapi.tonglian.config;

/**
 * 调取通联支付接口 参数数据
 *
 * @author suyangyu
 * @since 2020-06-09
 */
public class TongLianCardConfig {

    /**
     * APP_SECRETE
     */
    public static final String APP_SECRETE = "test";

    /**
     * APP_KEY
     */
    public static final String APP_KEY = "test";

    /**
     * 数据串秘钥
     */
    public static final String DATA_SECRET = "abcdefgh";

    /**
     * 开卡 密码  默认
     */
    public static final String PASSWORD = "111111";

    /**
     * 机构号
     */
    public static final String BRH_ID = "0229000040";

    /**
     * 品牌号
     */
    public static final String BRAND_NO = "0030";


    /**
     * chan_no
     */
    public static final String CHAN_NO = "4000000001";

    /**
     * 请求地址
     */
    public static final String URL_TEST = "http://116.228.64.55:8080/aop/rest";

    /**
     * 请求地址(正式)
     */
    public static final String URL_PROD = "https://prcs.allinpay.com/aop/rest";

    /**
     * 系统参数 格式
     */
    public static final String FORMAT = "json";


    /**
     * 系统参数 签名方法
     */
    public static final String SIGN_METHOD = "MD5";

    /**
     * 系统参数 接口名称 开卡
     */
    public static final String OPEN_METHOD = "allinpay.ppcs.cloud.card.open";



    /**
     * 系统参数 账户余额调整
     */
    public static final String ADJDTL_METHOD = "allinpay.ppcs.adjdtl.add";

    /**
     * 系统参数 卡产品绑定
     */
    public static final String BIND_PRODUCT = "allinpay.ppcs.bindproduct.add";


    /**
     * 系统参数 接口名称 查询卡信息
     */
    public static final String QUERY_CARD_INFO_METHOD = "allinpay.ppcs.cardinfo.get";


    /**
     * 系统参数 接口名称 金额增加
     */
    public static final String CARD_SINGLE_TOP_UP_ADD = "allinpay.ppcs.cloud.cardsingletopup.add";



    /**
     * 系统参数 接口名称 金额支付
     */
    public static final String PAY_WITH_PASSWORD = "allinpay.card.cloud.paywithpassword.add";

    /**
     * 版本号
     */
    public static final String VERSION = "1.0";


    /**
     * 产品号
     */
    public static final String PRDT_NO = "0001";

    /**
     * 充值途径
     */
    public static final String TOP_UP_WAY = "1";

    /**
     * 商户号
     */
    public static final String MER_ID = "999990053990001";

}
