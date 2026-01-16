package com.ht.user.mall.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单主表
 * </p>
 *
 * @author ${author}
 * @since 2020-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderShoppingCart implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type=IdType.AUTO)
    private Long id;

    /**
     * 主体编号
     */
    private String merchantCode;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 订单类型:后台充值订单admin_recharge,后台调账admin_adjust,购物订单shop
     */
    private String type;

    /**
     * 订单状态
     */
    private String state;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 售货员
     */
    private String saleId;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 金额：默认单位：分
     */
    private Integer amount;

    /**
     * 描述
     */
    private String comments;

    /**
     * 折扣：默认单位：分
     */
    private Integer discount;

    /**
     * 是否选中 记录选中状态
     */
    private String selected;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateAt;


}
