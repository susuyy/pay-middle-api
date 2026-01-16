package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PrimeBuyCardData implements Serializable {

    /**
     * 用户openId
     */
    private String openId;

    private String userId;

    private String userPhone;

    /**
     * 选中购卡
     */
    private List<CardElectronicSell> cardElectronicSellList;

    /**
     * 是否阅读法律条文
     */
    private boolean read;

}
