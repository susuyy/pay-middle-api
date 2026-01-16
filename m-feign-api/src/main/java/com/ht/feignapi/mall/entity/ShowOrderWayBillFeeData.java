package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShowOrderWayBillFeeData implements Serializable {
    private List<ProductionsMsg> productionsMsgList;

    private String wayBillType;
}
