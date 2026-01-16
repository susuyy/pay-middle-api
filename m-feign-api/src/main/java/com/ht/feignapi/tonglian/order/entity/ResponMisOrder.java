package com.ht.feignapi.tonglian.order.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResponMisOrder implements Serializable {


    /**
     * 业务类型
     */
    @JsonProperty("orderCode")
    @JSONField(name = "orderCode")
    private String orderCode;


    /**
     * 交易成功返回，云MIS系统唯一流水号，用于查询或对账
     */
    @JsonProperty("cloudMisTrxSsn")
    @JSONField(name = "cloudMisTrxSsn")
    private String cloudMisTrxSsn;


}
