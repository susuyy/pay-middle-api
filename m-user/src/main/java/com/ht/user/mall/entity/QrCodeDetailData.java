package com.ht.user.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QrCodeDetailData implements Serializable {

    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 券码
     */
    private String cardNo;


}
