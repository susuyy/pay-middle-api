package com.ht.feignapi.tonglian.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ht.feignapi.prime.entity.CardOrderDetailsVo;
import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
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

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateAt;

    private String userName;

    private String phone;

    private List<CardOrderDetailsVo> orderDetailsList;

    private List<String> cardNoList;

    private List<CardOrderPayTrace> payTraceList;

    private Integer realPayAmount;

    @TableField(exist = false)
    private String collectMoneyType;

    @TableField(exist = false)
    private String refundOperator;

    /**
     * 收银台号
     */
    @TableField(exist = false)
    private String cashId;

    /**
     * pos机号
     */
    @TableField(exist = false)
    private String pos_serial_num;

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
