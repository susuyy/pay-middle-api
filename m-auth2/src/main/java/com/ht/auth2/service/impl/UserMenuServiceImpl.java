package com.ht.auth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserMenu;
import com.ht.auth2.entity.UserMenuTree;
import com.ht.auth2.mapper.UserMenuMapper;
import com.ht.auth2.service.UserMapRoleMenuService;
import com.ht.auth2.service.UserMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@Service
public class UserMenuServiceImpl extends ServiceImpl<UserMenuMapper, UserMenu> implements UserMenuService {

    @Autowired
    private UserMapRoleMenuService userMapRoleMenuService;

    /**
     * 查询用户展示的菜单
     *
     * @param account
     * @return
     */
    @Override
    public List<UserMenu> queryMenuByAccount(String account) {
        Set<String> menuCodeSet = userMapRoleMenuService.queryMenuCodeSet(account);
        QueryWrapper queryWrapper = new QueryWrapper();
        if (menuCodeSet != null && menuCodeSet.size() > 0) {
            queryWrapper.in("menu_code", menuCodeSet);
            return this.baseMapper.selectList(queryWrapper);
        }
        return null;
    }

    /**
     * 新增菜单
     *
     * @param menuName
     * @param level
     * @param parentMenuCode
     * @param state
     * @param appCode
     * @param appName
     */
    @Override
    public void addUserMenu(String menuName, String level, String parentMenuCode, String state, String appCode, String appName) {
        UserMenu userMenu = new UserMenu();
        userMenu.setAppCode(appCode);
        userMenu.setAppName(appName);
        userMenu.setState(state);
        userMenu.setLevel(level);
        userMenu.setParentMenuCode(parentMenuCode);
        userMenu.setMenuName(menuName);
        userMenu.setMenuCode(IdWorker.getIdStr());
        this.baseMapper.insert(userMenu);
    }

    /**
     * 根据appCode查询菜单树形结构
     *
     * @param appCode
     * @param userId
     * @param account
     * @return
     */
    @Override
    public List<UserMenuTree> queryMenuTreeListByAppCode(String appCode, String userId, String account) {
        Set<String> set = userMapRoleMenuService.queryMenuCodeSet(account);
        QueryWrapper<UserMenu> queryWrapper = new QueryWrapper<>();
        QueryWrapper<UserMenu> queryWrapperNew = new QueryWrapper<>();
        if (set != null && set.size() > 0) {
            queryWrapper.in("menu_code", set);
//            List<UserMenuTree> userMenuTrees = this.baseMapper.queryUserMenuTreeNoAll(appCode, queryWrapper);

            List<UserMenu> list = this.baseMapper.selectList(queryWrapper);
//            Set<String> parentMenuCode =new HashSet<>();
            for (UserMenu userMenu : list) {
                if (!userMenu.getParentMenuCode().equals("0")) {
                    set.add(userMenu.getParentMenuCode());
                    // 处理三级菜单 拉取二级菜单 拉取顶层菜单
                    if ("3".equals(userMenu.getLevel())){
                        String twoParentMenuCode = userMenu.getParentMenuCode();
                        UserMenu userMenuQuery = queryByMenuCode(twoParentMenuCode);
                        set.add(userMenuQuery.getParentMenuCode());
                    }
                }
            }
            queryWrapperNew.in("menu_code", set);
            List<UserMenuTree> userMenuTrees = this.baseMapper.queryUserMenuTreeNoAll(appCode, queryWrapperNew);

            List<UserMenuTree> userMenuTreesEnd = findSonList(userMenuTrees, queryWrapperNew);
            return userMenuTreesEnd;
        }
        return null;
    }

    private UserMenu queryByMenuCode(String menuCode) {
        QueryWrapper<UserMenu> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("menu_code",menuCode);
        return getOne(queryWrapper,false);
    }

    /**
     * 查询子菜单树
     *
     * @param userMenuTrees
     * @param queryWrapper
     * @return
     */
    public List<UserMenuTree> findSonList(List<UserMenuTree> userMenuTrees, QueryWrapper queryWrapper) {
        for (UserMenuTree userMenuTree : userMenuTrees) {
            List<UserMenuTree> sonMenuTreeList = this.baseMapper.querySonMenuTree(userMenuTree.getMenuCode(), queryWrapper);
            if (sonMenuTreeList != null) {
                userMenuTree.setRoutes(sonMenuTreeList);
                userMenuTree.setChildren(sonMenuTreeList);
                findSonList(sonMenuTreeList, queryWrapper);
            }
        }
        return userMenuTrees;
    }

    /**
     * 根据条件查询菜单列表分页
     *
     * @param searchData
     * @return
     */
    @Override
    public IPage queryMenuListBySearchData(SearchData searchData) {
        QueryWrapper queryWrapper = new QueryWrapper();
        IPage iPage = new Page<>(searchData.getPageNo(), searchData.getPageSize());
        if (!StringUtils.isEmpty(searchData.getCode())) {
            queryWrapper.eq("menu_code", searchData.getCode());
        }
        if (!StringUtils.isEmpty(searchData.getName())) {
            queryWrapper.like("menu_name", searchData.getName());
        }
        if (!StringUtils.isEmpty(searchData.getStartTime()) && !StringUtils.isEmpty(searchData.getEndTime())) {
            queryWrapper.between("create_at", searchData.getStartTime(), searchData.getEndTime());
        }
        queryWrapper.eq("app_code", searchData.getAppCode());
        queryWrapper.orderByDesc("create_at");
        return this.baseMapper.selectPage(iPage, queryWrapper);
    }

    /**
     * 根据菜单编码集 查询菜单集
     *
     * @param set
     * @return
     */
    @Override
    public List<UserMenu> queryMenuList(Set<String> set) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (set != null && set.size() > 0) {
            queryWrapper.in("menu_code", set);
            return this.baseMapper.selectList(queryWrapper);
        }
        return null;
    }

    /**
     * 根据appCode 查询菜单集
     *
     * @param appCode
     * @return
     */
    @Override
    public List<UserMenuTree> listByAppCodeTree(String appCode) {
        return this.baseMapper.queryUserMenuTreeAll(appCode);
    }
}
