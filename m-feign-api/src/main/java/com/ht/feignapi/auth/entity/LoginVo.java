package com.ht.feignapi.auth.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2021/9/26 12:05
 */
@Data
public class LoginVo implements Serializable {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;

    private String merchantCode;

}
