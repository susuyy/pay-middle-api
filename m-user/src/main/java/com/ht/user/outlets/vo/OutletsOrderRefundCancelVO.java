package com.ht.user.outlets.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2021/11/22 17:15
 */
@Data
public class OutletsOrderRefundCancelVO implements Serializable {

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
    private BigDecimal amount;

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

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

}
