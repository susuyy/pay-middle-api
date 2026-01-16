package com.ht.feignapi.auth.service.impl;

import com.ht.feignapi.auth.config.BoWeiAuthServiceConfig;
import com.ht.feignapi.auth.entity.AuthTokenData;
import com.ht.feignapi.auth.entity.LoginData;
import com.ht.feignapi.auth.entity.UserAndTokenData;
import com.ht.feignapi.auth.entity.UserVO;
import com.ht.feignapi.util.JWTUtil;
import com.ht.feignapi.util.MD5Util;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class BoWeiService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 调用第三方接口授权登录
     *
     * @param loginData
     */
    public UserAndTokenData login(LoginData loginData) throws Exception {
        //定义url
        String url = BoWeiAuthServiceConfig.LOGIN_URL;
        //定义头信息
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(BoWeiAuthServiceConfig.Authorization_KEY, BoWeiAuthServiceConfig.Authorization_VALUE);
        headers.add(BoWeiAuthServiceConfig.Tenant_Id_KEY, BoWeiAuthServiceConfig.Tenant_Id_VALUE);
        //定义请求体  有授权模式 用户的名称 和密码
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(BoWeiAuthServiceConfig.tenantId_KEY, BoWeiAuthServiceConfig.tenantId_VALUE);
        formData.add("username", loginData.getUsername());
        String password = loginData.getPassword();
        String passwordMd5 = MD5Util.getMD5(password);
        formData.add("password", passwordMd5);
        //发送POST 请求 携带 头 和请求体 到认证服务器
        HttpEntity<MultiValueMap> requestentity = new HttpEntity<MultiValueMap>(formData, headers);
        //参数1 指定要发送的请求的url
        //参数2 指定要发送的请求的方法 PSOT
        //参数3 指定请求实体(包含头和请求体数据)
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestentity, Map.class);
        Map body = responseEntity.getBody();

        AuthTokenData authTokenData = new AuthTokenData();
        BeanMap beanMap = BeanMap.create(authTokenData);
        beanMap.putAll(body);
        UserVO userVO = queryUserMsg(authTokenData.getAccess_token());
        UserAndTokenData userAndTokenData = new UserAndTokenData();
        userAndTokenData.setAuthTokenData(authTokenData);
        userAndTokenData.setUserVO(userVO);
        return userAndTokenData;
    }


    /**
     * 查询用户信息(调用第三方)
     *
     * @return
     */
    public UserVO queryUserMsg(String jwtToken) {
        Claims claims = JWTUtil.parseJWT(jwtToken);
        //定义url
        String url = "http://47.99.85.203:8084/api/blade-consumer/detail?id=" + claims.get("user_id");
        //定义头信息
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(BoWeiAuthServiceConfig.Authorization_KEY, BoWeiAuthServiceConfig.Authorization_VALUE);
        headers.add(BoWeiAuthServiceConfig.Tenant_Id_KEY, BoWeiAuthServiceConfig.Tenant_Id_VALUE);
        headers.add(BoWeiAuthServiceConfig.Blade_Auth_KEY, BoWeiAuthServiceConfig.Bearer + jwtToken);
        //发送POST 请求 携带 头 和请求体 到认证服务器
        HttpEntity<MultiValueMap> requestentity = new HttpEntity<MultiValueMap>(headers);
        //参数1 指定要发送的请求的url
        //参数2 指定要发送的请求的方法 PSOT
        //参数3 指定请求实体(包含头和请求体数据)
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestentity, Map.class);
        Map body = responseEntity.getBody();
        Map dataBody = (Map) body.get("data");
        //map转化为实体类对象
        UserVO userVO = new UserVO();
        BeanMap beanMap = BeanMap.create(userVO);
        beanMap.putAll(dataBody);
        return userVO;
    }
}
