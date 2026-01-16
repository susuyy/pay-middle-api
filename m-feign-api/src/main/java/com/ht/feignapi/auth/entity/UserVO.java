package com.ht.feignapi.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserVO implements Serializable {
    /**
     * 用户主键
     */
    private String id;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建部门
     */
    private String createDept;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 删除标识
     */
    private Integer isDeleted;

    /**
     * 租户id
     */
    private String tenantId;


    /**
     * 用户编号
     */
    private String code;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String name;

    /**
     * 真名
     */
    private String realName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String phone;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 岗位id
     */
    private String postId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 性别名称
     */
    private String sexName;

    /**
     * 所属系统名称
     */
    private List clientNameList;

    /**
     * 所属系统id
     */
    private List clientIdList;

    /**
     * 人员类别
     */
    private String userType;



}
