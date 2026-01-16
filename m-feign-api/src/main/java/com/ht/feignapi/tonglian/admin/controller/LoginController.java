package com.ht.feignapi.tonglian.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.LoginData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.admin.entity.LoginVo;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.constant.MerchantConstant;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;

import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/17 10:19
 */
@RestController
@RequestMapping("/tonglian/adminLogin")
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    /**
     * 后台控制器登录
     *
     * @param loginVo 登录参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map login(@RequestBody LoginVo loginVo) {
        Assert.notNull(loginVo.getMerchantCode(),"商户号不能为空");
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

        Result<List<MrcMapMerchantUser>> merchantUserResult = merchantsClientService.getMerchantAdminUserList(loginVo.getMerchantCode());
        logger.info("***************************loginVo*****************************" + loginVo);
        logger.info("**********merchantUserList***************" + JSON.toJSONString(merchantUserResult));
        Assert.isTrue(
                ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(merchantUserResult.getCode())
                        &&!CollectionUtils.isEmpty(merchantUserResult.getData()),"获取商户用户数据出错");
        MrcMapMerchantUser merchantUserExist = null;
        for (MrcMapMerchantUser merchantUser:merchantUserResult.getData()) {
            if (merchantUser.getMerchantCode().equals(loginVo.getMerchantCode())) {
                merchantUserExist = merchantUser;
                break;
            }
        }
        if (merchantUserExist == null){
            throw new CheckException(ResultTypeEnum.MERCHANT_ERROR);
        }
        if (!MerchantConstant.MERCHANT_ADMIN_ENABLE.equals(merchantUserExist.getState())){
            throw new CheckException(ResultTypeEnum.MERCHANT_ERROR,"账号被禁用，请联系管理员");
        }

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
