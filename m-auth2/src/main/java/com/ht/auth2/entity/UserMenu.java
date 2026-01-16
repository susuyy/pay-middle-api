package com.ht.auth2.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class UserMenu implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
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

    private String url;

    private String path;

    private String component;

    private String icon;

    private String name;

    private String hideInMenu;

    private Date createAt;

    private Date updateAt;


}
