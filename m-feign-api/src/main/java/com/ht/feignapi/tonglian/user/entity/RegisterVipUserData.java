package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员注册前端提交参数
 *
 * @author suyangy
 * @since 2020-06-15
 */
@Data
public class RegisterVipUserData implements Serializable {

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 手机号收到的验证码
     */
    private String authCode;

    /**
     * 卡号编码
     */
    private String cardCode;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 密码
     */
    private String password;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 生日
     */
    private String birthday;

}
