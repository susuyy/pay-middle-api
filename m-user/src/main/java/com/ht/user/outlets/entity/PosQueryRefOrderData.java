package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PosQueryRefOrderData implements Serializable {

    /**
     * 金额
     */
    private String amount;


    /**
     * 交易类型
     */
    private String trxcode;


    /**
     * 交易结果码
     */
    private String trxstatus;


    /**
     * 商户号
     */
    private String cusid;

    /**
     * 应用ID
     */
    private String appid;


    /**
     * trxerrmsg
     */
    private String trxerrmsg;

    /**
     * 随机字符串
     */
    private String randomstr;

    /**
     * 参考号
     */
    private String termrefnum;

    /**
     * 原交易流水
     */
    private String srctrxid;



    /**
     * 商户平台订单号
     */
    private String orderid;

    /**
     * traceno
     */
    private String traceno;

    /**
     * fee
     */
    private String fee;

    /**
     * 终端批次号
     */
    private String termbatchid;

    /**
     * sign校验码
     */
    private String sign;

    /**
     * 交易请求日期
     */
    private String trxdate;

    /**
     * 交易流水号
     */
    private String trxid;

    private String accttype;

    /**
     * 支付渠道流水号
     */
    private String chnltrxid;

    /**
     * 交易完成时间
     */
    private String fintime;

    /**
     * 授权码
     */
    private String termauthno;

    /**
     * 终端号
     */
    private String termno;

    private String paychnl;

    /**
     * 返回码
     */
    private String retcode;

    /**
     * 返回码说明
     */
    private String retmsg;

}
