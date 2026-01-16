package com.ht.feignapi.prime.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_refund_order")
public class CardRefundOrder implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 退款订单号
     */
    private String orderId;

    /**
     * 原交易订单号 mer_order_id
     */
    private String oriOrderId;

    /**
     * 生成的退货单号
     */
    private String backOrderId;

    /**
     * 商户号
     */
    private String merId;

    /**
     * 有效期
     */
    private String expireDate;

    /**
     * 通联流水号
     */
    private String transNo;

    /**
     * 交易日期
     */
    private String transDate;

    /**
     * 退款金额
     */
    private Integer amount;

    /**
     * 卡号
     */
    private String cardId;

    /**
     * 退款成功标识 success成功  fail失败
     */
    private String state;

    /**
     * 数据信息
     */
    private String subMsg;

    /**
     * 数据信息
     */
    private String userPhone;

    /**
     * 扩展字段1  (商户号|终端|收银机号|门店号|操作员号)
     */
    private String ext1;

    /**
     * 扩展字段2
     */
    private String ext2;

    /**
     * 扩展字段3
     */
    private String ext3;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateAt;

    /**
     * 卡类型
     */
    @TableField(exist = false)
    private String cardType;

    /**
     * 卡类型名称
     */
    @TableField(exist = false)
    private String cardTypeName;

    /**
     * 是否收款标识
     */
    @TableField(exist = false)
    private String cardIsFree;
}
