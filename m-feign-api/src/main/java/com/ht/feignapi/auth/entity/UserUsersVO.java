package com.ht.feignapi.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@Data
public class UserUsersVO implements Serializable {

    private static final long serialVersionUID=1L;


    private Long id;

    /**
     * 用户token
     */
    private String token;

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
     * 邮箱
     */
    private String mail;

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
     * 汽车品牌
     */
    private String carBrand;

    /**
     * 年收入
     */
    private String annualIncome;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 职业
     */
    private String job;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 婚姻状况
     */
    private String marriage;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

    /**
     * 用户角色集
     */
    private List<UserRoles> userRolesList;

    /**
     * 用户菜单集
     */
    private List<UserMenu> userMenuList;

    /**
     * 用户分组集
     */
    private List<UserGroups> userGroupsList;
}
