package com.ht.feignapi.tonglian.order.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MisOrderData implements Serializable {

    /**
     * 业务类型   必填
     */
    @JsonProperty("businessId")
    @JSONField(name = "businessId")
    private String businessId;

    /**
     * 支付金额，十二位长度（单位：分，不足左补零）  必填
     */
    @JsonProperty("amount")
    @JSONField(name = "amount")
    private String amount;

    /**
     * 订单编号  必填
     */
    @JsonProperty("orderCode")
    @JSONField(name = "orderCode")
    private String orderCode;

    /**
     * 收银机款台号
     */
    @JsonProperty("cashId")
    @JSONField(name = "cashId")
    private String cashId;

    /**
     *
     * 商户分店号
     */
    @JsonProperty("storeId")
    @JSONField(name = "storeId")
    private String storeId;

    /**
     *
     * 商户号
     */
    @JsonProperty("merchantCode")
    @JSONField(name = "merchantCode")
    private String merchantCode;

    /**
     * 备注信息
     */
    @JsonProperty("memo")
    @JSONField(name = "memo")
    private String memo;


    /**
     * 交易流水号
     */
    @JsonProperty("payCode")
    @JSONField(name = "payCode")
    private String payCode;

    /**
     * 支付方式限制
     */
    @JsonProperty("limitPayType")
    @JSONField(name = "limitPayType")
    private String limitPayType;

    /**
     * 商品信息
     */
    @JsonProperty("orderDetail")
    @JSONField(name = "orderDetail")
    private String orderDetail;

    /**
     * 富基上送的 门店号(主订单保存用)
     */
    @JsonProperty("storeCode")
    @JSONField(name = "storeCode")
    private String storeCode;

    /**
     * 购买人身份证
     */
    @JsonProperty("idCardNo")
    @JSONField(name = "idCardNo")
    private String idCardNo;

    /**
     * 购买人手机号
     */
    @JsonProperty("actualPhone")
    @JSONField(name = "actualPhone")
    private String actualPhone;


}
