package com.ht.user.outlets.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutletsOrderRefundCancel implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 退款单号,收银台上送
     */
    private String refundCancelCode;

    /**
     * 原交易订单号
     */
    private String oriOrderCode;

    /**
     * 我方自生成唯一标识
     */
    private String backOrderCode;

    /**
     * 业务系统的商户编码
     */
    private String merchantCode;

    /**
     * 上送获取到的商户号
     */
    private String merchId;

    /**
     * 上送获取到的商户名
     */
    private String merchName;

    /**
     * 对应支付流水表的trace_no(我方自生成的流水号)
     */
    private String tranNo;

    /**
     * 支付来源标识
     */
    private String sourceId;

    /**
     * 退款金额
     */
    private Long amount;

    /**
     * 退款成功标识 success成功  fail失败
     */
    private String state;

    /**
     * 拓展字段1 (操作员)
     */
    private String ext1;

    /**
     * 拓展字段2
     */
    private String ext2;

    /**
     * 拓展字段3
     */
    private String ext3;

    /**
     * 业务编码
     */
    private String businessType;

    /**
     * 服务类型 pos为pos机业务,qrCode为扫码业务
     */
    private String serviceType;

    /**
     * 调用的支付渠道的api
     */
    private String channelApi;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    /**
     * 支付时的交易类型
     */
    private String payTrxcode;

    /**
     * 支付时的交易类型 中文描述
     */
    private String payTrxcodeDescribe;

    /**
     * 支付时对应的收银台号
     */
    private String cashId;
}
