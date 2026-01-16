package com.ht.feignapi.prime.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CardRefundOrderExcelData implements Serializable {


    /**
     * 退款订单号
     */
    @ExcelProperty("退款单号")
    private String orderId;

    /**
     * 原交易订单号 mer_order_id
     */
    @ExcelProperty("原交易订单号")
    private String oriOrderId;

    /**
     * 退款金额
     */
    @ExcelProperty("退款金额")
    private String amount;

    /**
     * 卡号
     */
    @ExcelProperty("卡号")
    private String cardId;

    /**
     * 手机
     */
    @ExcelProperty("手机号")
    private String userPhone;

    /**
     * 数据信息
     */
    @ExcelProperty("描述")
    private String subMsg;

    /**
     * 操作人
     */
    @ExcelProperty("操作人")
    private String ext2;

    @ExcelProperty("操作时间")
    private String createAt;

}
