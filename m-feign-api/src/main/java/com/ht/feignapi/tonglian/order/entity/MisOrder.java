package com.ht.feignapi.tonglian.order.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MisOrder implements Serializable {

    /**
     * 业务类型
     */
    @JsonProperty("BUSINESS_ID")
    @JSONField(name = "BUSINESS_ID")
    private String BUSINESS_ID;

    /**
     * 具体的支付请求数据，JSON格式字符串
     */
    @JsonProperty("CUST_DATA")
    @JSONField(name = "CUST_DATA")
    private String CUST_DATA;

    /**
     * 收银机款台号
     */
    @JsonProperty("CASH_ID")
    @JSONField(name = "CASH_ID")
    private String CASH_ID;

    /**
     *
     * 商户分店号
     */
    @JsonProperty("STORE_ID")
    @JSONField(name = "STORE_ID")
    private String STORE_ID;

    /**
     *商户分店号
     */
    @JsonProperty("APP_ID")
    @JSONField(name = "APP_ID")
    private String APP_ID;

    /**
     * 计算方法参照附录1
     */
    @JsonProperty("SIGN_DATA")
    @JSONField(name = "SIGN_DATA")
    private String SIGN_DATA;

    /**
     *  指定APP包名
     */
    @JsonProperty("APP_PACKAGE_NM")
    @JSONField(name = "APP_PACKAGE_NM")
    private String APP_PACKAGE_NM;

    /**
     *  APP_CLASS_NM
     */
    @JsonProperty("APP_CLASS_NM")
    @JSONField(name = "APP_CLASS_NM")
    private String APP_CLASS_NM;
}
