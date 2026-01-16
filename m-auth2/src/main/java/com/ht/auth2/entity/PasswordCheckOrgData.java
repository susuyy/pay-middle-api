package com.ht.auth2.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordCheckOrgData implements Serializable {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户原密码
     */
    private String orgPassword;

    /**
     * 用户新密码
     */
    private String newPassword;
}
