package com.ht.user.mall.entity;

import com.ht.user.card.entity.Merchants;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetOrderQrCodeDetailData implements Serializable {

    /**
     * 券码 二维码 列表
     */
    private List<QrCodeDetailData> qrCodeDetailDataList;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 前端展示 有效期
     */
    private String validityDateStr;

    /**
     * 使用门店
     */
    private List<MerchantsDetailData> merchantsDetailDataList;

    /**
     * 商品券码页 展示的 订单数据
     */
    private OrderQrShowData orderQrShowData;





}
