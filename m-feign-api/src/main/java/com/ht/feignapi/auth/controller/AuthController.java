package com.ht.feignapi.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.entity.*;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.service.AuthService;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping(value = "/auth/userUsers",produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthClientService authClient;

    @Autowired
    private AuthService authService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    /**
     * 测试
     * @return
     */
    @GetMapping("/test")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public UserUsers test() {
        UserUsers userUsers = new UserUsers();
        userUsers.setNickName("test");
        return userUsers;
    }

    //以下用户相关接口

    /**
     * 用户登录 (auth密码模式登录)
     * @param loginData
     * @return
     * @throws Exception
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginData loginData) throws Exception {
        Result result = authClient.login(loginData);
        return result;
    }

    /**
     * 用户登录 微信用户登录授权 openid登录
     * @param loginData
     * @return
     * @throws Exception
     */
    @PostMapping("/loginOpenid")
    public Result loginOpenid(@RequestBody LoginData loginData)throws Exception {
        Result result = authClient.loginOpenid(loginData);
        return result;
    }

    /**
     * 用户注册 创建用户
     * @param userUsers
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserUsers userUsers) {
        Result<RetServiceData> register = authService.register(userUsers);

        //根据appCode
        if (AppConstant.MS_APP_CODE.equals(userUsers.getAppCode())){
            MrcMapMerchantUser mrcMapMerchantUser = new MrcMapMerchantUser();
            mrcMapMerchantUser.setMerchantCode("HLMSD");
            mrcMapMerchantUser.setUserId(register.getData().getData().getId());
            mrcMapMerchantUser.setState("enable");
            mrcMapMerchantUser.setType("1");
            mrcMapMerchantUser.setCreateAt(new Date());
            mrcMapMerchantUser.setUpdateAt(new Date());
            merchantsClientService.saveMerchantMapUser(mrcMapMerchantUser);
        }

        return register;
    }

    /**
     * 查询用户列表 分页
     * @param searchData
     * @return
     */
    @PostMapping("/getAllUserList")
    public RetPageData getUserListSearchData(@RequestBody SearchData searchData){
        Result result = authClient.getUserListSearchData(searchData);
        String string = JSONObject.toJSONString(result.getData());
        RetPageData retPageData = JSONObject.parseObject(string, RetPageData.class);
        return retPageData;
    }

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @GetMapping("/getUserById")
    public UserUsers getUserById(@RequestParam(value = "id") String id){
        Result result = authClient.getUserById(id);
        String string = JSONObject.toJSONString(result.getData());
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
        return userUsers;
    }

    /**
     * 根据用户id 修改密码
     * @param userUsers
     */
    @PostMapping("/updatePasswordByUserId")
    public void updatePasswordById(@RequestBody UserUsers userUsers){
        authClient.updatePasswordByUserId(userUsers);
    }


    //以下分组接口

    /**
     * 获取分组 树形结构
     * @param appCode
     * @return
     */
    @GetMapping("/groupTreeList/{appCode}")
    public Result groupTreeList(@PathVariable("appCode")String appCode) {
        Result result = authClient.groupList(appCode);
        return result;
    }


    /**
     * 添加组织架构 分组
     * @param userGroups
     * @return
     */
    @PostMapping("/addGroup")
    public void add(@RequestBody UserGroups userGroups){
        authClient.addUserGroups(userGroups);
    }

    /**
     * 获取分组列表 根据条件分页筛选
     * @param searchData
     * @return
     */
    @PostMapping("/getGroupList")
    public RetPageData getGroupList(@RequestBody SearchData searchData){
        Result result = authClient.getGroupList(searchData);
        String string = JSONObject.toJSONString(result.getData());
        RetPageData retPageData = JSONObject.parseObject(string, RetPageData.class);
        return retPageData;
    }

    /**
     * 修改分组
     * @param userGroups
     */
    @PostMapping("/updateGroup")
    public void updateGroup(@RequestBody UserGroups userGroups){
        authClient.updateUserGroups(userGroups);
    }

    /**
     * 根据id 查询分组信息
     * @param id
     * @return
     */
    @GetMapping("/getUserGroupsById")
    public UserGroups getUserGroupsById(String id){
        Result result = authClient.getUserGroupsById(id);
        String string = JSONObject.toJSONString(result.getData());
        UserGroups userGroups = JSONObject.parseObject(string, UserGroups.class);
        return userGroups;
    }

    //以下角色接口

    /**
     * 添加角色
     * @param userRoles
     * @return
     */
    @PostMapping("/addRole")
    public void add(@RequestBody UserRoles userRoles){
        authClient.addUserRoles(userRoles);
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @DeleteMapping("/deleteRole/{id}")
    public void deleteRoleById(@PathVariable("id")String id){
        authClient.deleteRoleById(id);
    }

    /**
     * 查询所有角色列表
     * @param searchData
     * @return
     */
    @PostMapping("/roleList")
    public RetPageData getRoleList(@RequestBody SearchData searchData){
        Result result = authClient.getRoleList(searchData);
        String string = JSONObject.toJSONString(result.getData());
        RetPageData retPageData = JSONObject.parseObject(string, RetPageData.class);
        return retPageData;
    }

    /**
     * 修改角色
     * @param userRoles
     */
    @PostMapping("/updateRole")
    public void updateRole(@RequestBody UserRoles userRoles){
        authClient.updateUserRole(userRoles);
    }

    /**
     * 根据id 查询角色信息
     * @param id
     * @return
     */
    @GetMapping("/getUserRoleById")
    public UserRoles getUserRoleById(String id){
        Result result = authClient.getUserRoleById(id);
        String string = JSONObject.toJSONString(result.getData());
        return JSONObject.parseObject(string,UserRoles.class);
    }

    //以下菜单接口

    /**
     * 根据appCode 和用户id 和 用户 account 查询菜单树
     * @param appCode
     * @param userId
     * @param account
     * @return
     */
    @GetMapping("/menuList")
    public List<UserMenuTree> getMenuTreeListByAppCode(@RequestParam("appCode") String appCode,
                                                       @RequestParam("userId") String userId,
                                                       @RequestParam("account") String account){
        List<UserMenuTree> menuTreeList = authClient.queryMenuTreeListByAppCode(appCode, userId, account).getData();
//        String string = JSONObject.toJSONString(result.getData());
//        return JSONObject.parseObject(string,List.class);
        return menuTreeList;
    }

    /**
     * 增加菜单
     * @param userMenu
     * @return
     */
    @PostMapping("/addMenu")
    public void add(@RequestBody UserMenu userMenu){
        authClient.addUserMenu(userMenu);
    }

    /**
     * 删除菜单
     * @param id
     * @return
     */
    @DeleteMapping("/deleteMenu/{id}")
    public void deleteMenuById(@PathVariable("id")String id){
        authClient.deleteMenuById(id);
    }

    /**
     * 根据查询条件 筛选菜单列表 分页
     * @param searchData
     * @return
     */
    @PostMapping("/getMenuList")
    public RetPageData getMenuList(@RequestBody SearchData searchData){
        Result result = authClient.getMenuListBySearchData(searchData);
        String string = JSONObject.toJSONString(result.getData());
        return JSONObject.parseObject(string,RetPageData.class);
    }

    /**
     * 修改菜单数据
     * @param userMenu
     */
    @PostMapping("/updateMenu")
    public void updateMenu(@RequestBody UserMenu userMenu){
        authClient.updateMenu(userMenu);
    }

    /**
     * 根据id 查询 菜单信息
     * @param id
     * @return
     */
    @GetMapping("/getMenuById")
    public UserMenu getMenuById(String id){
        Result result = authClient.getMenuById(id);
        String string = JSONObject.toJSONString(result.getData());
        return JSONObject.parseObject(string,UserMenu.class);
    }


    //以下关联关系接口

    /**
     * 分配 分组 角色 对应关联关系
     * @param userMapGroupRole
     * @return
     */
    @PostMapping("/addGroupRole")
    public void add(@RequestBody UserMapGroupRole userMapGroupRole){
        authClient.addUserMapGroupRole(userMapGroupRole);
    }

    /**
     * 分配 角色和菜单 对应
     * @param userMapRoleMenu
     * @return
     */
    @PostMapping("/addRoleMenu")
    public void add(@RequestBody UserMapRoleMenu userMapRoleMenu){
        authClient.createUserMapRoleMenu(userMapRoleMenu);
    }

    /**
     * 分配 角色和菜单集 对应
     * @param addRoleMenuList
     * @return
     */
    @PostMapping("/addRoleMenuList")
    public void addRoleMenuList(@RequestBody AddRoleMenuList addRoleMenuList){
        authClient.addRoleMenuList(addRoleMenuList);
    }

    /**
     * 分配用户与 分组 关系
     * @param userMapUserGroup
     * @return
     */
    @PostMapping("/addUserGroup")
    public void mapUser(@RequestBody UserMapUserGroup userMapUserGroup){
        authClient.addUserGroup(userMapUserGroup);
    }


}
