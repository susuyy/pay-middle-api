package com.ht.feignapi.tonglian.admin.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/2 16:02
 */
@Data
public class MemberImportVo {
    @ExcelProperty("卡号")
    private String cardCode;

    @ExcelProperty("密码")
    private String password;

    @ExcelProperty("手机号")
    private String tel;

    @ExcelProperty("余额")
    private String accountBalance;

    @ExcelProperty("积分")
    private String point;

    @ExcelProperty("姓名")
    private String realName;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("OpenId")
    private String OpenId;

    @ExcelProperty("生日")
    private String birthday;

    @ExcelProperty("年龄")
    private Short age;

    @ExcelProperty("车牌号")
    private String plateNumbers;

    @ExcelProperty("职业")
    private String job;

    @ExcelProperty("年收入")
    private String annualIncome;

    @ExcelProperty("汽车品牌")
    private String carBrand;

    @ExcelProperty("推荐人ID")
    private Long recommendPersonId;

    @ExcelProperty("推荐人名称")
    private String recommendPersonName;

    @ExcelProperty("推荐门店ID")
    private Long recommendMerchantId;

    @ExcelProperty("推荐门店名称")
    private String recommendMerchantName;
}
