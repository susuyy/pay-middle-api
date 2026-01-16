package com.ht.feignapi.tongshangyun.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseAgentCollectApplyData implements Serializable {

    /**
     * 支付跳转页面地址
     */
    private String payUrl;
}
