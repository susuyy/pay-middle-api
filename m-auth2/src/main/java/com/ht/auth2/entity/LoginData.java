package com.ht.auth2.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginData implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * openid
     */
    private String openid;
}
