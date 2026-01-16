package com.ht.feignapi.prime.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConsumeOrdersMasterExcelData implements Serializable {

    @ExcelProperty(value = "订单编号")
    private String orderCode;

    @ExcelProperty(value = "订单总额")
    private String amount;

    @ExcelProperty(value = "总实收金额")
    private String receiveAmount;

    @ExcelProperty(value = "支付状态")
    private String state;

    @ExcelProperty(value = "支付时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String creatAt;

//    @ExcelProperty(value = "退款操作人")
    @ExcelIgnore
    private String refundOperator;

//    @ExcelProperty(value = "操作时间")
    @ExcelIgnore
    private String refundTime;
}
