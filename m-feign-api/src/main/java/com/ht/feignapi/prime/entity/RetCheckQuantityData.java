package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetCheckQuantityData implements Serializable {

    /**
     * 用户
     */
    private VipUser vipUser;


    /**
     * 卡列表
     */
    private List<CardElectronicSell> cardElectronicSellList;

    /**
     * 是否包含库存标识
     */
    private boolean quantityFlag;

    /**
     * 余额校验标识
     */
    private boolean userAccountFlag;

    /**
     * 剩余购卡面值
     */
    private long remainingCardFaceValue;
}
