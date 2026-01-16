package com.ht.auth2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserMenu;
import com.ht.auth2.entity.UserMenuTree;

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
public interface UserMenuService extends IService<UserMenu> {

    /**
     * 查询用户展示的菜单
     * @param account
     * @return
     */
    List<UserMenu> queryMenuByAccount(String account);

    /**
     * 新郑菜单
     * @param menuName
     * @param level
     * @param parentMenuCode
     * @param state
     * @param appCode
     * @param appName
     */
    void addUserMenu(String menuName, String level, String parentMenuCode, String state, String appCode, String appName);

    /**
     * 根据appCode 查询菜单树形结构
     * @param appCode
     * @param userId
     * @param account
     * @return
     */
    List<UserMenuTree> queryMenuTreeListByAppCode(String appCode, String userId, String account);

    /**
     * 根据条件查询菜单列表分页
     * @param searchData
     * @return
     */
    IPage queryMenuListBySearchData(SearchData searchData);

    /**
     * 根据菜单编码集 查询菜单集
     * @param set
     * @return
     */
    List<UserMenu> queryMenuList(Set<String> set);

    /**
     * 根据appCode 查询菜单集
     * @param appCode
     * @return
     */
    List<UserMenuTree> listByAppCodeTree(String appCode);
}
