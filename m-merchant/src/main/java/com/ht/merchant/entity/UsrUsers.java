package com.ht.merchant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author suyangy
 * @since 2020-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UsrUsers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
     * 年收入
     */
    private String annualIncome;

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
     * 年龄
     */
    private Short age;

    /**
     * 汽车品牌
     */
    private String carBrand;

    private Date createAt;

    private Date updateAt;

}
