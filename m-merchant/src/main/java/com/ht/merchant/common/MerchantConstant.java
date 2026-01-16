package com.ht.merchant.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zheng weiguang
 * @Date: 2021/1/4 10:44
 */
public class MerchantConstant {

    public static final String OBJECT_TYPE = "OBJECT";
    public static final String MERCHANT_TYPE = "MERCHANT";

    public static final Map<String,String> merchantNameMap = new HashMap<>();
    static{
        merchantNameMap.put("HLMSD","海旅免税");
        merchantNameMap.put("THSZ","通华");
        merchantNameMap.put("HIGO","嗨go");
        merchantNameMap.put("YZT","壹账通");

    }
}
