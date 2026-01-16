package com.ht.feignapi.prime.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2021/2/25 15:00
 */
@Data
public class PrimeBuyCardOrderExcelVo implements Serializable {
    @ExcelProperty("序列号")
    private String rowNum;
    @ExcelProperty("订单编号")
    private String orderCode;
    @ExcelProperty("订单总额")
    private String amount;
    @ExcelProperty("单笔实付金额")
    private String detailAmount;
    @ExcelProperty("单笔实收金额")
    private String receiveAmount;
    @ExcelProperty("支付状态")
    private String state;
    @ExcelProperty("销售时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date createAt;
    @ExcelIgnore
    private Long userId;
    @ExcelProperty("卡号")
    private String cardNo;

    @ExcelProperty("批次号")
    private String batchCode;

    @ExcelProperty("卡名称")
    private String cardName;
    @ExcelProperty("卡类型")
    private String cardType;

    @ExcelProperty("手机号")
    private String phone;
    @ExcelIgnore
    private String productionCode;
    @ExcelProperty("交易类型")
    private String sellType;
    @ExcelProperty("付款方式")
    private String payType;
    @ExcelIgnore
    private int quantity;

    @ExcelProperty(value = "卡来源")
    private String buyCardSource;
}
