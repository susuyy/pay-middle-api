package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CardProductInfoArrays implements Serializable {

    private List<CardProductInfo> cardProductInfo;
}
