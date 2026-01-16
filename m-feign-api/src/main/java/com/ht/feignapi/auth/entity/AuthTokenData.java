package com.ht.feignapi.auth.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方使用 本项目无用
 */
@Data
public class AuthTokenData implements Serializable {
    private String tenant_id;

    private String account;

    private String user_name;

    private String nick_name;

    private String role_name;

    private String avatar;

    private String access_token;

    private String refresh_token;

    private String token_type;

    private Integer expires_in;

    private String license;
}
