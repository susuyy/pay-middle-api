package com.ht.auth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ht.auth2.constant.GroupCodeConstant;
import com.ht.auth2.entity.RetServiceData;
import com.ht.auth2.entity.SearchData;
import com.ht.auth2.entity.UserUsers;
import com.ht.auth2.mapper.UserUsersMapper;
import com.ht.auth2.service.UserMapGroupRoleService;
import com.ht.auth2.service.UserMapUserGroupService;
import com.ht.auth2.service.UserUsersService;
import com.ht.auth2.entity.AuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@Service
public class UserUsersServiceImpl extends ServiceImpl<UserUsersMapper, UserUsers> implements UserUsersService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapGroupRoleService userMapGroupRoleService;

    @Autowired
    private UserMapUserGroupService userMapUserGroupService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 用户注册
     *
     * @param userUsers
     * @return
     */
    @Override
    public RetServiceData register(UserUsers userUsers) {
        RetServiceData retServiceData = new RetServiceData();
        if (StringUtils.isEmpty(userUsers.getAppCode())){
            logger.info("注册时AppCode 未定义,不属于任何项目");
            retServiceData.setFlag(false);
            retServiceData.setMessage("注册时AppCode 未定义,不属于任何项目");
            return retServiceData;
        }
        String oriAppCode = userUsers.getAppCode();
//        if ("m-ms-brh".equals(oriAppCode)){
//            userUsers.setAppCode("m-ms");
//        }
        String appCodeGroup = getAppCodeGroup(oriAppCode);
        UserUsers queryUser = queryByAccountNoCode(userUsers.getAccount());
        if (queryUser!=null){
            retServiceData.setFlag(false);
            retServiceData.setMessage("该账号已被注册");
            return retServiceData;
        }
        if (!StringUtils.isEmpty(userUsers.getPassword())) {
            userUsers.setPassword(passwordEncoder.encode(userUsers.getPassword()));
        }
        this.baseMapper.insert(userUsers);
        userMapUserGroupService.addUserGroup(userUsers.getId(),appCodeGroup,"normal");
        retServiceData.setFlag(true);
        retServiceData.setMessage("注册成功");
        retServiceData.setData(userUsers);
        return retServiceData;
    }

    /**
     * 根据appCode 返回 默认分组编码
     * @param appCode
     * @return
     */
    private String getAppCodeGroup(String appCode){
        if ("m-tl".equals(appCode)){
            return GroupCodeConstant.TL_USER_GROUP_CODE;
        }else if ("m-quiz".equals(appCode)){
            return GroupCodeConstant.QUIZ_USER_GROUP_CODE;
        }else if ("m-czj".equals(appCode)){
            return GroupCodeConstant.CZJ_USER_GROUP_CODE;
        }else if ("m-mall".equals(appCode)){
            return GroupCodeConstant.MALL_USER_GROUP_CODE;
        }else if ("m-ms".equals(appCode)){
            return GroupCodeConstant.MS_USER_GROUP_CODE;
        }else if ("m-ms-brh".equals(appCode)){
            return GroupCodeConstant.MS_BRH_USER_GROUP_CODE;
        }else if ("m-outlets".equals(appCode)){
            return GroupCodeConstant.M_OUTLETS_USER_GROUP_CODE;
        }else {
            return GroupCodeConstant.NULL_USER_GROUP_CODE;
        }
    }

    /**
     * 密码模式认证登录 颁发令牌
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @param grandType
     * @return
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret, String grandType) {

        //1.定义url (申请令牌的url)
        //参数 : 微服务的名称spring.application指定的名称
        ServiceInstance choose = loadBalancerClient.choose("m-auth2");
        String url = choose.getUri().toString() + "/oauth/token";

        //2.定义头信息 (有client id 和client secr)
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(new String(clientId + ":" + clientSecret).getBytes()));
        //3. 定义请求体  有授权模式 用户的名称 和密码
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", grandType);
        formData.add("username", username);
        formData.add("password", password);
        //4.模拟浏览器 发送POST 请求 携带 头 和请求体 到认证服务器

        HttpEntity<MultiValueMap> requestentity = new HttpEntity<MultiValueMap>(formData, headers);

//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
//            @Override
//            public void handleError(ClientHttpResponse response) throws IOException {
//                //当响应的值为400或401时候也要正常响应，不要抛出异常
//                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
//                    super.handleError(response);
//                }
//            }
//        });

        //参数1 指定要发送的请求的url
        //参数2 指定要发送的请求的方法 PSOT
        // 参数3 指定请求实体(包含头和请求体数据)
        logger.info("*************SEND INFO*********************:\\" + "url:"+url+"\\requestentity:"+requestentity);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestentity, Map.class);
        logger.info("*************RESPONSE INFO*********************"+responseEntity);
        //5.接收到返回的响应(包装数据响应)
        Map data = responseEntity.getBody();

        Map bodyData =(Map) data.get("data");

        //封装.
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String accessToken = (String) bodyData.get("access_token");
        //token类型
        String tokenType = (String) bodyData.get("token_type");
        //作用范围自定义
        String scope = (String) bodyData.get("scope");
        //增强内容
        String enhance = (String) bodyData.get("enhance");
        //jti
        String jti = (String) bodyData.get("jti");
        //过期时间
        Integer expiresIn = (Integer) bodyData.get("expires_in");

        authToken.setEnhance(enhance);
        authToken.setExpiresIn(expiresIn);
        authToken.setJti(jti);
        authToken.setAccessToken(accessToken);
        authToken.setScope(scope);
        authToken.setTokenType(tokenType);
        //6.返回
        return authToken;
    }


    /**
     * 根据账号查询用户
     *
     * @param account
     * @return
     */
    @Override
    public UserUsers queryByAccountNoCode(String account) {
        return this.baseMapper.selectByAccountNoCode(account);
    }

    /**
     * 根据账号查询用户
     *
     * @param account
     * @param appCode
     * @return
     */
    @Override
    public UserUsers queryByAccount(String account, String appCode) {
        return this.baseMapper.selectByAccount(account,appCode);
    }



    /**
     * 查询用户列表,根据条件筛选 分页
     * @param searchData
     * @return
     */
    @Override
    public IPage<UserUsers> queryUserListSearch(SearchData searchData) {
        QueryWrapper<UserUsers> queryWrapper=new QueryWrapper();
        IPage<UserUsers> iPage = new Page<>(searchData.getPageNo(), searchData.getPageSize());
        if (!StringUtils.isEmpty(searchData.getRealName())){
            queryWrapper.like("real_Name",searchData.getRealName());
        }
        if (!StringUtils.isEmpty(searchData.getNickName())){
            queryWrapper.like("nick_name",searchData.getNickName());
        }
        if (!StringUtils.isEmpty(searchData.getAccount())){
            queryWrapper.like("account",searchData.getAccount());
        }
        if (!StringUtils.isEmpty(searchData.getAppCode())){
            queryWrapper.eq("app_code",searchData.getAppCode());
        }
        queryWrapper.orderByDesc("create_at");
        queryWrapper.isNotNull("account");
        queryWrapper.isNotNull("password");
        return this.baseMapper.selectPage(iPage,queryWrapper);
    }

    /**
     *  根据openid 查询微信端用户数据
     * @param openid
     * @return
     */
    @Override
    public UserUsers queryByOpenid(String openid) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("open_id",openid);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public UserUsers queryByTel(String phoneNum, String appCode) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("tel",phoneNum);
        queryWrapper.eq("app_code",appCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void updatePasswordByTel(String password, String tel, String appCode) {
        UserUsers userUsers = queryByTel(tel, appCode);
        userUsers.setPassword(passwordEncoder.encode(password));
        this.baseMapper.updateById(userUsers);
    }

    @Override
    public void updatePasswordByOpenid(String password, String openid) {
        UserUsers userUsers = queryByOpenid(openid);
        userUsers.setPassword(passwordEncoder.encode(password));
        this.baseMapper.updateById(userUsers);
    }

    @Override
    public void updateOpenidById(Long userId, String openid) {
        UserUsers userUsers = getById(userId);
        userUsers.setOpenId(openid);
        updateById(userUsers);
    }

    @Override
    public void updatePasswordByUserId(UserUsers userUsers) {
        userUsers.setPassword(passwordEncoder.encode(userUsers.getPassword()));
        this.updateById(userUsers);
    }

    @Override
    public Boolean checkPassword(Long userId, String password) {
        UserUsers user = this.getById(userId);
        return passwordEncoder.matches(password,user.getPassword());
    }

    @Override
    public List<UserUsers> queryByTelList(String phoneNum, String appCode) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("tel",phoneNum);
        queryWrapper.eq("app_code",appCode);
        return this.baseMapper.selectList(queryWrapper);
    }


}
