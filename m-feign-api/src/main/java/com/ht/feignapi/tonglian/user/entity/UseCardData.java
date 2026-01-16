package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UseCardData implements Serializable {

    private Boolean useLimitFlag;

    private String useMessage;
}
