package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetScanQrPayData implements Serializable {

    /**
     * 渠道号 限微信交易响应
     */
    private String chnlid;

    /**
     * 随机字符串
     */
    private String randomstr;

    /**
     * 交易类型
     */
    private String trxcode;

    /**
     * 手续费
     */
    private String fee;

    /**
     * 签名
     */
    private String sign;

    /**
     * 渠道号  限微信交易响应
     */
    private String cmid;

    /**
     * 交易单号 收银宝平台的交易流水号
     */
    private String trxid;

    /**
     * 交易状态 交易的状态,对于刷卡支付，该状态表示实际的支付结果，其他为下单状态  0000为交易成功
     */
    private String trxstatus;

    /**
     * 借贷标识  00-借记卡 02-信用卡 99-其他（花呗/余额等）
     */
    private String accttype;

    /**
     * 商户交易单号  商户的交易订单号
     */
    private String reqsn;

    /**
     * 渠道平台交易单号 例如微信,支付宝平台的交易单号
     */
    private String chnltrxid;

    /**
     * 交易完成时间 yyyyMMddHHmmss
     */
    private String fintime;

    /**
     * 商户号 平台分配的商户号
     */
    private String cusid;

    /**
     * 应用ID  平台分配的APPID
     */
    private String appid;

    /**
     * 原交易金额  与请求字段trxamt值一致
     */
    private String initamt;

    /**
     * 支付平台用户标识 微信支付-用户的微信openid 支付宝支付-用户user_id 	如果为空,则默认填000000
     */
    private String acct;

    /**
     * 实际交易金额
     */
    private String trxamt;

    /**
     * 返回码 SUCCESS/FAIL
     */
    private String retcode;

    /**
     * 错误响应
     */
    private String errmsg;
}
