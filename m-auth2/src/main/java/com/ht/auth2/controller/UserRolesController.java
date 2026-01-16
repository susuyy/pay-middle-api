package com.ht.auth2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.auth2.constant.UserConstant;
import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserGroups;
import com.ht.auth2.entity.UserRoles;
import com.ht.auth2.result.Result;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.service.UserRolesService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色定义 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@RestController
@RequestMapping("/userRoles")
@CrossOrigin(allowCredentials = "true")
public class UserRolesController {

    @Autowired
    private UserRolesService userRolesService;

    /**
     * 添加角色
     * @param userRoles
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody UserRoles userRoles){
        userRolesService.addUserRoles(userRoles.getRoleName(),userRoles.getRoleType(), UserConstant.NORMAL,userRoles.getAppCode(),userRoles.getAppName());
        return new Result(ResultTypeEnum.SERVICE_SUCCESS.getCode(),ResultTypeEnum.SERVICE_SUCCESS.getMessage());
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable("id")String id){
        userRolesService.removeById(Long.parseLong(id));
        return new Result(ResultTypeEnum.SERVICE_SUCCESS.getCode(),ResultTypeEnum.SERVICE_SUCCESS.getMessage());
    }

    /**
     * 查询所有 角色列表
     * @return
     */
    @PostMapping("/roleList")
    public IPage getRoleList(@RequestBody SearchData searchData){
        IPage iPage=userRolesService.queryBySearchData(searchData);
        return iPage;
    }

    /**
     * 更新 角色 信息
     * @param userRoles
     */
    @PostMapping("/updateUserRole")
    public void updateUserRole(@RequestBody UserRoles userRoles){
        userRolesService.updateById(userRoles);
    }


    /**
     * 根据id查询角色信息
     * @param id
     * @return
     */
    @GetMapping("/getUserRoleById")
    public UserRoles getUserRoleById(@RequestParam("id") String id){
        return userRolesService.getById(Long.parseLong(id));
    }

    /**
     * 根据appCode 查询角色集
     * @param appCode
     * @return
     */
    @GetMapping("/list")
    public List<UserRoles> list(@RequestParam("appCode") String appCode){
        return userRolesService.listByAppCode(appCode);
    }


}

