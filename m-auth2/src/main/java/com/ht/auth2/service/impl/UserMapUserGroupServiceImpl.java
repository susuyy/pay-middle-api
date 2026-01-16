package com.ht.auth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.auth2.entity.*;
import com.ht.auth2.mapper.UserMapUserGroupMapper;
import com.ht.auth2.result.AddMapException;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.service.UserGroupsService;
import com.ht.auth2.service.UserMapUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.log.LogInputStream;

import java.util.Date;
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
public class UserMapUserGroupServiceImpl extends ServiceImpl<UserMapUserGroupMapper, UserMapUserGroup> implements UserMapUserGroupService {

    @Autowired
    private UserGroupsService userGroupsService;

    /**
     * 根据userId查询用户所在分组
     * @param userId
     * @return
     */
    @Override
    public List<UserMapUserGroup> queryByUserId(Long userId) {
        return this.baseMapper.selectByUserId(userId);
    }

    @Override
    public void addUserGroup(Long userId, String groupCode,String state) {
        UserMapUserGroup userMapUserGroupQuery = queryByUserIdGroupCode(userId, groupCode);
        if (userMapUserGroupQuery!=null){
            throw new AddMapException(ResultTypeEnum.MAP_EXIST);
        }
        UserMapUserGroup userMapUserGroup = new UserMapUserGroup();
        userMapUserGroup.setUserId(userId);
        userMapUserGroup.setGroupCode(groupCode);
        userMapUserGroup.setState(state);
        userMapUserGroup.setCreateAt(new Date());
        userMapUserGroup.setUpdateAt(new Date());
        save(userMapUserGroup);
    }

    public UserMapUserGroup queryByUserIdGroupCode(Long userId, String groupCode){
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("group_code",groupCode);
        queryWrapper.eq("user_id",userId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户id 查询用户分组
     * @param userId
     * @return
     */
    @Override
    public List<UserGroupsTree>queryUserGroupTreeList(Long userId) {
        QueryWrapper<UserMapUserGroup> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<UserMapUserGroup> list = this.baseMapper.selectList(queryWrapper);
        Set<String> set = new HashSet<>();
        for (UserMapUserGroup userMapUserGroup : list) {
            set.add(userMapUserGroup.getGroupCode());
        }
        List<UserGroupsTree> groupList=userGroupsService.queryGroupTreeList(set);
        return groupList;
    }

}
