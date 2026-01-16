package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserUsersVO implements Serializable {

    private Long id;

    /**
     * 微信openid
     */
    private String openId;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话
     */
    private String tel;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像地址
     */
    private String headPicUrl;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 证件类型
     */
    private String idCardType;

    /**
     * 证件号
     */
    private String idCardNum;

    /**
     * 邮件地址
     */
    private String mailAddress;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 婚姻
     */
    private String marriage;

    /**
     * 职业
     */
    private String job;


    /**
     * 是否会员
     */
    private Boolean isVip;

    /**
     * 用户积分
     */
    private Integer point;

    /**
     * 卡券数量
     */
    private Integer cardNum;

    /**
     * 钱包余额
     */
    private BigDecimal money;

    /**
     * 会员等级
     */
    private String vipType;

}
