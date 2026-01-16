package com.ht.feignapi.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.LoginData;
import com.ht.feignapi.auth.entity.LoginVo;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/17 10:19
 */
@RestController
@RequestMapping("/auth/adminLogin")
public class AuthLoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuthClientService authClientService;



    /**
     * 后台控制器登录
     *
     * @param loginVo 登录参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map login(@RequestBody LoginVo loginVo) {
        LoginData loginData = new LoginData();
        loginData.setUsername(loginVo.getUserName());
        loginData.setPassword(loginVo.getPassword());
        Result result = authClientService.login(loginData);
        logger.info("***************************result***************************" + result);
        if (!result.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode())){
            throw new CheckException(ResultTypeEnum.LOGIN_ERROR);
        }
        Object data = result.getData();
        String string = JSONObject.toJSONString(data);
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("token",userUsers.getToken());
        hashMap.put("merchantCode",loginVo.getMerchantCode());
        hashMap.put("adminId",userUsers.getId());
        hashMap.put("account",userUsers.getAccount());
        return hashMap;
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        return "成功登出";
    }
}
