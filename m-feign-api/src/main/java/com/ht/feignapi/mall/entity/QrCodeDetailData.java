package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QrCodeDetailData implements Serializable {

    /**
     * 名称
     */
    private String productionName;

    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 券码
     */
    private String cardNo;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 展示 过期时间
     */
    private String showEndDate;

}
