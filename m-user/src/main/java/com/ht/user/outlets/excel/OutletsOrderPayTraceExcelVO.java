package com.ht.user.outlets.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2021/11/22 14:33
 */
@Data
public class OutletsOrderPayTraceExcelVO implements Serializable {

    /**
     * 订单编码
     */
    @ExcelProperty("订单编码")
    private String orderCode;

    /**
     * 订单编码
     */
    @ExcelProperty("交易号")
    private String sybTrxid;

    /**
     * 支付类型
     */
    @ExcelProperty("支付类型")
    private String type;

    /**
     * 支付状态
     */
    @ExcelProperty("支付状态")
    private String state;

    /**
     * 支付来源
     */
    @ExcelProperty("支付来源")
    private String source;

    /**
     * 支付来源标识
     */
    @ExcelProperty("支付来源标识")
    private String sourceId;

    /**
     * 支付金额: 元
     */
    @ExcelProperty("支付金额")
    private BigDecimal amount;

    /**
     * pos机串号
     */
    @ExcelProperty("pos机串号")
    private String posSerialNum;

    /**
     * 收银台款台号
     */
    @ExcelProperty("收银台款台号")
    private String cashId;

    /**
     * 已退款金额 元
     */
    @ExcelProperty("已退款金额")
    private BigDecimal refundAmount;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("支付时间")
    private String payTime;

}
