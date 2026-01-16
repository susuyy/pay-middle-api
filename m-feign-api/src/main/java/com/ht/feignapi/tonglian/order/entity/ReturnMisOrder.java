package com.ht.feignapi.tonglian.order.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReturnMisOrder implements Serializable {

    /**
     * 分配的appId
     */
    @JsonProperty("APP_ID")
    @JSONField(name = "APP_ID")
    private String APP_ID;

    /**
     * 业务类型
     */
    @JsonProperty("BUSINESS_ID")
    @JSONField(name = "BUSINESS_ID")
    private String BUSINESS_ID;

    /**
     * 响应编码 code
     */
    @JsonProperty("RSP_CODE")
    @JSONField(name = "RSP_CODE")
    private String RSP_CODE;

    /**
     * 透传支付产品给云MIS的响应JSON字符串
     */
    @JsonProperty("RSP_DATA")
    @JSONField(name = "RSP_DATA")
    private String RSP_DATA;

    /**
     * 响应描述
     */
    @JsonProperty("RSP_DESC")
    @JSONField(name = "RSP_DESC")
    private String RSP_DESC;

    /**
     * 交易成功返回，云MIS系统唯一流水号，用于查询或对账
     */
    @JsonProperty("ClOUD_MIS_TRX_SSN")
    @JSONField(name = "ClOUD_MIS_TRX_SSN")
    private String ClOUD_MIS_TRX_SSN;

    /**
     * 计算方法参照附录1
     */
    @JsonProperty("SIGN_DATA")
    @JSONField(name = "SIGN_DATA")
    private String SIGN_DATA;

}
