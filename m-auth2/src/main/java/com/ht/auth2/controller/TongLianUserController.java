package com.ht.auth2.controller;


import com.ht.auth2.entity.UserUsers;
import com.ht.auth2.service.UserUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/tonglian/userUsers")
@CrossOrigin(allowCredentials = "true")
public class TongLianUserController {

    @Autowired
    private UserUsersService userUsersService;

    /**
     * 根据手机号查询用户信息
     * @param phoneNum
     * @param appCode
     * @return
     */
    @GetMapping("/queryByTel")
    public UserUsers queryByTel(@RequestParam("phoneNum") String phoneNum, @RequestParam("appCode") String appCode){
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
     * 校验密码
     * @param userId 用户id
     * @param password 传入的密码
     */
    @PostMapping("/checkPassword/{userId}/{password}")
    public Boolean checkPassword(@PathVariable("userId") Long userId,@PathVariable("password") String password){

        return userUsersService.checkPassword(userId,password);
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
    @GetMapping("/queryByTelList")
    public List<UserUsers> queryByTelList(@RequestParam("phoneNum") String phoneNum, @RequestParam("appCode") String appCode){
        return userUsersService.queryByTelList(phoneNum,appCode);
    }
}
