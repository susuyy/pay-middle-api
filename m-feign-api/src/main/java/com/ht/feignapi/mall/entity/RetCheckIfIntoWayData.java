package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetCheckIfIntoWayData implements Serializable {

    /**
     * 是否需要填写派送信息标识
     */
    private boolean intoWayFlag;

    /**
     * 派送信息明细
     */
    private List<ReturnWayBillDetail> retDetailList;

    /**
     * 派送费用 总计 单位分
     */
    private Integer totalWayBillFee;
}
