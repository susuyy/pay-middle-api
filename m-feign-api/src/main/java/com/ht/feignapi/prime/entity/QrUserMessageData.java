package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QrUserMessageData implements Serializable {

    /**
     *
     * 用户标识 openId
     */
    private String openId;

    /**
     * 用户账户余额标识
     */
    private String userAccountFlagCode;

    /**
     * 二维码时效性验证码
     */
    private String qrAuthCode;
}
