package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class AdminBatchBindCardRet implements Serializable {

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 数量
     */
    private String quantity;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 管理员发卡操作账号
     */
    private String operatorAccount;

    /**
     * 管理员发卡操作账号id
     */
    private String operatorId;

    /**
     * 收款类型
     */
    private String payType;

    /**
     * 支付金额
     */
    private String payAmount;

    /**
     * 卡号集合
     */
    private List<CardElectronic> cardElectronicList;
}
