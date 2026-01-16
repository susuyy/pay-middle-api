package com.ht.feignapi.posapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.entity.LoginData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.posapi.util.LoginHttpRequestUtil;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.admin.entity.LoginVo;
import com.ht.feignapi.tonglian.merchant.constant.MerchantConstant;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pos/login")
public class PosLoginController {

    @Autowired
    private LoginHttpRequestUtil loginHttpRequestUtil;

    /**
     * pos选择系统登录
     *
     * @param loginVo 登录参数
     * @return 登录结果
     */
    @PostMapping("/loginSystem")
    public Object loginSystem(@RequestBody LoginVo loginVo) {
        Assert.notNull(loginVo.getMerchantCode(),"商户号不能为空");
        Assert.notNull(loginVo.getSystemCode(),"系统标识不能为空");

        Object data = loginHttpRequestUtil.requestLoginHttp(loginVo);
        System.out.println(data);
        return data;
    }

}
