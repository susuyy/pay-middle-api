package com.ht.feignapi.auth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.*;
import com.ht.feignapi.auth.service.AuthService;
import com.ht.feignapi.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthClientService authClient;



    /**
     * 用户注册 auth
     * @param userUsers
     * @return
     */
    @Override
    public Result<RetServiceData> register(UserUsers userUsers) {
//        RetServiceData retServiceData = authClient.register(userUsers);
        Result<RetServiceData> register = authClient.register(userUsers);
        return register;
    }

}
