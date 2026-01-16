package com.ht.feignapi.auth.service;


import com.ht.feignapi.auth.entity.*;
import com.ht.feignapi.result.Result;

public interface AuthService {



    /**
     * m-auth2创建用户
     * @param userUsers
     * @return
     */
    Result<RetServiceData> register(UserUsers userUsers);



}
