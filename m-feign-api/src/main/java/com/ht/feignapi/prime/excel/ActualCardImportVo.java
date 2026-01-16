package com.ht.feignapi.prime.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class   ActualCardImportVo implements Serializable {

    /**
     * 用户手机号
     */
    @ExcelProperty("手机号")
    private String userPhone;

    /**
     *  实体卡卡号
     */
    @ExcelProperty("卡号")
    private String cardNo;
}
