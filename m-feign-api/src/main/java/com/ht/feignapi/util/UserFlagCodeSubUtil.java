package com.ht.feignapi.util;

import com.ht.feignapi.prime.entity.QrUserMessageData;

public class UserFlagCodeSubUtil {

    /**
     * 通联扫码 用户标识码 截取
     * @param userFlagCode
     * @return
     */
    public static QrUserMessageData userFlagCodeSub(String userFlagCode){

        //二维码 时效性验证码
        String qrAuthCode = userFlagCode.substring(31, 34);

        //用户账户余额标识
        String userAccountFlagCode = userFlagCode.substring(28, 31);

        //用户标识 openId
        String openId = userFlagCode.substring(0,userFlagCode.length() - 6);
        QrUserMessageData qrUserMessageData = new QrUserMessageData();
        qrUserMessageData.setOpenId(openId);
        qrUserMessageData.setUserAccountFlagCode(userAccountFlagCode);
        qrUserMessageData.setQrAuthCode(qrAuthCode);
        return qrUserMessageData;
    }

    public static void main(String[] args) {
        QrUserMessageData qrUserMessageData = userFlagCodeSub("ojnfgs1lO0I6Kgh125OpmyF77VpIHHA123");
        System.out.println(qrUserMessageData);
    }
}
