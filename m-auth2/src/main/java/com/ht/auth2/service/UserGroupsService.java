package com.ht.auth2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserGroups;
import com.ht.auth2.entity.UserGroupsTree;

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
public interface UserGroupsService extends IService<UserGroups> {


    /**
     * 查询分组 树状结构
     * @return
     * @param appCode
     */
    List<UserGroupsTree> queryUserGroupsTree(String appCode);

    /**
     * 新增分组
     * @param userGroups
     */
    void addUserGroups(UserGroups userGroups);

    /**
     * 获取用户分组列表 根据条件筛选  分页
     * @param searchData
     * @return
     */
    IPage queryGroupListBySearchData(SearchData searchData);

    /**
     * 根据分组编码集合 获取分组列表
     * @param set
     * @return
     */
    List<UserGroupsTree> queryGroupTreeList(Set<String> set);

    /**
     * 根据appCode 查询分组集
     * @param appCode
     * @return
     */
    List<UserGroups> listByAppCode(String appCode);

    /**
     * 查询用户组
     * @param userId
     * @return
     */
    List<UserGroups> queryUserGroupsList(Long userId);
}
