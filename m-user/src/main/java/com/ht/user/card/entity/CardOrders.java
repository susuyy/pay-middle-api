package com.ht.user.card.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
 * @since 2020-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_orders")
public class CardOrders implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 订单类型
     */
    private String type;

    /**
     * 订单生成人员Id
     */
    private String saleId;

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

    /**
     * 描述
     */
    private String comments;

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date updateAt;

    @TableField(exist = false)
    private List<String> cardNoList;
    @TableField(exist = false)
    private List<CardOrderDetailsVo> orderDetailsList;

    @TableField(exist = false)
    private String cardName;

    @TableField(exist = false)
    private List<CardOrderPayTrace> payTraceList;

    /**
     * 限制支付类型
     */
    private String limitPayType;


    /**
     * 主门店编码
     */
    private String storeCode;

    /**
     * 购买人手机号
     */
    private String actualPhone;

    /**
     * 购买人身份证
     */
    private String idCardNo;

}
