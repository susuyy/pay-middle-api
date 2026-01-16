package com.ht.feignapi.tonglian.admin.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/17 10:11
 */
@Data
public class LoginVo {

    @NotNull(message = "用户名不能为空")
    private String userName;

    @NotNull(message = "密码不能为空")
    private String password;

    @NotNull(message = "机构号不能为空")
    private String merchantCode;

    /**
     * 扩展系统标识
     */
    private String systemCode;
}
