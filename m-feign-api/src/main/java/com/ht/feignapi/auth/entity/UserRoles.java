package com.ht.feignapi.auth.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 角色定义
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserRoles implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色类型
     */
    private String roleType;

    /**
     * 角色分组码
     */
    private String roleGroupCode;

    /**
     * 角色状态
     */
    private String roleState;


    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 应用名称
     */
    private String appName;


    private Date createAt;

    private Date updateAt;


}
