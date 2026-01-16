package com.ht.merchant.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchSubMerchantsData implements Serializable {

    private String searchCode;

    private String searchName;

    private String objectMerchantCode;

    private String type;

    private Integer pageNo;

    private Integer pageSize;
}
