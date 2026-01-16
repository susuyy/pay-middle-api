package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PosSelectCardNo implements Serializable {

    /**
     * 卡编号
     */
    private String cardNo;
}
