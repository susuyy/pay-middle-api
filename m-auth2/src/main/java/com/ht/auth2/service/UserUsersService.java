package com.ht.auth2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.auth2.entity.RetServiceData;
import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserUsers;
import com.ht.auth2.entity.AuthToken;

import java.util.List;


/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
public interface UserUsersService extends IService<UserUsers> {

    /**
     * 用户注册
     * @param userUsers
     * @return
     */
    RetServiceData register(UserUsers userUsers);


    AuthToken login(String username, String password, String clientId, String clientSecret, String grandType);




    /**
     * 根据账号查询用户
     * @param account
     * @return
     */
    UserUsers queryByAccountNoCode(String account);

    /**
     * 根据账号查询用户
     * @param account
     * @param appCode
     * @return
     */
    UserUsers queryByAccount(String account, String appCode);



    /**
     * 查询用户列表 根据条件 筛选 分页
     * @param searchData
     * @return
     */
    IPage<UserUsers> queryUserListSearch(SearchData searchData);

    /**
     * 根据openid查询用户数据
     * @param openid
     * @return
     */
    UserUsers queryByOpenid(String openid);

    /**
     * 根据手机号查询用户信息
     * @param phoneNum
     * @param appCode
     */
    UserUsers queryByTel(String phoneNum, String appCode);

    /**
     * 根据手机号 修改用户密码
     * @param password
     * @param tel
     * @param appCode
     */
    void updatePasswordByTel(String password, String tel, String appCode);

    /**
     * 根据openid修改用户密码
     * @param password
     * @param openid
     */
    void updatePasswordByOpenid(String password, String openid);

    /**
     * 根据用户id 更新openid
     * @param userId
     * @param openid
     */
    void updateOpenidById(Long userId, String openid);

    /**
     * 根据用户id 修改密码
     * @param userUsers
     */
    void updatePasswordByUserId(UserUsers userUsers);

    /**
     * 校验密码
     * @param userId
     * @param password
     * @return
     */
    Boolean checkPassword(Long userId, String password);

    List<UserUsers> queryByTelList(String phoneNum, String appCode);
}
