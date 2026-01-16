package com.ht.user.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

public class RequestQrCodeDataStrUtil {

    public static String subStringQrCodeData(String qrCodeDataStr) {
        String qrCodeDataSubStr = qrCodeDataStr.substring(0, qrCodeDataStr.length() - 4);
        return qrCodeDataSubStr;
    }
}
