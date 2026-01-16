package com.ht.feignapi.util;

public class UserCodeAuthUtil {

    /**
     * 获取用户 支付码的 过期验证码
     *
     * @param userCode
     * @return
     */
    public static String getAuthCode(String userCode){
        return userCode.substring(userCode.length()-3);
    }

    /**
     * 获取用户的真实openId
     * @param userCode
     * @return
     */
    public static String getRealOpenId(String userCode){
        return userCode.substring(0, userCode.length() - 3);
    }

    /**
     * 获取用户的真实openId
     * @param userCode
     * @return
     */
    public static String getRealOpenIdFree(String userCode){
        return userCode.substring(0, userCode.length() - 7);
    }

}
