package com.ht.feignapi.tonglian.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CuspayData implements Serializable {

    /**
     * 金额
     */
    private String amt;

    /**
     * 订单编号
     */
    private String oid;

    /**
     * 二维码数据
     */
    private String c;

    /**
     * 同步回调地址
     */
    private String returl;

    /**
     * 通联分配的appID
     */
    private String appId;

    /**
     * sign 签名
     */
    private String sign;

    /**
     * signtype 签名类型
     */
    private String signType;

    /**
     * 商户编码
     */
    private String merchantCode;
}
