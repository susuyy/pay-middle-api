package com.ht.feignapi.higo.constant;

import java.util.HashMap;
import java.util.Map;

public class WxConstant {

    public static final String appid = "wx45ddf45c5520e5dc";
    public static final String secret = "324cae27b61abde9038c93e50717cf01";

    public static final String AES = "AES";
    public static final String AES_CBC_PADDING = "AES/CBC/PKCS7Padding";


    /**
     * 微信 错误与信息
     */
    public static Map<String,String> WX_CODE_MSG_MAP = new HashMap<String,String>(){
        {
            put("-1","系统繁忙，此时请开发者稍候再试");
            put("40029","code 无效");
            put("45011","频率限制，每个用户每分钟100次");
            put("40226","高风险等级用户，小程序登录拦截 。风险等级详见用户安全解方案");
        }
    };
}
