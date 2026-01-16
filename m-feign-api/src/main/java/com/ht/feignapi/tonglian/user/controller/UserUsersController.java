package com.ht.feignapi.tonglian.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.config.DicConstantGroupCode;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tonglian.user.entity.*;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.tonglian.user.service.impl.UserUsersServiceImpl;
import com.ht.feignapi.tonglian.utils.Cache;
import com.ht.feignapi.tonglian.utils.CacheManager;
import com.ht.feignapi.tonglian.utils.QrCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author suyangy
 * @since 2020-06-15
 */
@RestController
@RequestMapping(value = "/tonglian/userUsers",produces = "application/json")
@CrossOrigin(allowCredentials = "true")
public class UserUsersController {

    private Logger logger = LoggerFactory.getLogger(UserUsersController.class);

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private MapMerchantPrimesClientService mapMerchantPrimesClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    /**
     * pos端会员用户注册
     *
     * @param registerVipUserData
     * @return
     */
    @PostMapping("/register")
    public void register(@RequestBody RegisterVipUserData registerVipUserData) {
        //校验验证码
        Cache cacheInfo = CacheManager.getCacheInfo(registerVipUserData.getPhoneNum() + "&authCode");
        if (cacheInfo == null) {
            throw new CheckException(ResultTypeEnum.REGISTER_ERROR);
        }
        Cache cache = (Cache) cacheInfo.getValue();
        String cacheAuthCode = (String) cache.getValue();
        if (StringUtils.isEmpty(cacheAuthCode) || !cacheAuthCode.equals(registerVipUserData.getAuthCode())) {
            throw new CheckException(ResultTypeEnum.AUTH_CODE_ERROR);
        }
        //校验用户是否已注册过
        String objectMerchantCode = merchantsClientService.queryObjectMerchantCode(registerVipUserData.getMerchantCode()).getData();
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByTelAndMerchantCode(registerVipUserData.getPhoneNum(), objectMerchantCode).getData();

        if (mrcMapMerchantPrimes!=null){
            throw new CheckException(ResultTypeEnum.USER_NOT_NULL);
        }
        //注册用户信息
        userUsersService.add(registerVipUserData.getCardCode(),
                registerVipUserData.getGender(),
                registerVipUserData.getPassword(),
                registerVipUserData.getPhoneNum(),
                registerVipUserData.getRealName(),
                registerVipUserData.getMerchantCode(),
                registerVipUserData.getBirthday());
    }

    /**
     * C端 pos端 根据手机号发送验证码接口
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

    /**
     * pos端手机验证码验证,根据手机号查询用户信息
     *
     * @param phoneNum
     * @param authCode
     * @return
     */
    @GetMapping("/checkAuthCode/{phoneNum}/{authCode}/{merchantCode}")
    public UserUsers checkAuthCode(@PathVariable("phoneNum") String phoneNum,
                                   @PathVariable("authCode") String authCode,
                                   @PathVariable("merchantCode")String merchantCode) {
        Cache cacheInfo = CacheManager.getCacheInfo(phoneNum + "&authCode");
        Cache cache = (Cache) cacheInfo.getValue();
        String cacheAuthCode = (String) cache.getValue();
        if (StringUtils.isEmpty(cacheAuthCode) || !cacheAuthCode.equals(authCode)) {
            throw new CheckException(ResultTypeEnum.AUTH_CODE_ERROR);
        }
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByTelAndMerchantCode(phoneNum, merchantCode).getData();
        UserUsers users = authClientService.getUserByIdTL(mrcMapMerchantPrimes.getUserId().toString()).getData();
        return users;
    }

    /**
     * pos端  根据用户手机号修改用户密码
     *
     * @param updatePasswordData
     * @return
     */
    @PostMapping("/updatePassword")
    public void updatePassword(@RequestBody UpdatePasswordData updatePasswordData) {
        userUsersService.updatePassword(updatePasswordData.getNewPassword(), updatePasswordData.getPhoneNum());
    }


    /**
     * C端公众号 根据用户openid 修改用户密码
     *
     * @param updatePasswordOpenidData
     * @return
     */
    @PostMapping("/updatePasswordOpenid")
    public void updatePasswordOpenid(@RequestBody UpdatePasswordOpenidData updatePasswordOpenidData) {
        userUsersService.updatePasswordByOpenid(updatePasswordOpenidData.getNewPassword(), updatePasswordOpenidData.getPhoneNum(), updatePasswordOpenidData.getOpenid());
    }

    /**
     * pos端用户绑定会员卡
     *
     * @param userBindCardData
     * @return
     */
    @PostMapping("/userBindCard")
    public void userBindCard(@RequestBody UserBindCardData userBindCardData) {
        Cache cacheInfo = CacheManager.getCacheInfo(userBindCardData.getPhoneNum() + "&authCode");
        Cache cache = (Cache) cacheInfo.getValue();
        String cacheAuthCode = (String) cache.getValue();
        if (StringUtils.isEmpty(cacheAuthCode) || !cacheAuthCode.equals(userBindCardData.getAuthCode())) {
            throw new CheckException(ResultTypeEnum.AUTH_CODE_ERROR);
        }
        if (StringUtils.isEmpty(userBindCardData.getIcCardId())) {
            throw new CheckException(ResultTypeEnum.IC_CARD_ID_NULL);
        }
        userUsersService.userBindCard(userBindCardData);
    }

    /**
     * C端  前端提交code码获取openid和用户信息(登录接口)
     *
     * @param code
     * @param merchantCode
     * @return
     */
    @GetMapping("/getOpenid/{code}/{merchantCode}")
    public UserUsersVO getOpenid(@PathVariable("code") String code, @PathVariable("merchantCode") String merchantCode) {
        UserUsersVO userUsersVO = userUsersService.getOpenidAndUserMsg(code, merchantCode);
        return userUsersVO;
    }

