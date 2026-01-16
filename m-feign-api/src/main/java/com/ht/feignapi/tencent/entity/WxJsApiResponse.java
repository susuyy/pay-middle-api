package com.ht.feignapi.tencent.entity;

import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2020/11/4 15:33
 */
@Data
public class WxJsApiResponse {
    private Integer errcode;
    private String errmsg;
    private String ticket;
    private Long expires_in;
}
