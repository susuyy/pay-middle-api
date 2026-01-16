package com.ht.auth2.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserGroups;
import com.ht.auth2.entity.UserGroupsTree;
import com.ht.auth2.entity.UserMenu;
import com.ht.auth2.result.Result;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.service.UserGroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.acl.Group;
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
@RequestMapping("/userGroups")
@CrossOrigin(allowCredentials = "true")
public class UserGroupsController {

    @Autowired
    private UserGroupsService userGroupsService;

    /**
     * 获取所有分组列表
     * @param appCode
     * @return
     */
    @GetMapping("/groupList/{appCode}")
    public List<UserGroupsTree> groupList(@PathVariable("appCode")String appCode) {
        return userGroupsService.queryUserGroupsTree(appCode);
    }

    /**
     * 添加组织架构 分组
     * @param userGroups
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody UserGroups userGroups){
        userGroupsService.addUserGroups(userGroups);
        return new Result(ResultTypeEnum.SERVICE_SUCCESS.getCode(),ResultTypeEnum.SERVICE_SUCCESS.getMessage());
    }

    /**
     * 分页获取用户分组列表
     * @param searchData
     * @return
     */
    @PostMapping("/getGroupList")
    public IPage getGroupList(@RequestBody SearchData searchData){
        return userGroupsService.queryGroupListBySearchData(searchData);
    }

    /**
     * 更新分组信息
     * @param userGroups
     */
    @PostMapping("/updateUserGroups")
    public void updateUserGroups(@RequestBody UserGroups userGroups){
        userGroupsService.updateById(userGroups);
    }

    /**
     * 根据id查询分组信息
     * @param id
     * @return
     */
    @GetMapping("/getUserGroupsById")
    public UserGroups getUserGroupsById(@RequestParam("id") String id){
        return userGroupsService.getById(Long.parseLong(id));
    }

    /**
     * 根据appCode 查询 分组集
     * @param appCode
     * @return
     */
    @GetMapping("/list")
    public List<UserGroups> list(@RequestParam("appCode") String appCode){
        return userGroupsService.listByAppCode(appCode);
    }

}

