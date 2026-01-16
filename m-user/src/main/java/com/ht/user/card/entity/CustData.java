package com.ht.user.card.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustData implements Serializable {

    /**
     * 业务类型   必填
     */
    @JsonProperty("BUSINESS_ID")
    @JSONField(name = "BUSINESS_ID")
    private String BUSINESS_ID;

    /**
     * 支付金额，十二位长度（单位：分，不足左补零）  必填
     */
    @JsonProperty("AMOUNT")
    @JSONField(name = "AMOUNT")
    private String AMOUNT;

    /**
     * 订单编号  必填
     */
    @JsonProperty("ORDER_NO")
    @JSONField(name = "ORDER_NO")
    private String ORDER_NO;

    /**
     * 备注信息  非必填
     */
    @JsonProperty("MEMO")
    @JSONField(name = "MEMO")
    private String MEMO;

    /**
     * 扫码信息 非必填
     */
    @JsonProperty("QRCODE")
    @JSONField(name = "QRCODE")
    private String QRCODE;

    /**
     * 交易唯一标识  非必填
     */
    @JsonProperty("TRANS_CHECK")
    @JSONField(name = "TRANS_CHECK")
    private String TRANS_CHECK;

    /**
     * 扩展字段具体说明见附录2 非必填
     */
    @JsonProperty("PAGE_APPEND_CONTENT")
    @JSONField(name = "PAGE_APPEND_CONTENT")
    private String PAGE_APPEND_CONTENT;

    /**
     * 扩展字段具体说明见附录2 非必填
     */
    @JsonProperty("VALIDATE_INFO")
    @JSONField(name = "VALIDATE_INFO")
    private String VALIDATE_INFO;

    /**
     * 扩展字段具体说明见附录2 非必填
     */
    @JsonProperty("BUS_INFO")
    @JSONField(name = "BUS_INFO")
    private String BUS_INFO;

}
