package com.ht.feignapi.tonglian.admin.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/6 15:57
 */
@Data
public class UserRealCardImportVo {

    @ExcelProperty("openId")
    private String openId;

    @ExcelProperty("实体卡号")
    private String icCard;

    @ExcelProperty("手机号")
    private String phone;
}
