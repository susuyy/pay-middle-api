package com.ht.feignapi.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 订单明细
 * </p>
 *
 * @author ${author}
 * @since 2020-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderShoppingCartDetails implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 商户编码
     */
    private String merchantCode;

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
     * 商品分类编码
     */
    private String productionCategoryCode;

    /**
     * 商品分类名称
     */
    private String productionCategoryName;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 状态
     */
    private String state;

    /**
     * 订单明细分类
     */
    private String type;

    /**
     * 折扣：默认单位：分
     */
    private Integer disccount;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateAt;


}
