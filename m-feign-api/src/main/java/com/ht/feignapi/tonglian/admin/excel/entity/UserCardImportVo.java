package com.ht.feignapi.tonglian.admin.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/7 9:38
 */
@Data
public class UserCardImportVo {

    @ExcelProperty("手机号/openId")
    private String phoneOrOpenId;
}