    /**
     * C端公众号  根据openid 查询用户信息
     *
     * @param openid
     * @param merchantCode
     * @return
     */
    @GetMapping("/getUserMsg/{openid}/{merchantCode}")
    public UserUsersVO getUserByOpenid(@PathVariable("openid") String openid, @PathVariable("merchantCode") String merchantCode) {
        UserUsersVO usrUsersVO = userUsersService.queryUserByOpenid(openid, merchantCode);
        return usrUsersVO;
    }

    /**
     * C端公众号  用户信息绑定手机  注册为商户的会员
     *
     * @param authCode
     * @param phoneNum
     * @param openid
     * @param merchantCode
     * @return
     */
    @PostMapping("/bindPhoneNum")
    public void bindPhoneNum(String authCode, String phoneNum, String openid, String merchantCode) {
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByTelAndMerchantCode(phoneNum, merchantCode).getData();
        if (mrcMapMerchantPrimes != null && !StringUtils.isEmpty(mrcMapMerchantPrimes.getOpenId())){
            throw new CheckException(ResultTypeEnum.USER_NOT_NULL);
        }
        Cache cacheInfo = CacheManager.getCacheInfo(phoneNum + "&authCode");
        Cache cache = (Cache) cacheInfo.getValue();
        String cacheAuthCode = (String) cache.getValue();
        System.out.println(cacheAuthCode);
        System.out.println(authCode);
        if (StringUtils.isEmpty(cacheAuthCode) || !cacheAuthCode.equals(authCode)) {
            throw new CheckException(ResultTypeEnum.AUTH_CODE_ERROR);
        }
        userUsersService.bindPhoneNum(phoneNum, openid, merchantCode);
    }

    /**
     * C端公众号  用户完善用户信息
     *
     * @param openid
     * @param realName
     * @param gender
     * @param birthday
     * @param marriage
     * @param job
     * @param idCardType
     * @param idCardNum
     * @return
     */
    @PostMapping("/updateUserMsg")
    public void updateUserMsg(String openid, String realName, String gender, String birthday, String marriage, String job, String idCardType, String idCardNum) {
        userUsersService.updateUserMsg(openid, realName, gender, birthday, marriage, job, idCardType, idCardNum);
    }

    /**
     * 用户修改手机号
     *
     * @param tel
     * @param authCode
     * @param openid
     * @return
     */
    @PostMapping("/updateTel")
    public String updateTel(String tel, String authCode, String openid,String merchantCode) {
        Cache cacheInfo = CacheManager.getCacheInfo(tel + "&authCode");
        Cache cache = (Cache) cacheInfo.getValue();
        String cacheAuthCode = (String) cache.getValue();
        if (StringUtils.isEmpty(cacheAuthCode) || !cacheAuthCode.equals(authCode)) {
            throw new CheckException(ResultTypeEnum.AUTH_CODE_ERROR);
        }
        userUsersService.updateTelByOpenid(openid, tel,merchantCode);
        return tel;
    }

    /**
     * C端用户 绑定实体储值卡
     *
     * @param icCardId
     * @param userId
     * @param merchantCode
     * @return
     */
    @PostMapping("/userBindActualCard")
    public void userBindActualCard(String icCardId, String userId, String merchantCode) {
        userUsersService.userBindActualCard(icCardId, userId, merchantCode);
    }



    /**
     * pos端用户余额查询  根据手机号或者 实体卡号 或生成的会员码
     *
     * @param queryAccountMoneyData
     * @return
     */
    @PostMapping("/queryAccountMoney")
    public BigDecimal queryAccountMoney(@RequestBody QueryAccountMoneyData queryAccountMoneyData) {
        String tel = queryAccountMoneyData.getTel();
        if (!StringUtils.isEmpty(tel)) {
            Cache cacheInfo = CacheManager.getCacheInfo(tel + "&authCode");
            Cache cache = (Cache) cacheInfo.getValue();
            String cacheAuthCode = (String) cache.getValue();
            if (!StringUtils.isEmpty(cacheAuthCode) && cacheAuthCode.equals(queryAccountMoneyData.getAuthCode())) {
                BigDecimal moneyBigDecimal = userUsersService.queryAccountMoney(queryAccountMoneyData);
                return moneyBigDecimal;
            } else {
                throw new CheckException(ResultTypeEnum.SERVICE_ERROR);
            }
        }
        BigDecimal moneyBigDecimal = userUsersService.queryAccountMoney(queryAccountMoneyData);
        return moneyBigDecimal;
    }

    /**
     * 根据openid获取用户会员码
     *
     * @param openid
     * @return
     */
    @GetMapping("/userQrCode/{openid}")
    public String getUserQrCodeByOpenid(@PathVariable("openid") String openid) {
        String romCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        String content = openid + romCode;
        String code = QrCodeUtils.creatRrCode(content, 200, 200);
        String replace = code.replace("\r\n", "");
        String replaceOne = replace.replace("\n", "");
        String replaceTwo = replaceOne.replace("\r", "");
        return replaceTwo;
    }


    /**
     * 根据openid获取用户余额
     *
     * @param openid
     * @return
     */
    @GetMapping("/userMoney/{openid}")
    public Integer getUserMoneyByOpenid(@PathVariable("openid") String openid) {
        logger.info("提交的openid==========="+openid);
        UserUsers userUsers = userUsersService.queryUserByOpenid(openid);
        BigDecimal bigDecimal = null;
        try {
            bigDecimal = cardMapUserClientService.queryUserMoney(userUsers.getId()).getData();
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return 0;
        }
        return Integer.parseInt(bigDecimal.toString());
    }


}

