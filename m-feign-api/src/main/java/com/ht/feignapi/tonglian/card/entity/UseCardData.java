package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UseCardData implements Serializable {

    private Boolean useLimitFlag;

    private String useMessage;

    private Long userId;
}
