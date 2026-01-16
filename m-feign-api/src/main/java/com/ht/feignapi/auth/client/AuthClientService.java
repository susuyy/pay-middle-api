package com.ht.feignapi.auth.client;


import com.ht.feignapi.auth.entity.*;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@FeignClient("${custom.client.auth.name}")
public interface AuthClientService {

    /**
     * auth2 用户信息
     * @param user
     * @return
     */
    @GetMapping("/userUsers/user")
    Result user(Principal user);

    /**
     * auth2 服务 登录
     */
    @PostMapping("/userUsers/login")
    Result<UserUsersVO> login(LoginData loginData);

    /**
     * 微信用户登录 openid登录
     * @param loginData
     * @return
     */
    @PostMapping("/userUsers/loginOpenid")
    Result loginOpenid(@RequestBody LoginData loginData);

    /**
     * AUTH2 服务用户注册
     * @param userUsers
     */
    @PostMapping("/userUsers/register")
    Result<RetServiceData> register(@RequestBody UserUsers userUsers);

    @GetMapping("/{applicationCode}/userUsers/{phone}")
    UserUsers getUser(@PathVariable("applicationCode") String applicationCode,@PathVariable("phone") String phone);

    /**
     * 测试通信
     */
    @GetMapping("/userUsers/test")
    void test();

    /**
     * 获取分组树形结构
     * @return
     * @param appCode
     */
    @GetMapping("/userGroups/groupList/{appCode}")
    Result groupList(@PathVariable("appCode") String appCode);

    /**
     * 根据 账号查询 用户信息
     * @param account
     * @param appCode
     * @return
     */
    @GetMapping("/userUsers/getUserByAccount/{account}")
    Result<UserUsers> queryUserByAccount(@PathVariable("account") String account, @RequestParam("appCode") String appCode);

    /**
     * 添加分组
     * @param userGroups
     */
    @PostMapping("/userGroups/add")
    void addUserGroups(@RequestBody UserGroups userGroups);

    /**
     * 添加分组 与 角色关联关系
     * @param userMapGroupRole
     */
    @PostMapping("/userMapGroupRole/add")
    void addUserMapGroupRole(@RequestBody UserMapGroupRole userMapGroupRole);

    /**
     * 添加角色与菜单关联关系
     * @param userMapRoleMenu
     */
    @PostMapping("/userMapRoleMenu/add")
    void createUserMapRoleMenu(@RequestBody UserMapRoleMenu userMapRoleMenu);

    /**
     * 添加用户与分组关联关系
     * @param userMapUserGroup
     */
    @PostMapping("/userMapUserGroup/add")
    void addUserGroup(@RequestBody UserMapUserGroup userMapUserGroup);

    /**
     * 增加菜单
     * @param userMenu
     */
    @PostMapping("/userMenu/add")
    void addUserMenu(@RequestBody UserMenu userMenu);

    /**
     * 删除菜单
     * @param id
     */
    @DeleteMapping("/userMenu/delete/{id}")
    void deleteMenuById(@PathVariable("id") String id);

    /**
     * 新增角色
     * @param userRoles
     */
    @PostMapping("/userRoles/add")
    void addUserRoles(@RequestBody UserRoles userRoles);

    /**
     * 根据id 删除角色
     * @param id
     */
    @DeleteMapping("/userRoles/delete/{id}")
    void deleteRoleById(@PathVariable("id") String id);

    /**
     * 查询所有角色列表
     * @return
     */
    @PostMapping("/userRoles/roleList")
    Result getRoleList(@RequestBody SearchData searchData);

    /**
     * 查询菜单树形结构
     * @param appCode
     * @param userId
     * @param account
     * @return
     */
    @GetMapping("/userMenu/menuTreeList")
    Result<List<UserMenuTree>> queryMenuTreeListByAppCode(@RequestParam("appCode") String appCode,@RequestParam("userId") String userId ,@RequestParam("account") String account);

    /**
     * 根据条件 分页筛选分组列表
     * @param searchData
     * @return
     */
    @PostMapping("/userGroups/getGroupList")
    Result getGroupList(@RequestBody SearchData searchData);

    /**
     * 根据查询条件 分页筛选 菜单列表
     * @param searchData
     * @return
     */
    @PostMapping("/userMenu/getMenuList")
    Result getMenuListBySearchData(@RequestBody SearchData searchData);

    /**
     * 根据查询条件获取用户列表 筛选 分页
     * @param searchData
     * @return
     */
    @PostMapping("/userUsers/userListSearch")
    Result getUserListSearchData(@RequestBody SearchData searchData);

    /**
     * 修改用户数据
     * @param userUsers
     */
    @PostMapping("/userUsers/updateUser")
    void updateUser(@RequestBody UserUsers userUsers);

