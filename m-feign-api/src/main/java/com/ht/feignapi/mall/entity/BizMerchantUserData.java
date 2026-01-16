package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BizMerchantUserData implements Serializable {

    /**
     * 主体商户编码
     */
    private String objectMerchantCode;

    /**
     * 子商户编码
     */
    private String subMerchantCode;

    /**
     * 主体 对应通商云的 bizUserId
     */
    private String bizObjectMerchantUserId;

    /**
     * 子商户对应通商云的 bizUserId
     */
    private String bizSubMerchantUserId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户对应通商云的 bizUserId
     */
    private String bizUserId;
}
