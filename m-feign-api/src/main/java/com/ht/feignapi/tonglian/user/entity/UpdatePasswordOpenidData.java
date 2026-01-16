package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户修改密码前端提交参数
 *
 * @author suyangy
 * @since 2020-06-15
 */
@Data
public class UpdatePasswordOpenidData implements Serializable {

    /**
     * 用户id
     */
    private String openid;

    /**
     * 用户手机号
     */
    private String phoneNum;

    /**
     * 新密码
     */
    private String newPassword;
}
