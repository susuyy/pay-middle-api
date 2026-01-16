package com.ht.auth2.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.auth2.config.AuthClientDataConfig;
import com.ht.auth2.entity.*;
import com.ht.auth2.result.Result;
import com.ht.auth2.result.ResultTypeEnum;
import com.ht.auth2.result.UserDefinedException;

import com.ht.auth2.service.UserGroupsService;
import com.ht.auth2.service.UserRolesService;
import com.ht.auth2.service.UserUsersService;
import com.ht.auth2.vo.UserUsersVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@RestController
@RequestMapping("/userUsers")
@CrossOrigin(allowCredentials = "true")
public class UserUsersController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private UserRolesService userRolesService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserGroupsService userGroupsService;


    /**
     * 测试通信接口
     */
    @GetMapping("/test")
    public void test(){
        System.out.println("测试通信");
    }

    /**
     * 获取登录用户信息
     * oauth2 service
     * @param user
     * @return
     */
    @GetMapping("/user")
    public Result user(Principal user) {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) user;
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        String name = userAuthentication.getName();
        return Result.success(name);
    }


    /**
     * 密码模式  认证.
     * @param loginData
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginData loginData) {
        UserUsersVO userUsersVO = null;
        try {
            //登录 之后生成令牌的数据返回
            AuthToken authToken = userUsersService.login(loginData.getUsername(),loginData.getPassword(), AuthClientDataConfig.CLIENT_ID,AuthClientDataConfig.CLIENT_SECRET,AuthClientDataConfig.GRAND_TYPE_PASSWORD);
            UserUsers users = userUsersService.queryByAccountNoCode(loginData.getUsername());
            //封装响应前端的 user数据
            users.setToken(JSONObject.toJSONString(authToken));
            userUsersService.updateById(users);
            users.setToken(authToken.getAccessToken());
            userUsersVO = new UserUsersVO();
            BeanUtils.copyProperties(users,userUsersVO);
            List<UserRoles> userRolesList=userRolesService.queryRoleListByAccount(users.getAccount());
            userUsersVO.setUserRolesList(userRolesList);
            List<UserGroups> userGroupsList = userGroupsService.queryUserGroupsList(users.getId());
            userUsersVO.setUserGroupsList(userGroupsList);
        } catch (Exception e) {
            logger.info("*********************"+e.toString());
            return new Result(ResultTypeEnum.LOGIN_ERROR,ResultTypeEnum.LOGIN_ERROR.getMessage(),e.getMessage());
        }
        return new Result(ResultTypeEnum.SERVICE_SUCCESS,ResultTypeEnum.SERVICE_SUCCESS.getMessage(),userUsersVO);
    }

    /**
     * 微信用户登录 默认授权 普通用户权限 user
     * @param loginData
     * @return
     */
    @PostMapping("/loginOpenid")
    public Result loginOpenid(@RequestBody LoginData loginData) {
        UserUsersVO userUsersVO = null;
        try {
            //登录 之后生成令牌的数据返回
            AuthToken authToken = userUsersService.login("user","user", AuthClientDataConfig.CLIENT_ID,AuthClientDataConfig.CLIENT_SECRET,AuthClientDataConfig.GRAND_TYPE_PASSWORD);
            UserUsers users = userUsersService.queryByOpenid(loginData.getOpenid());
            //封装响应前端的 user数据
            users.setToken(JSONObject.toJSONString(authToken));
            userUsersService.updateById(users);
            users.setToken(authToken.getAccessToken());
            userUsersVO = new UserUsersVO();
            BeanUtils.copyProperties(users,userUsersVO);
        } catch (Exception e) {
            System.out.println(e);
            return new Result(ResultTypeEnum.LOGIN_ERROR,ResultTypeEnum.LOGIN_ERROR.getMessage(),e.getMessage());
        }
        return new Result(ResultTypeEnum.SERVICE_SUCCESS,ResultTypeEnum.SERVICE_SUCCESS.getMessage(),userUsersVO);
    }


    /**
     * 用户注册
     * @param userUsers
     */
    @PostMapping("/register")
    public RetServiceData register(@RequestBody UserUsers userUsers){
        RetServiceData retServiceData = userUsersService.register(userUsers);
        return retServiceData;
    }

    /**
     *  测试用户列表接口
     * @return
     */
    @GetMapping("/list")
    public List list(){
        return userUsersService.list();
    }

    /**
     * 根据账号查询用户
     * @param account
     * @param appCode
     * @return
     */
    @GetMapping("/getUserByAccount/{account}/{appCode}")
    public UserUsers getUserByAccount(@PathVariable("account")String account,@PathVariable("appCode")String appCode){
        UserUsers userUsers = userUsersService.queryByAccount(account,appCode);
        return userUsers;
    }

    /**
     * 分页查询 搜索用户 列表
     * @param searchData
     * @return
     */
    @PostMapping("/userListSearch")
    public IPage<UserUsers> getUserListSearch(@RequestBody SearchData searchData){
        return userUsersService.queryUserListSearch(searchData);
    }

    /**
     * 修改用户数据
     * @param userUsers
     */
    @PostMapping("/updateUser")
    public void updateUser(@RequestBody UserUsers userUsers){
        userUsersService.updateById(userUsers);
    }

    /**
     * 根据id 查询用户
     * @param id
     * @return
     */
    @GetMapping("/getUserById")
    public UserUsers getUserById(@RequestParam String id){
        return userUsersService.getById(Long.parseLong(id));
    }

    /**
     * 根据手机号查询用户信息
     * @param phoneNum
     * @param appCode
     * @return
     */
    @GetMapping("/queryByTel")
    public UserUsers queryByTel(@RequestParam("phoneNum") String phoneNum,@RequestParam("appCode") String appCode){
        return userUsersService.queryByTel(phoneNum,appCode);
    }

    /**
     * 根据手机号修改密码
     * @param password
     * @param tel
     * @param appCode
     */
    @PostMapping("/updatePassword")
    public void updatePasswordByTel(@RequestParam("password") String password,@RequestParam("tel") String tel,@RequestParam("appCode") String appCode){
        userUsersService.updatePasswordByTel(password,tel,appCode);
    }

    /**
     * 根据openid查询用户信息
     * @param openid
     * @return
     */
    @GetMapping("/queryByOpenid")
    public UserUsers queryByOpenid(@RequestParam("openid") String openid){
        UserUsers userUsers = userUsersService.queryByOpenid(openid);
        return userUsers;
    }

    /**
     * 根据openid修改用户密码
     * @param password
     * @param openid
     */
    @PostMapping("/updatePasswordByOpenid")
    public void updatePasswordByOpenid(@RequestParam("password") String password,@RequestParam("openid") String openid){
        userUsersService.updatePasswordByOpenid(password,openid);
    }

    /**
     * 更新或保存用户 数据
     * @param userUsers
     */
    @PostMapping("/saveOrUpdateUser")
    public Long saveOrUpdateUser(@RequestBody UserUsers userUsers){
        userUsersService.saveOrUpdate(userUsers);
        System.out.println(userUsers);
        return userUsers.getId();
    }

    /**
     * 根据用户id更新openid
     * @param userId
     * @param openid
     */
    @PostMapping("/updateOpenidById")
    public void updateOpenidById(@RequestParam("userId") Long userId,@RequestParam("openid") String openid){
        userUsersService.updateOpenidById(userId,openid);
    }

    /**
     * 根据用户id 修改密码
     * @param userUsers
     */
    @PostMapping("/updatePasswordByUserId")
    public void updatePasswordByUserId(@RequestBody UserUsers userUsers){
        userUsersService.updatePasswordByUserId(userUsers);
    }

    /**
     * 根据用户id 修改密码,校验原密码
     * @param passwordCheckOrgData
     */
    @PostMapping("/updatePasswordCheckOrg")
    public void updatePasswordCheckOrg(@RequestBody PasswordCheckOrgData passwordCheckOrgData){
        Boolean checkPasswordFlag = userUsersService.checkPassword(Long.parseLong(passwordCheckOrgData.getUserId()), passwordCheckOrgData.getOrgPassword());
        if (checkPasswordFlag){
            UserUsers userUsers = new UserUsers();
            userUsers.setId(Long.parseLong(passwordCheckOrgData.getUserId()));
            userUsers.setPassword(passwordCheckOrgData.getNewPassword());
            userUsersService.updatePasswordByUserId(userUsers);
        }else {
            throw new UserDefinedException(ResultTypeEnum.ORG_PASSWORD_ERROR);
        }
    }
}

