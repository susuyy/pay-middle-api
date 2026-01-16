package com.ht.auth2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserRoles;

import java.util.List;
import java.util.Set;


/**
 * <p>
 * 角色定义 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
public interface UserRolesService extends IService<UserRoles> {

    /**
     * 根据账号获取用户角色集
     * @param account
     * @return
     */
    List<UserRoles> queryRoleListByAccount(String account);

    /**
     * 通过编码集合 查询类别返回
     * @param set
     * @return
     */
    Set<String> querySetType(Set<String> set);

    /**
     * 新增 角色
     * @param roleName
     * @param roleType
     * @param state
     * @param appCode
     * @param appName
     */
    void addUserRoles(String roleName, String roleType, String state, String appCode, String appName);

    /**
     * 通过查询条件查询角色列表(分页)
     * @param searchData
     * @return
     */
    IPage queryBySearchData(SearchData searchData);

    /**
     * 根据编码集 查询角色集
     * @param set
     * @return
     */
    List<UserRoles> queryRoleListByCode(Set<String> set);

    /**
     * 根据appCode查询列表,不分页
     * @param appCode
     * @return
     */
    List<UserRoles> listByAppCode(String appCode);
}
