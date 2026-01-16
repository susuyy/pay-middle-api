package com.ht.feignapi.prime.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2021/2/23 10:55
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CardOrderDetailsVo implements Serializable {

    private static final long serialVersionUID=1L;


    private Long id;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 金额：默认单位：分
     */
    private Integer amount;

    /**
     * 商品编码
     */
    private String productionCode;

    /**
     * 商品名称
     */
    private String productionName;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 面值
     */
    private String faceValue;

    /**
     * 购买金额
     */
    private String boughtPrice;

    /**
     * 卡类型
     */
    private String cardType;

}
