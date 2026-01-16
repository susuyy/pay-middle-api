package com.ht.feignapi.tonglian.order.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RspData implements Serializable {

    /**
     * 银行卡交易时返回卡号；扫码交易返回OPENID或USERID
     */
    @JsonProperty("CARDNO")
    @JSONField(name = "CARDNO")
    private String CARDNO;

    /**
     * 交易返回码注释
     */
    @JsonProperty("REJCODE_CN")
    @JSONField(name = "REJCODE_CN")
    private String REJCODE_CN;

    /**
     *商户编号
     */
    @JsonProperty("MERCH_ID")
    @JSONField(name = "MERCH_ID")
    private String MERCH_ID;

    /**
     * 交易渠道
     */
    @JsonProperty("TRANS_CHANNEL")
    @JSONField(name = "TRANS_CHANNEL")
    private String TRANS_CHANNEL;

    /**
     *批次号
     */
    @JsonProperty("BATCH_NO")
    @JSONField(name = "BATCH_NO")
    private String BATCH_NO;

    /**
     * 支付时间
     */
    @JsonProperty("TIME")
    @JSONField(name = "TIME")
    private String TIME;

    /**
     * 交易返回码
     */
    @JsonProperty("REJCODE")
    @JSONField(name = "REJCODE")
    private String REJCODE;

    /**
     * 签名
     */
    @JsonProperty("SIGN")
    @JSONField(name = "SIGN")
    private String SIGN;

    /**
     * 支付日期
     */
    @JsonProperty("DATE")
    @JSONField(name = "DATE")
    private String DATE;

    /**
     * 终端编号
     */
    @JsonProperty("TER_ID")
    @JSONField(name = "TER_ID")
    private String TER_ID;

    /**
     * 订单编号
     */
    @JsonProperty("ORDER_NO")
    @JSONField(name = "ORDER_NO")
    private String ORDER_NO;

    /**
     * 手续费，十二位长度（单位：分）
     */
    @JsonProperty("CARD_FEE")
    @JSONField(name = "CARD_FEE")
    private String CARD_FEE;

    /**
     * 支付金额，十二位长度（单位：分，不足左补零）
     */
    @JsonProperty("AMOUNT")
    @JSONField(name = "AMOUNT")
    private String AMOUNT;

    /**
     * 发卡行行号
     */
    @JsonProperty("ISS_NO")
    @JSONField(name = "ISS_NO")
    private String ISS_NO;

    /**
     * 系统参考号
     */
    @JsonProperty("REF_NO")
    @JSONField(name = "REF_NO")
    private String REF_NO;

    /**
     * 凭证号
     */
    @JsonProperty("TRACE_NO")
    @JSONField(name = "TRACE_NO")
    private String TRACE_NO;

    /**
     * 是否打印小票（1：打印,0：未打印）
     */
    @JsonProperty("PRINT_FLAG")
    @JSONField(name = "PRINT_FLAG")
    private String PRINT_FLAG;

    /**
     * 卡类型
     */
    @JsonProperty("CARDTYPE")
    @JSONField(name = "CARDTYPE")
    private String CARDTYPE;

    /**
     *
     * 业务类型
     */
    @JsonProperty("BUSINESS_ID")
    @JSONField(name = "BUSINESS_ID")
    private String BUSINESS_ID;

    /**
     *交易单号
     */
    @JsonProperty("TRANS_TICKET_NO")
    @JSONField(name = "TRANS_TICKET_NO")
    private String TRANS_TICKET_NO;

    /**
     * 授权码
     */
    @JsonProperty("AUTH_NO")
    @JSONField(name = "AUTH_NO")
    private String AUTH_NO;

    /**
     * 商户名称
     */
    @JsonProperty("MERCH_NAME")
    @JSONField(name = "MERCH_NAME")
    private String MERCH_NAME;

    /**
     * 有效期
     */
    @JsonProperty("EXP_DATE")
    @JSONField(name = "EXP_DATE")
    private String EXP_DATE;

    /**
     * 借贷记卡标识（银行卡）
     *
     * 0:借记 1:贷记
     */
    @JsonProperty("CARD_TYPE_IDENTY")
    @JSONField(name = "CARD_TYPE_IDENTY")
    private String CARD_TYPE_IDENTY;

    /**
     * 发卡行名称
     */
    @JsonProperty("ISS_NAME")
    @JSONField(name = "ISS_NAME")
    private String ISS_NAME;

    /**
     * usdk 版本 编码 code
     */
    @JsonProperty("USDK_VERSION_CODE")
    @JSONField(name = "USDK_VERSION_CODE")
    private String USDK_VERSION_CODE;

    /**
     * 操作员
     */
    @JsonProperty("OPER_NO")
    @JSONField(name = "OPER_NO")
    private String OPER_NO;

    /**
     * 内外卡标识（银行卡）
     *
     * 0：内卡 1:外卡
     */
    @JsonProperty("WILD_CARD_SIGN")
    @JSONField(name = "WILD_CARD_SIGN")
    private String WILD_CARD_SIGN;

    /**
     * 类型
     */
    @JsonProperty("TRANSTYPE")
    @JSONField(name = "TRANSTYPE")
    private String TRANSTYPE;

    /**
     * 签名标志
     */
    @JsonProperty("SIGN_FLAG")
    @JSONField(name = "SIGN_FLAG")
    private String SIGN_FLAG;

}
