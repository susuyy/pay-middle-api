package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 通联接口交互数据 卡信息
 *
 * @author suyangyu
 * @since 2020-06-12
 */
@Data
public class CardInfo implements Serializable {
    /**
     * 卡所属机构号
     */
    private String brhId;

    /**
     * 卡号
     */
    private String cardId;

    /**
     * 品牌号
     */
    private String brandId;

    /**
     * 有效期
     */
    private String validityDate;

    /**
     * 卡状态
     * 0-正常
     * 1-挂失
     * 2-冻结
     * 3-作废
     */
    private String cardSta;

    /**
     * 卡产品列表
     * 可能包含多产品
     */
    private CardProductInfoArrays cardProductInfoArrays;
}
