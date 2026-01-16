package com.ht.feignapi.tencent.entity;

import lombok.Data;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/11/4 10:39
 */
@Data
public class WxOfficialJSJDKConfig {
    //公众号的唯一标识
    private String appId;

    //生成签名的时间戳
    private String timestamp;

    //生成签名的随机串
    private String nonceStr;

    //签名
    private String signature;

    //需要使用的JS接口列表
    private String[] jsApiList;
}
