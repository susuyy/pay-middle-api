package com.ht.feignapi.prime.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

@Data
public class SummaryExcelData implements Serializable {

    /**
     * 卡类型
     */
    @ExcelProperty("卡类型")
    @ColumnWidth(15)
    private String cardType;

    /**
     * 预付款总额
     */
    @ExcelProperty("预付款总额")
    @ColumnWidth(15)
    private String cardTotalAmount;

    /**
     * 已核销金额
     */
    @ExcelProperty("已核销金额")
    @ColumnWidth(15)
    private String consumeAmount;

    /**
     * 已核销金额
     */
    @ExcelProperty("退款金额")
    @ColumnWidth(15)
    private String refundAmount;

    /**
     * 未核销金额
     */
    @ExcelIgnore
    private String remainingAmount;
}
