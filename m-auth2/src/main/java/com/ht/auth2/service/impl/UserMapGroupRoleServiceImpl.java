package com.ht.auth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.auth2.entity.UserMapGroupRole;
import com.ht.auth2.entity.UserMapUserGroup;
import com.ht.auth2.entity.UserRoles;
import com.ht.auth2.entity.UserUsers;
import com.ht.auth2.mapper.UserMapGroupRoleMapper;
import com.ht.auth2.result.AddMapException;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.service.UserMapGroupRoleService;
import com.ht.auth2.service.UserMapUserGroupService;
import com.ht.auth2.service.UserRolesService;
import com.ht.auth2.service.UserUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
public class UserMapGroupRoleServiceImpl extends ServiceImpl<UserMapGroupRoleMapper, UserMapGroupRole> implements UserMapGroupRoleService {


    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private UserMapUserGroupService userMapUserGroupService;

    @Autowired
    private UserRolesService userRolesService;

    /**
     * 根据分组集查询角色集
     * @param userGroupList
     * @return
     */
    @Override
    public List<UserMapGroupRole> queryByGroupCodeList(List<UserMapUserGroup> userGroupList) {
        List<String> groupCodeList = new ArrayList<>();
        for (UserMapUserGroup userMapUserGroup : userGroupList) {
            groupCodeList.add(userMapUserGroup.getGroupCode());
        }
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.in("group_code",groupCodeList);
        return this.baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据账号 查询用户 角色集编码code (去重)
     * @param account
     * @return
     */
    @Override
    public Set<String> queryRoleCodeSet(String account) {
        UserUsers userUsers = userUsersService.queryByAccountNoCode(account);
        List<UserMapUserGroup> userGroupList = userMapUserGroupService.queryByUserId(userUsers.getId());
        List<UserMapGroupRole> groupRoleList= queryByGroupCodeList(userGroupList);
        List<String> roleCodeList = new ArrayList<>();
        for (UserMapGroupRole userMapGroupRole : groupRoleList) {
            roleCodeList.add(userMapGroupRole.getRoleCode());
        }
        Set<String> roleCodeSet =new HashSet();
        for (String roleCode : roleCodeList) {
            roleCodeSet.add(roleCode);
        }
        return roleCodeSet;
    }

    /**
     * 新增分组 与 角色 关系
     * @param groupCode
     * @param roleCode
     * @param state
     */
    @Override
    public void addUserMapGroupRole(String groupCode, String roleCode, String state) {
        if (StringUtils.isEmpty(groupCode) || StringUtils.isEmpty(roleCode)){
            return;
        }
        UserMapGroupRole userMapGroupRoleQuery = queryByGroupRoleCode(groupCode, roleCode);
        if (userMapGroupRoleQuery!=null){
            throw new AddMapException(ResultTypeEnum.MAP_EXIST);
        }
        UserMapGroupRole userMapGroupRole = new UserMapGroupRole();
        userMapGroupRole.setRoleCode(roleCode);
        userMapGroupRole.setGroupCode(groupCode);
        userMapGroupRole.setState(state);
        this.baseMapper.insert(userMapGroupRole);
    }

    public UserMapGroupRole queryByGroupRoleCode(String groupCode, String roleCode){
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("group_code",groupCode);
        queryWrapper.eq("role_code",roleCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<UserRoles> queryGroupRoleList(String groupCode) {
        QueryWrapper<UserMapGroupRole> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("group_code",groupCode);
        List<UserMapGroupRole> userMapGroupRoles = this.baseMapper.selectList(queryWrapper);
        Set<String> set=new HashSet<>();
        for (UserMapGroupRole userMapGroupRole : userMapGroupRoles) {
            set.add(userMapGroupRole.getRoleCode());
        }
        return userRolesService.queryRoleListByCode(set);
    }


}
