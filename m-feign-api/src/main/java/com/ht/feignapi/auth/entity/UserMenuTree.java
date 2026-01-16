package com.ht.feignapi.auth.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserMenuTree implements Serializable {

    private static final long serialVersionUID=1L;


    private Long id;

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单分组
     */
    private String menuGroupCode;

    /**
     * 父类菜单编码
     */
    private String parentMenuCode;

    /**
     * 菜单等级
     */
    private String level;

    /**
     * 菜单状态
     */
    private String state;

    /**
     * 菜单url
     */
    private String url;

    private String path;

    private String component;

    private String icon;

    private String name;

//    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    private String hideInMenu;

    private Date createAt;

    private Date updateAt;

    /**
     * 菜单子节点
     */
    private List<UserMenuTree> children;

    /**
     * 菜单子节点
     */
    private List<UserMenuTree> routes;

    /**
     * 类别
     */
    private String type;

}
