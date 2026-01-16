package com.ht.auth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.ht.auth2.constant.UserConstant;
import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserGroups;
import com.ht.auth2.entity.UserGroupsTree;
import com.ht.auth2.entity.UserMapUserGroup;
import com.ht.auth2.mapper.UserGroupsMapper;
import com.ht.auth2.service.UserGroupsService;
import com.ht.auth2.service.UserMapUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
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
public class UserGroupsServiceImpl extends ServiceImpl<UserGroupsMapper, UserGroups> implements UserGroupsService {


    @Autowired
    private UserMapUserGroupService userMapUserGroupService;

    /**
     * 查询分组 树状结构
     * @return
     * @param appCode
     */
    @Override
    public List<UserGroupsTree> queryUserGroupsTree(String appCode) {
        return this.baseMapper.queryUserGroupsTree(appCode);
    }

    /**
     * 新增分组
     * @param userGroups
     */
    @Override
    public void addUserGroups(UserGroups userGroups) {
        userGroups.setAppCode(userGroups.getAppCode());
        userGroups.setAppName(userGroups.getAppName());
        userGroups.setGroupCode(IdWorker.getIdStr());
        userGroups.setState(UserConstant.NORMAL);

        if (StringUtils.isEmpty(userGroups.getParentGroupCode())){
            userGroups.setParentGroupCode("0");
        }else {
            userGroups.setParentGroupCode(userGroups.getParentGroupCode());
        }

        userGroups.setCreateAt(new Date());
        this.baseMapper.insert(userGroups);
    }

    @Override
    public IPage queryGroupListBySearchData(SearchData searchData) {
        QueryWrapper queryWrapper=new QueryWrapper();
        IPage iPage = new Page<>(searchData.getPageNo(), searchData.getPageSize());
        if (!StringUtils.isEmpty(searchData.getCode())){
            queryWrapper.eq("group_code",searchData.getCode());
        }
        if (!StringUtils.isEmpty(searchData.getName())){
            queryWrapper.like("group_name",searchData.getName());
        }
        if (!StringUtils.isEmpty(searchData.getStartTime()) && !StringUtils.isEmpty(searchData.getEndTime())){
            queryWrapper.between("create_at",searchData.getStartTime(),searchData.getEndTime());
        }
        queryWrapper.eq("app_code",searchData.getAppCode());
        queryWrapper.orderByDesc("create_at");
        return this.baseMapper.selectPage(iPage, queryWrapper);
    }

    @Override
    public List<UserGroupsTree> queryGroupTreeList(Set<String> set) {
        QueryWrapper queryWrapper=new QueryWrapper();
        if (set!=null && set.size()>0) {
            queryWrapper.in("group_code", set);
            return this.baseMapper.selectGroupTreeList(queryWrapper);
        }else {
            return null;
        }
    }

    /**
     * 根据appCode查询分组集
     * @param appCode
     * @return
     */
    @Override
    public List<UserGroups> listByAppCode(String appCode) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("app_code",appCode);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserGroups> queryUserGroupsList(Long userId) {
        List<UserMapUserGroup> userMapUserGroups = userMapUserGroupService.queryByUserId(userId);
        if ( userMapUserGroups.size()>0) {
            List<String> groupCodeList = new ArrayList<>();
            for (UserMapUserGroup userMapUserGroup : userMapUserGroups) {
                groupCodeList.add(userMapUserGroup.getGroupCode());
            }
            QueryWrapper<UserGroups> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("group_code", groupCodeList);
            return this.baseMapper.selectList(queryWrapper);
        }else {
            return new ArrayList<UserGroups>();
        }
    }
}
