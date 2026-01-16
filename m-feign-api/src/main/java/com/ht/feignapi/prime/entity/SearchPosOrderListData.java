package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchPosOrderListData implements Serializable {


    private String startTime;

    private String endTime;

    private String orderCode;

    private String cashId;

    private String amount;

    private Integer pageNo;

    private Integer pageSize;

}
