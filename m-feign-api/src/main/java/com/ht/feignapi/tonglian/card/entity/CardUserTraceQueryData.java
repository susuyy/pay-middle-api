package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CardUserTraceQueryData implements Serializable {

    private String merchantCode;
    private Integer pageNo;
    private Integer pageSize;
}
