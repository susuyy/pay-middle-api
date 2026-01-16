package com.ht.auth2.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.auth2.constant.UserConstant;
import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserMenu;
import com.ht.auth2.entity.UserMenuTree;
import com.ht.auth2.result.Result;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.service.UserMenuService;
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
@RequestMapping("/userMenu")
@CrossOrigin(allowCredentials = "true")
public class UserMenuController {

    @Autowired
    private UserMenuService userMenuService;

    /**
     * 增加菜单
     * @param userMenu
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody UserMenu userMenu){
        userMenuService.addUserMenu(userMenu.getMenuName(),userMenu.getLevel(),userMenu.getParentMenuCode(), UserConstant.NORMAL,userMenu.getAppCode(),userMenu.getAppName());
        return new Result(ResultTypeEnum.SERVICE_SUCCESS.getCode(),ResultTypeEnum.SERVICE_SUCCESS.getMessage());
    }

    /**
     * 删除菜单
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable("id")String id){
        userMenuService.removeById(Long.parseLong(id));
        return new Result(ResultTypeEnum.SERVICE_SUCCESS.getCode(),ResultTypeEnum.SERVICE_SUCCESS.getMessage());
    }

    /**
     * 获取用户展示的菜单列表
     * @param appCode
     * @param userId
     * @param account
     * @return
     */
    @GetMapping("/menuTreeList")
    public List<UserMenuTree> queryMenuTreeListByAppCode(@RequestParam("appCode") String appCode,@RequestParam("userId") String userId ,@RequestParam("account") String account){
        return userMenuService.queryMenuTreeListByAppCode(appCode,userId,account);
    }

    /**
     * 根据条件 筛选菜单列表 分页
     * @param searchData
     * @return
     */
    @PostMapping("/getMenuList")
    public IPage getMenuListBySearchData(@RequestBody SearchData searchData){
        return userMenuService.queryMenuListBySearchData(searchData);
    }

    /**
     * 修改 菜单数据
     * @param userMenu
     */
    @PostMapping("/updateMenu")
    public void updateMenu(@RequestBody UserMenu userMenu){
        userMenuService.updateById(userMenu);
    }

    /**
     * 根据id 查询菜单信息
     * @param id
     * @return
     */
    @GetMapping("/getMenuById")
    public UserMenu getMenuById(@RequestParam("id") String id){
        return userMenuService.getById(Long.parseLong(id));
    }

    /**
     * 根据appCode 查询 菜单集
     * @param appCode
     * @return
     */
    @GetMapping("/list")
    public List<UserMenuTree> list(@RequestParam("appCode") String appCode){
        return userMenuService.listByAppCodeTree(appCode);
    }
}

