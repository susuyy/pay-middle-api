package com.ht.auth2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.auth2.constant.UserConstant;
import com.ht.auth2.entity.UserMapGroupRole;
import com.ht.auth2.entity.UserMapUserGroup;
import com.ht.auth2.entity.UserRoles;
import com.ht.auth2.result.Result;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.service.UserMapGroupRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/userMapGroupRole")
@CrossOrigin(allowCredentials = "true")
public class UserMapGroupRoleController {

    @Autowired
    private UserMapGroupRoleService userMapGroupRoleService;

    /**
     * 分配角色
     * @param userMapGroupRole
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody UserMapGroupRole userMapGroupRole){

        userMapGroupRoleService.addUserMapGroupRole(userMapGroupRole.getGroupCode(),userMapGroupRole.getRoleCode(), UserConstant.NORMAL);
        return new Result(ResultTypeEnum.SERVICE_SUCCESS.getCode(),ResultTypeEnum.SERVICE_SUCCESS.getMessage());
    }

    /**
     * 根据分组编码查询 角色集
     * @param groupCode
     * @return
     */
    @GetMapping("/queryGroupRoleList")
    public List<UserRoles> queryGroupRoleList(@RequestParam String groupCode){
        return userMapGroupRoleService.queryGroupRoleList(groupCode);
    }

    /**
     * 分组解绑角色
     * @param groupCode
     * @param roleCode
     */
    @DeleteMapping("/deleteMapGroupRole")
    public void deleteMapGroupRole(@RequestParam("groupCode")String groupCode,@RequestParam("roleCode")String roleCode){
        QueryWrapper<UserMapGroupRole> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("group_code",groupCode);
        queryWrapper.eq("role_code",roleCode);
        userMapGroupRoleService.remove(queryWrapper);
    }

}

