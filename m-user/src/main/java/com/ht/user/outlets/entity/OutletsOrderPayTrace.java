package com.ht.user.outlets.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 订单支付流水
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutletsOrderPayTrace implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
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
     * 手续费,单位分
     */
    private Integer fee;

    /**
     * pos机串号
     */
    private String posSerialNum;

    /**
     * 用户标识
     */
    private String userFlag;

    /**
     * 华联天安业务系统的商户编码
     */
    private String merchantCode;

    /**
     * pos上送获取到的商户号
     */
    private String merchId;

    /**
     * pos上送获取到的商户名
     */
    private String merchName;

    /**
     * 外部流水号,用于记录pos第三方收款的流水号
     */
    private String refTraceNo;

    private String traceNo;

    /**
     * 收银台款台号
     */
    private String cashId;

    /**
     * 已退款金额
     */
    private Integer refundAmount;

    /**
     * 调用的支付渠道的api
     */
    private String channelApi;

    /**
     * 支付完成时间
     */
    private String payTime;

    /**
     * 手机号
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
     * 该条支付后,卡剩余余额
     */
    private String refRemainFaceValue;

    private String refCardBrhId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    /**
     * 来源标识码
     */
    @TableField(exist = false)
    private String sourceCode;

    /**
     * 显示退单按钮
     */
    @TableField(exist = false)
    private String isShowRefund;
}
