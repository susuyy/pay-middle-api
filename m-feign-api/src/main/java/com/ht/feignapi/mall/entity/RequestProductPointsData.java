package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RequestProductPointsData implements Serializable {

    private List<ProductPointsData> productPointsDataList;

    private String openId;

    private String objectMerchantCode;
}
