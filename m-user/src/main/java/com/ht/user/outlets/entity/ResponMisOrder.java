package com.ht.user.outlets.entity;

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



}
