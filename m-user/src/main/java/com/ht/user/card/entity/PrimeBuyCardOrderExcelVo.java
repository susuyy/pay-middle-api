package com.ht.user.card.entity;

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
    private String orderCode;
    private Integer amount;
    private Integer detailAmount;
    private String state;
    private String comments;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    private Long userId;
    private String productionCode;
    private String rowNum;
    private int quantity;

    private String receiveAmount;
    private String payType;
    private String phone;



}
