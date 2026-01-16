package com.ht.feignapi.tonglian.order.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单主表
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CardOrdersVO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 订单描述
     */
    private String orderDesc;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 订单类型
     */
    private String type;

    /**
     * 订单类型
     */
    private String state;

    /**
     * 商户编号
     */
    private String merchantCode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 金额：默认单位：分
     */
    private Integer amount;

    /**
     * 折扣：默认单位：分
     */
    private Integer discount;

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date createAt;

    private List<CardOrderDetails> cardOrderDetailsList;

    /**
     * 限制支付类型
     */
    private String limitPayType;

}
