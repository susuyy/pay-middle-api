package com.ht.feignapi.tencent.entity;

import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2020/11/4 10:55
 */
@Data
public class WxAccessToken {
    private String accessToken;
    private String expiresIn;
}
