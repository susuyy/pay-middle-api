package com.ht.feignapi.mall.entity;

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
     * 使用门店
     */
    private MerchantsDetailData merchantsDetailData;

    /**
     * 商品券码页 展示的 订单数据
     */
    private OrderQrShowData orderQrShowData;

    /**
     * 展示商品数据
     */
    private MallProductions mallProductions;





}
