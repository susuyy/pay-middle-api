package com.ht.feignapi.shoppingmall.controller;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.LoginData;
import com.ht.feignapi.auth.entity.UserUsersVO;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.shoppingmall.service.ShoppingMallUserService;

import com.ht.feignapi.shoppingmall.vo.MerchantUserVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shopping-mall/user")
@CrossOrigin(allowCredentials = "true")
public class ShoppingMallController {


    private Logger log = LoggerFactory.getLogger(ShoppingMallController.class);

    @Autowired
    private ShoppingMallUserService shoppingMallUserService;

    @Autowired
    private AuthClientService authClient;


    /**
     *
     * 扎堆商城 微信小程序授权登录
     * @param postParams
     * @return
     */
    @PostMapping("/app/userInfo")
    public Result userInfo(@RequestBody JSONObject postParams) {
        JSONObject result = shoppingMallUserService.userInfo(postParams);
        return Result.success(result);
    }

    /**
     * 添加商家的同时为商家注册用户
     * @param merchantUserVo
     * @return
     */
    @PostMapping("/addMerchant")
    public Result addMerchant(@RequestBody MerchantUserVo merchantUserVo) {
        shoppingMallUserService.addMerchant(merchantUserVo);
        return Result.success("操作成功");
    }

    /**
     * 修改商家的同时修改商家用户表
     * @param merchantUserVo
     * @return
     */
    @PostMapping("/modifyMerchant")
    public Result modifyMerchant(@RequestBody MerchantUserVo merchantUserVo) {
        shoppingMallUserService.modifyMerchant(merchantUserVo);
        return Result.success("操作成功");
    }


    /**
     * 用户登录 (auth密码模式登录)
     * @param loginData
     * @return
     * @throws Exception
     */
    @PostMapping("/app/login")
    public Result login(@RequestBody LoginData loginData) {
        try {
            UserUsersVO userUsersVO  = authClient.login(loginData).getData();
            String realName = userUsersVO.getRealName();
            JSONObject resultJson = new JSONObject();
            JSONObject userInfoJson = new JSONObject();
            userInfoJson.put("user_login",realName);
            userInfoJson.put("mobile",userUsersVO.getTel());
            userInfoJson.put("token", userUsersVO.getToken());
            userInfoJson.put("type",10);
            userInfoJson.put("id",userUsersVO.getId());
            resultJson.put("user_info",userInfoJson);
            return Result.success(resultJson);
        }catch (Exception e){
            log.error("login error={}",e);
            return Result.error(ResultTypeEnum.SERVICE_ERROR);
        }

    }


}
