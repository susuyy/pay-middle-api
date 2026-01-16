package com.ht.auth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.auth2.constant.UserConstant;
import com.ht.auth2.entity.*;
import com.ht.auth2.mapper.UserMapRoleMenuMapper;
import com.ht.auth2.result.AddMapException;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.service.UserMapGroupRoleService;
import com.ht.auth2.service.UserMapRoleMenuService;
import com.ht.auth2.service.UserMenuService;
import com.ht.auth2.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@Service
public class UserMapRoleMenuServiceImpl extends ServiceImpl<UserMapRoleMenuMapper, UserMapRoleMenu> implements UserMapRoleMenuService {

    @Autowired
    private UserMapGroupRoleService userMapGroupRoleService;

    @Autowired
    private UserMenuService userMenuService;


    /**
     * 查询用户的菜单编码 去重
     */
    @Override
    public Set<String> queryMenuCodeSet(String account) {
        Set<String> roleCodeSet = userMapGroupRoleService.queryRoleCodeSet(account);
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.in("role_code",roleCodeSet);
        List<UserMapRoleMenu> list = this.baseMapper.selectList(queryWrapper);
        HashSet<String> menuCodeSet = new HashSet<>();
        for (UserMapRoleMenu userMapRoleMenu : list) {
            menuCodeSet.add(userMapRoleMenu.getMenuCode());
        }
        return menuCodeSet;
    }

    /**
     * 新增角色菜单关系
     * @param roleCode
     * @param menuCode
     * @param state
     */
    @Override
    public void createUserMapRoleMenu(String roleCode, String menuCode, String state) {
        UserMapRoleMenu userMapRoleMenuQuery = queryByMenuRoleCode(roleCode, menuCode);
        if (userMapRoleMenuQuery!=null){
            throw new AddMapException(ResultTypeEnum.MAP_EXIST);
        }
        UserMapRoleMenu userMapRoleMenu = new UserMapRoleMenu();
        userMapRoleMenu.setMenuCode(menuCode);
        userMapRoleMenu.setRoleCode(roleCode);
        userMapRoleMenu.setState(state);
        this.baseMapper.insert(userMapRoleMenu);
    }

    public UserMapRoleMenu queryByMenuRoleCode(String roleCode, String menuCode){
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("role_code",roleCode);
        queryWrapper.eq("menu_code",menuCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据角色编码查询菜单集
     * @param roleCode
     * @return
     */
    @Override
    public List<UserMenu> queryRoleMenuList(String roleCode) {
        QueryWrapper<UserMapRoleMenu> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("role_code",roleCode);
        List<UserMapRoleMenu> userMapRoleMenus = this.baseMapper.selectList(queryWrapper);
        Set<String> set = new HashSet<>();
        for (UserMapRoleMenu userMapRoleMenu : userMapRoleMenus) {
            set.add(userMapRoleMenu.getMenuCode());
        }
        return userMenuService.queryMenuList(set);
    }


    /**
     * 添加角色与菜单集对应
     * @param roleCode
     * @param menuCodeList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleMenuList(String roleCode, List<String> menuCodeList) {
        this.baseMapper.deleteByRoleCode(roleCode);
        for (String menuCode : menuCodeList) {
            createUserMapRoleMenu(roleCode,menuCode, UserConstant.NORMAL);
        }
    }


}
