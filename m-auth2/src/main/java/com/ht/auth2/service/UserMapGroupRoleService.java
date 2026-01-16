package com.ht.auth2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.auth2.entity.UserMapGroupRole;
import com.ht.auth2.entity.UserMapUserGroup;
import com.ht.auth2.entity.UserRoles;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
public interface UserMapGroupRoleService extends IService<UserMapGroupRole> {

    /**
     * 根据分组集 查询角色集
     * @param userGroupList
     * @return
     */
    List<UserMapGroupRole> queryByGroupCodeList(List<UserMapUserGroup> userGroupList);

    /**
     * 根据账号 查询用户的角色编码 (去重)
     * @param account
     * @return
     */
    Set<String> queryRoleCodeSet(String account);

    /**
     * 创建分组 与 角色的关联关系
     * @param groupCode
     * @param roleCode
     * @param state
     */
    void addUserMapGroupRole(String groupCode, String roleCode, String state);

    /**
     * 根据分组编码 查询对应的角色集
     * @param groupCode
     * @return
     */
    List<UserRoles> queryGroupRoleList(String groupCode);
}
