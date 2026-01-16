package com.ht.auth2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.auth2.constant.UserConstant;
import com.ht.auth2.entity.UserGroups;
import com.ht.auth2.entity.UserGroupsTree;
import com.ht.auth2.entity.UserMapUserGroup;
import com.ht.auth2.result.Result;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.service.UserMapUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@RestController
@RequestMapping("/userMapUserGroup")
@CrossOrigin(allowCredentials = "true")
public class UserMapUserGroupController {

    @Autowired
    private UserMapUserGroupService userMapUserGroupService;

    /**
     * 分配用户与 分组 关系
     * @param userMapUserGroup
     * @return
     */
    @PostMapping("/add")
    public Result mapUser(@RequestBody UserMapUserGroup userMapUserGroup){
        userMapUserGroupService.addUserGroup(userMapUserGroup.getUserId(),userMapUserGroup.getGroupCode(), UserConstant.NORMAL);
        return new Result(ResultTypeEnum.SERVICE_SUCCESS.getCode(),ResultTypeEnum.SERVICE_SUCCESS.getMessage());
    }

    /**
     *  根据用户id 查询用户对应的分组 (待调整)
     * @param userId
     * @return
     */
    @GetMapping("/queryUserGroupList")
    public List<UserGroupsTree> queryUserGroupList(@RequestParam String userId){
        List<UserGroupsTree> list=userMapUserGroupService.queryUserGroupTreeList(Long.parseLong(userId));
        if (list==null){
            list=new ArrayList<>();
        }
        return list;
    }

    /**
     * 用户解绑分组
     * @param userId
     * @param groupCode
     */
    @DeleteMapping("/deleteMapUserGroup")
    public void deleteMapUserGroup(@RequestParam("userId")String userId,@RequestParam("groupCode")String groupCode){
        QueryWrapper<UserMapUserGroup> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("group_code",groupCode);
        userMapUserGroupService.remove(queryWrapper);
    }

}

