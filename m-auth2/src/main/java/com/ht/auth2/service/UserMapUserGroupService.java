package com.ht.auth2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.auth2.entity.UserGroups;
import com.ht.auth2.entity.UserGroupsTree;
import com.ht.auth2.entity.UserMapUserGroup;
import com.ht.auth2.entity.UserUsers;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
public interface UserMapUserGroupService extends IService<UserMapUserGroup> {

    /**
     * 根据userId 查询用户所在分组
     * @param userId
     * @return
     */
    List<UserMapUserGroup> queryByUserId(Long userId);

    /**
     * 添加用户分组
     * @param userId
     * @param groupCode
     * @param state
     */
    void addUserGroup(Long userId, String groupCode,String state);

    /**
     * 根据用户id 查询用户对应分组
     * @param userId
     * @return
     */
    List<UserGroupsTree> queryUserGroupTreeList(Long userId);
}