    /**
     * 根据id 查询用户信息
     * @param id
     * @return
     */
    @GetMapping("/userUsers/getUserById")
    Result<UserUsers> getUserById(@RequestParam("id") String id);

    /**
     * 修改分组数据
     * @param userGroups
     */
    @PostMapping("/userGroups/updateUserGroups")
    void updateUserGroups(@RequestBody UserGroups userGroups);

    /**
     * 根据id 查询分组信息
     * @param id
     * @return
     */
    @GetMapping("/userGroups/getUserGroupsById")
    Result getUserGroupsById(@RequestParam("id") String id);

    /**
     * 更新 角色 信息
     * @param userRoles
     */
    @PostMapping("/userRoles/updateUserRole")
    void updateUserRole(@RequestBody UserRoles userRoles);

    /**
     * 根据id查询角色信息
     * @param id
     * @return
     */
    @GetMapping("/userRoles/getUserRoleById")
    Result getUserRoleById(@RequestParam("id") String id);

    /**
     * 修改 菜单数据
     * @param userMenu
     */
    @PostMapping("/userMenu/updateMenu")
    void updateMenu(@RequestBody UserMenu userMenu);

    /**
     * 根据id 查询菜单信息
     * @param id
     * @return
     */
    @GetMapping("/userMenu/getMenuById")
    Result getMenuById(@RequestParam("id") String id);

    /**
     * 分配 角色和菜单集 对应
     * @param addRoleMenuList
     * @return
     */
    @PostMapping("/userMapRoleMenu/addRoleMenuList")
    void addRoleMenuList(AddRoleMenuList addRoleMenuList);

    /**
     * 根据用户id 修改密码
     * @param userUsers
     */
    @PostMapping("/userUsers/updatePasswordByUserId")
    void updatePasswordByUserId(@RequestBody UserUsers userUsers);

    //以下通联接口

    /**
     * 根据用户id 修改密码 ()
     * @param userUsers
     */
    @PostMapping("/tonglian/userUsers/updatePasswordByUserId")
    void updatePasswordByUserIdTL(@RequestBody UserUsers userUsers);

    /**
     * 根据用户id更新openid
     * @param userId
     * @param openid
     */
    @PostMapping("/tonglian/userUsers/updateOpenidById")
    void updateOpenidById(@RequestParam("userId") Long userId,@RequestParam("openid") String openid);

    /**
     * 更新或保存用户 数据
     * @param userUsers
     */
    @PostMapping("/tonglian/userUsers/saveOrUpdateUser")
    Result<Long> saveOrUpdateUser(@RequestBody UserUsers userUsers);

    /**
     * 根据openid修改用户密码
     * @param password
     * @param openid
     */
    @PostMapping("/tonglian/userUsers/updatePasswordByOpenid")
    void updatePasswordByOpenid(@RequestParam("password") String password,@RequestParam("openid") String openid);

    /**
     * 根据手机号修改密码
     * @param password
     * @param tel
     * @param appCode
     */
    @PostMapping("/tonglian/userUsers/updatePassword")
    void updatePasswordByTel(@RequestParam("password") String password,@RequestParam("tel") String tel,@RequestParam("appCode") String appCode);

    /**
     * 根据openid查询用户信息
     * @param openid
     * @return
     */
    @GetMapping("/tonglian/userUsers/queryByOpenid")
    Result<UserUsers> queryByOpenid(@RequestParam("openid") String openid);

    /**
     * 根据手机号查询用户信息
     * @param phoneNum
     * @param appCode
     * @return
     */
    @GetMapping("/tonglian/userUsers/queryByTel")
    Result<UserUsers> queryByTel(@RequestParam("phoneNum") String phoneNum,@RequestParam("appCode") String appCode);

    /**
     * 根据手机号查询用户信息
     * @param phoneNum
     * @param appCode
     * @return
     */
    @GetMapping("/tonglian/userUsers/queryByTelList")
    Result<List<UserUsers>> queryByTelList(@RequestParam("phoneNum") String phoneNum,@RequestParam("appCode") String appCode);

    /**
     * 根据id 查询用户信息
     * @param id
     * @return
     */
    @GetMapping("/tonglian/userUsers/getUserById")
    Result<UserUsers> getUserByIdTL(@RequestParam("id") String id);

    /**
     * 修改用户数据通联用
     * @param userUsers
     */
    @PostMapping("/tonglian/userUsers/updateUser")
    void updateUserTL(@RequestBody UserUsers userUsers);

    /**
     * 校验密码
     * @param userId 用户id
     * @param password 传入的密码
     */
    @PostMapping("/tonglian/userUsers/checkPassword/{userId}/{password}")
    Result<Boolean> checkPassword(@PathVariable("userId") Long userId,@PathVariable("password") String password);

}
