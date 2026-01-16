package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MallQrCodeStatementData implements Serializable {

    private String cardNo;

    private String merchantCode;
}
