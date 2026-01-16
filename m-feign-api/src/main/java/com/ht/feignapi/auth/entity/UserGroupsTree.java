package com.ht.feignapi.auth.entity;

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
public class UserGroupsTree implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 分组编码
     */
    private String groupCode;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 备注
     */
    private String comments;

    /**
     * 拓展字段1
     */
    private String refText01;

    /**
     * 拓展字段2
     */
    private String refText02;

    /**
     * 父类分组码
     */
    private String parentGroupCode;

    /**
     * 状态
     */
    private String state;

    private Date createAt;

    private Date updateAt;

    /**
     * 分组子节点
     */
    private List<UserGroupsTree> children;

}
