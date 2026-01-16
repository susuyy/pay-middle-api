package com.ht.auth2.controller;


import com.ht.auth2.constant.UserConstant;
import com.ht.auth2.entity.AddRoleMenuList;
import com.ht.auth2.entity.UserMapRoleMenu;
import com.ht.auth2.entity.UserMenu;
import com.ht.auth2.entity.UserMenuTree;
import com.ht.auth2.result.Result;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.service.UserMapRoleMenuService;
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
@RequestMapping("/userMapRoleMenu")
@CrossOrigin(allowCredentials = "true")
public class UserMapRoleMenuController {

    @Autowired
    private UserMapRoleMenuService userMapRoleMenuService;

    /**
     * 分配角色和菜单对应
     * @param userMapRoleMenu
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody UserMapRoleMenu userMapRoleMenu){
        userMapRoleMenuService.createUserMapRoleMenu(userMapRoleMenu.getRoleCode(),userMapRoleMenu.getMenuCode(), UserConstant.NORMAL);
        return new Result(ResultTypeEnum.SERVICE_SUCCESS.getCode(),ResultTypeEnum.SERVICE_SUCCESS.getMessage());
    }

    /**
     * 根据角色编码 查询对应的菜单集
     * @param roleCode
     * @return
     */
    @GetMapping("/queryRoleMenuList")
    public List<UserMenu> queryRoleMenuList(@RequestParam String roleCode){
        return userMapRoleMenuService.queryRoleMenuList(roleCode);
    }

    /**
     * 分配 角色和菜单集 对应
     * @param addRoleMenuList
     * @return
     */
    @PostMapping("/addRoleMenuList")
    public void addRoleMenuList(@RequestBody AddRoleMenuList addRoleMenuList){
        userMapRoleMenuService.addRoleMenuList(addRoleMenuList.getRoleCode(),addRoleMenuList.getMenuCodeList());
    }
}

