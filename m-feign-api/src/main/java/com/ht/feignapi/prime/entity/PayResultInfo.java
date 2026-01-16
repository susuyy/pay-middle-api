package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayResultInfo implements Serializable {

    /**
     * 交易金额
     */
    private String amount;

    /**
     * 成功支付的流水号,通联返回的交易流水号
     */
    private String payTxnId;

    /**
     * 商户订单号
     */
    private String merOrderId;

    /**
     * 商户号
     */
    private String merId;

    /**
     * 支付币种
     */
    private String payCur;

    /**
     * 支付成功时间
     */
    private String payTxnTm;

    /**
     * 商户上送交易时间
     */
    private String merTm;

    /**
     * 支付活动
     */
    private String paymentId;

    /**
     * 交易类型
     */
    private String type;

    /**
     * 卡号
     */
    private String cardId;

    /**
     * 交易状态
     * 交易成功固定值1-成功，不成功的返回异常信息
     *
     * 0-处理中
     *
     * 1-成功
     *
     * 2-失败
     */
    private String stat;

    /**
     * 商户上送的自定义信息
     */
    private String misc;

    /**
     * 响应信息
     */
    private String subMsg;

}
