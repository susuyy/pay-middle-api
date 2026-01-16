package com.ht.feignapi.tonglian.utils;

public class RequestQrCodeDataStrUtil {

    /**
     * 获取完整卡号
     * @param qrCodeDataStr
     * @return
     */
    public static String subStringQrCodeData(String qrCodeDataStr) {
        String qrCodeDataSubStr = qrCodeDataStr.substring(0, qrCodeDataStr.length() - 4);
        return qrCodeDataSubStr;
    }

    /**
     * 获取后四位验证编号
     * @param qrCodeDataStr
     * @return
     */
    public static String subStringQrCodeAuthCode(String qrCodeDataStr) {
        String qrCodeDataSubStr = qrCodeDataStr.substring(qrCodeDataStr.length() - 4);
        return qrCodeDataSubStr;
    }


}
