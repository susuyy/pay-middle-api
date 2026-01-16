package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CardProductInfoArrays implements Serializable {

    private List<CardProductInfo> cardProductInfo;
}
