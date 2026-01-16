package com.ht.feignapi.prime.controller;

import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.entity.BindTelData;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.service.PrimeUserService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.user.entity.UserUsersVO;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.tonglian.utils.Cache;
import com.ht.feignapi.tonglian.utils.CacheManager;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/ms/userInfo")
@CrossOrigin(allowCredentials = "true")
public class PrimeUserInfoController {


    private Logger logger = LoggerFactory.getLogger(PrimeUserConsumerController.class);

    @Autowired
    private PrimeUserService primeUserService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    /**
     * C端商城  前端提交code码获取openid和用户信息(登录接口)
     *
     * @param code
     * @return
     */
    @GetMapping("/getOpenidAndUserInfo")
    public UserUsersVO getOpenid(@RequestParam("code") String code) {
        logger.info("code=====================" + code);
        if (StringUtils.isEmpty(code)) {
            throw new CheckException(ResultTypeEnum.OPENID_ERROR);
        }
        UserUsersVO userUsersVO = primeUserService.getOpenidAndUserMsg(code);
        return userUsersVO;
    }


    /**
     * C端商城  根据openid 查询用户信息
     *
     * @param openid
     * @return
     */
    @GetMapping("/getUserInfo")
    public UserUsersVO getUserByOpenid(@RequestParam("openid") String openid) {
        logger.info("openid=========" + openid);
        if (StringUtils.isEmpty(openid)) {
            throw new CheckException(ResultTypeEnum.OPENID_ERROR);
        }
        UserUsersVO usrUsersVO = primeUserService.queryUserByOpenid(openid);
        return usrUsersVO;
    }

    /**
     * 根据手机号发送验证码接口
     *
     * @param phoneNum
     * @return
     */
    @GetMapping("/sendCode/{phoneNum}")
    public void sendCode(@PathVariable("phoneNum") String phoneNum) {
        //生成验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        CacheManager.putCacheInfo(phoneNum + "&authCode",
                new Cache(phoneNum, code, System.currentTimeMillis(), false),
                System.currentTimeMillis());
        userUsersService.sendCode(phoneNum, code);
    }

//    /**
//     * 免税用户绑定手机 同时开卡
//     *
//     * @param authCode
//     * @param phoneNum
//     * @param openid
//     * @return
//     */
//    @PostMapping("/bindPhoneNum")
//    public void bindPhoneNum(String authCode, String phoneNum, String openid) {
//        UserUsers userUsers = authClientService.queryByTel(phoneNum, AppConstant.MS_APP_CODE).getData();
//        Cache cacheInfo = CacheManager.getCacheInfo(phoneNum + "&authCode");
//        Cache cache = (Cache) cacheInfo.getValue();
//        String cacheAuthCode = (String) cache.getValue();
//        System.out.println(cacheAuthCode);
//        System.out.println(authCode);
//        if (StringUtils.isEmpty(cacheAuthCode) || !cacheAuthCode.equals(authCode)) {
//            throw new CheckException(ResultTypeEnum.AUTH_CODE_ERROR);
//        }
//        primeUserService.bindPhoneNum(phoneNum, openid,userUsers);
//    }

    /**
     * 校验用户是否绑定手机
     * @param openId
     * @return
     */
    @GetMapping("/checkUserBindTel")
    public Map checkUserBindTel(@RequestParam("openId")String openId) {
        UserUsers userUsers = authClientService.queryByOpenid(openId).getData();
        if (StringUtils.isEmpty(userUsers.getTel())){
            Map map = new HashMap();
            map.put("isBindTel",true);
            map.put("tel","");
            return map;
        }else {
            Map map = new HashMap();
            map.put("isBindTel",false);
            map.put("tel",userUsers.getTel());
            return map;
        }
    }

}
