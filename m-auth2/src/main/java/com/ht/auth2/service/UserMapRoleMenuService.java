package com.ht.auth2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.auth2.entity.UserMapRoleMenu;
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
public interface UserMapRoleMenuService extends IService<UserMapRoleMenu> {

    /**
     * 查询用户的菜单编码 去重
     * @param account
     * @return
     */
    Set<String> queryMenuCodeSet(String account);

    /**
     * 新增菜单角色关系
     * @param roleCode
     * @param menuCode
     * @param state
     */
    void createUserMapRoleMenu(String roleCode, String menuCode, String state);

    /**
     * 根据角色编码 查询菜单集
     * @param roleCode
     * @return
     */
    List<UserMenu> queryRoleMenuList(String roleCode);

    /**
     * 添加角色 与菜单集对应
     * @param roleCode
     * @param menuCodeList
     */
    void addRoleMenuList(String roleCode, List<String> menuCodeList);

}
