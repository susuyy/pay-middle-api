package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class WXData implements Serializable {

    private String accessToken;

    private String openid;

    private String errCode;

    private String errMsg;

    private String refreshToken;

    /**
     * access_token过期时间
     */
    private String expiresIn;
}
