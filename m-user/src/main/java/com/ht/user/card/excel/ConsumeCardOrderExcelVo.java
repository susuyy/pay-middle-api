package com.ht.user.card.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ht.user.common.enums.CardElectronicEnum;
import com.ht.user.common.enums.PayTypeEnum;
import com.ht.user.config.ExcelEnumConverter;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConsumeCardOrderExcelVo {

    @ExcelProperty(value = "订单编号")
    private String orderCode;

    @ExcelProperty(value = "支付流水号")
    private String payCode;

    @ExcelProperty(value = "订单总额")
    private BigDecimal orderTotalAmount;

    @ExcelProperty(value = "单笔实收金额")
    private BigDecimal receiveAmount;

    @ExcelProperty(value = "单笔实付金额")
    private BigDecimal detailAmount;

    @ExcelProperty(value = "支付状态",converter = ExcelEnumConverter.class)
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private PayTypeEnum state;

    @ExcelProperty(value = "支付时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String createAt;

    @ExcelProperty(value = "卡号")
    private String sourceId;

    @ExcelProperty(value = "卡余额")
    private String cardFaceValue;

    @ExcelProperty(value = "批次号")
    private String refBatchCode;

    @ExcelProperty("卡名称")
    private String refCardName;

    @ExcelProperty(value = "卡类型",converter = ExcelEnumConverter.class)
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private CardElectronicEnum refCardType;

    @ExcelProperty(value = "手机号")
    private String userPhone;

    @ExcelProperty(value = "交易类型")
    private String sellType = "消费支出";

    @ExcelProperty(value = "付款方式")
    private String source;

    @ExcelProperty(value = "卡来源")
    private String merchId;

}
