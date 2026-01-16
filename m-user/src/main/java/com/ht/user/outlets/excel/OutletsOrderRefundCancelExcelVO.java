package com.ht.user.outlets.excel;

import com.alibaba.excel.annotation.ExcelProperty;
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
public class OutletsOrderRefundCancelExcelVO implements Serializable {


    /**
     * 退款单号,收银台上送
     */
    @ExcelProperty("退款单号")
    private String refundCancelCode;

    /**
     * 原交易订单号
     */
    @ExcelProperty("原支付订单号")
    private String oriOrderCode;

    /**
     * 我方自生成唯一标识
     */
    @ExcelProperty("唯一标识")
    private String backOrderCode;

    /**
     * 订单编码
     */
    @ExcelProperty("交易号")
    private String sybTrxid;

    /**
     * 支付来源标识
     */
    @ExcelProperty("退入账户")
    private String sourceId;

    /**
     * 退款金额
     */
    @ExcelProperty("退款金额")
    private BigDecimal amount;

    /**
     * 退款成功标识 success成功  fail失败
     */
    @ExcelProperty("退款状态")
    private String state;

    /**
     * 拓展字段1 (操作员)
     */
    @ExcelProperty("操作员")
    private String ext1;

    /**
     * 业务编码
     */
    @ExcelProperty("业务编码")
    private String businessType;

    /**
     * 服务类型 pos为pos机业务,qrCode为扫码业务
     */
    @ExcelProperty("服务类型")
    private String serviceType;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("退款时间")
    private Date createAt;


}
