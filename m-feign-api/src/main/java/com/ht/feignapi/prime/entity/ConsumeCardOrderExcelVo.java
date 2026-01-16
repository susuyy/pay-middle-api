package com.ht.feignapi.prime.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

@Data
public class ConsumeCardOrderExcelVo {

    @ExcelProperty(value = "序列号")
    private String rowNum;

    @ExcelProperty(value = "订单编号")
    private String orderCode;

    @ExcelProperty(value = "支付流水号")
    private String payCode;

    @ExcelProperty(value = "订单总额")
    private String amount;

    @ExcelProperty(value = "单笔实收金额")
    private String receiveAmount;

    @ExcelProperty(value = "单笔实付金额")
    private String detailAmount;

    @ExcelProperty(value = "支付状态")
    private String state;

//    @ExcelProperty(value = "订单描述")
    @ExcelIgnore
    private String comments;

    @ExcelProperty(value = "支付时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String creatAt;

    @ExcelIgnore
    private Long userId;

    @ExcelProperty(value = "卡号")
    private String cardNo;

    @ExcelProperty(value = "卡余额")
    private String cardFaceValue;

    @ExcelProperty(value = "批次号")
    private String batchCode;

    @ExcelProperty("卡名称")
    private String cardName;
    @ExcelProperty("卡类型")
    private String cardType;

    @ExcelProperty(value = "手机号")
    private String phone;

    @ExcelIgnore
    private String productionCode;

    @ExcelProperty(value = "交易类型")
    private String sellType;

    @ExcelProperty(value = "付款方式")
    private String payType;

    @ExcelProperty(value = "卡来源")
    private String buyCardSource;

//    @ExcelProperty(value = "退款操作人")
//    private String refundOperator;
//
//    @ExcelProperty(value = "操作时间")
//    private String refundTime;
}
