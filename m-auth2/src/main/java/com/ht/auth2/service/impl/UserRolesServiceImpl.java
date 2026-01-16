package com.ht.auth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserRoles;
import com.ht.auth2.mapper.UserRolesMapper;
import com.ht.auth2.service.UserMapGroupRoleService;
import com.ht.auth2.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色定义 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@Service
public class UserRolesServiceImpl extends ServiceImpl<UserRolesMapper, UserRoles> implements UserRolesService {

    @Autowired
    private UserMapGroupRoleService userMapGroupRoleService;

    /**
     * 根据账号获取用户角色集
     *
     * @param account
     * @return
     */
    @Override
    public List<UserRoles> queryRoleListByAccount(String account) {
        Set set = userMapGroupRoleService.queryRoleCodeSet(account);
        QueryWrapper queryWrapper = new QueryWrapper();
        if (set!=null && set.size()>0) {
            queryWrapper.in("role_code", set);
            return this.baseMapper.selectList(queryWrapper);
        }
        return null;
    }

    /**
     * 通过编码集合 查询类别返回
     * @param set
     * @return
     */
    @Override
    public Set<String> querySetType(Set<String> set) {
        HashSet<String> typeSet = new HashSet<>();
        for (String roleCode : set) {
            QueryWrapper queryWrapper=new QueryWrapper();
            queryWrapper.eq("role_code",roleCode);
            UserRoles userRoles = this.baseMapper.selectOne(queryWrapper);
            typeSet.add(userRoles.getRoleType());
        }
        return typeSet;
    }

    /**
     * 新增角色
     * @param roleName
     * @param roleType
     * @param state
     * @param appCode
     * @param appName
     */
    @Override
    public void addUserRoles(String roleName, String roleType, String state, String appCode, String appName) {
        UserRoles userRoles = new UserRoles();
        userRoles.setRoleCode(IdWorker.getIdStr());
        userRoles.setRoleName(roleName);
        userRoles.setRoleType(roleType);
        userRoles.setRoleState(state);
        userRoles.setAppCode(appCode);
        userRoles.setAppName(appName);
        this.baseMapper.insert(userRoles);
    }

    @Override
    public IPage queryBySearchData(SearchData searchData) {
        QueryWrapper queryWrapper=new QueryWrapper();
        IPage iPage = new Page<>(searchData.getPageNo(), searchData.getPageSize());
        if (!StringUtils.isEmpty(searchData.getCode())){
            queryWrapper.eq("role_code",searchData.getCode());
        }
        if (!StringUtils.isEmpty(searchData.getName())){
            queryWrapper.like("role_name",searchData.getName());
        }
        if (!StringUtils.isEmpty(searchData.getType())){
            queryWrapper.eq("role_type",searchData.getType());
        }
        if (!StringUtils.isEmpty(searchData.getStartTime()) && !StringUtils.isEmpty(searchData.getEndTime())){
            queryWrapper.between("create_at",searchData.getStartTime(),searchData.getEndTime());
        }
        queryWrapper.eq("app_code",searchData.getAppCode());
        queryWrapper.orderByDesc("create_at");
        return this.baseMapper.selectPage(iPage, queryWrapper);
    }

    /**
     * 根据编码集 查询角色集
     * @param set
     * @return
     */
    @Override
    public List<UserRoles> queryRoleListByCode(Set<String> set) {
        QueryWrapper queryWrapper=new QueryWrapper();
        if (set!=null && set.size()>0) {
            queryWrapper.in("role_code", set);
            return this.baseMapper.selectList(queryWrapper);
        }else {
            return null;
        }
    }

    @Override
    public List<UserRoles> listByAppCode(String appCode) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("app_code",appCode);
        return this.baseMapper.selectList(queryWrapper);
    }


}
