package com.ht.feignapi.mall.entity;

import lombok.Data;
import sun.rmi.log.LogInputStream;

import java.io.Serializable;
import java.util.List;

@Data
public class ReturnProductPointsData implements Serializable {

    /**
     * 总使用积分
     */
    private int totalUsePoints;

    /**
     * 总扣除金额
     */
    private int totalReduceMoney;

    /**
     * 商品积分详情
     */
    private List<ProductPointsMoneyDetail> productPointsMoneyDetailList;

    /**
     * 用户积分
     */
    private int userPoints;

    /**
     * 使用过后剩余的积分
     */
    private int afterUserPoints;
}
