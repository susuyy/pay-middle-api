package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PayOrderData implements Serializable {
    /**
     * 回调地址
     */
    private String returl;

    /**
     * 订单号 (前端提交)
     */
    private String orderCode;

    /**
     * 商户编码 具体购买 商品的门店编码 (前端提交)
     */
    private String merchantCode;

    /**
     * 使用的优惠券 标识 编码 (前端提交)
     */
    private List<String> cardNoList;

    /**
     * 金额
     */
    private Integer trxamt;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * appid
     */
    private String appId;

    /**
     * MD5Key
     */
    private String MD5Key;

    /**
     * 用户openId
     */
    private String acct;

    /**
     * MD5Key
     */
    private String sub_appid;

//    /**
//     * 使用积分 数量 (前端提交)
//     */
//    private Integer usePointAmount;

}
