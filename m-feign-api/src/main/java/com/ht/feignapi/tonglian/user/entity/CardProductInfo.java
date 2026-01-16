package com.ht.feignapi.tonglian.user.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * 通联交互数据 卡片的产品信息，一张卡片可能有多个产品
 *
 * @author suyangyu
 * @since 2020-06-12
 */
@Data
public class CardProductInfo implements Serializable {


    private Long id;

    /**
     * 卡号
     */
    private String cardId;

    /**
     * 产品号
     */
    private String productId;

    /**
     * 产品状态	0-正常 1-挂失 2-冻结 3-作废
     */
    private String productStat;

    /**
     * 账户余额 单位分
     */
    private String accountBalance;

    /**
     * 可用余额，单位分
     */
    private String validBalance;

    /**
     * 产品有效期
     */
    private String productDate;
}
