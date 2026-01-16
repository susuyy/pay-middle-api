package com.ht.feignapi.tonglian.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 订单支付流水
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CardOrderPayTrace implements Serializable {

    private static final long serialVersionUID=1L;


    private Long id;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 订单明细编码
     */
    private Long orderDetailId;

    /**
     * 支付来源
     */
    private String payCode;

    /**
     * 支付类型
     */
    private String type;

    /**
     * 支付状态
     */
    private String state;

    /**
     * 支付来源
     */
    private String source;

    /**
     * 支付来源标识
     */
    private String sourceId;

    /**
     * 支付金额: 默认 分
     */
    private Integer amount;

    /**
     * pos机串号
     */
    private String posSerialNum;

    /**
     * 用户标识
     */
    private String userFlag;

    /**
     * 华联天安 业务系统商户编码
     */
    private String merchantCode;

    /**
     * 商户号
     */
    private String merchId;

    /**
     * 商户名
     */
    private String merchName;

    /**
     * 外部流水号,通常用于记录pos收银第三方收款的流水号
     */
    private String refTraceNo;


    /**
     * 外部流水号,通常用于记录pos收银第三方收款的流水号
     */
    private String traceNo;

    /**
     * 收银台款台号
     */
    private String cashId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
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

    @TableField(exist = false)
    private String orderMasterDesc;

    @TableField(exist = false)
    private String orderMasterAmount;

    @TableField(exist = false)
    private Long orderMasterUserId;

    /**
     * 已退款金额
     */
    private Integer refundAmount;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 卡类型
     */
    private String refCardType;

    /**
     * 卡名
     */
    private String refCardName;

    /**
     * 卡批次
     */
    private String refBatchCode;

    /**
     * 卡批次
     */
    private String refCardBrhId;

    /**
     * 该条流水支付后 的 卡余额
     */
    private String refRemainFaceValue;

}
