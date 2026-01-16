package com.ht.feignapi.tonglian.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.RetServiceData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.OpenIdException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.admin.excel.entity.MemberImportVo;
import com.ht.feignapi.tonglian.card.clientservice.*;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.card.service.CardUserService;
import com.ht.feignapi.tonglian.config.*;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tonglian.merchant.service.MerchantPrimeService;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.tonglian.sysconstant.clientservice.DicConstantClientService;
import com.ht.feignapi.tonglian.user.entity.*;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.tonglian.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Service
public class UserUsersServiceImpl implements UserUsersService {
    private Logger logger = LoggerFactory.getLogger(UserUsersServiceImpl.class);

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MapMerchantPrimesClientService mapMerchantPrimesClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardMapMerchantCardClientService cardMapMerchantCardClientService;

    @Autowired
    private CardCardsClientService cardCardsClientService;

    @Autowired
    private CardOrderClientService cardOrderClientService;

    @Autowired
    private DicConstantClientService dicConstantClientService;

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private MerchantPrimeService merchantPrimeService;

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;

    /**
     * 用户会员注册 信息录入
     *
     * @param cardCode
     * @param gender
     * @param password
     * @param phoneNum
     * @param realName
     * @param merchantCode
     * @param birthday
     */
    @Override
    public void add(String cardCode, String gender, String password, String phoneNum, String realName, String merchantCode, String birthday) {
        UserUsers userUsers = new UserUsers();
        userUsers.setIdCardNum(cardCode);
        userUsers.setGender(gender);
        userUsers.setPassword(password);
        userUsers.setRealName(realName);
        userUsers.setTel(phoneNum);
        userUsers.setUpdateAt(new Date());
        userUsers.setBirthday(birthday);
        userUsers.setAppCode(AppConstant.TONGLIAN_APP_CODE);
        userUsers.setAppName(AppConstant.TONGLIAN_APP_NAME);
        RetServiceData retServiceData = authClientService.register(userUsers).getData();
        Object data = retServiceData.getData();
        String string = JSONObject.toJSONString(data);
        UserUsers users = JSONObject.parseObject(string, UserUsers.class);
        String objectMerchantCode = merchantsClientService.queryObjectMerchantCode(merchantCode).getData();
        MrcMapMerchantPrimes mapMerchantPrimes = new MrcMapMerchantPrimes();
        Integer perPaymentLimit = Integer.parseInt(merchantsConfigClientService.getConfigByKey(merchantCode, "PER_PAYMENT_LIMIT").getData());
        Integer dailyPaymentLimit = Integer.parseInt(merchantsConfigClientService.getConfigByKey(merchantCode, "DAILY_PAYMENT_LIMIT").getData());
        mapMerchantPrimes.setPerPaymentLimit(perPaymentLimit);
        mapMerchantPrimes.setDailyPaymentLimit(dailyPaymentLimit);
        mapMerchantPrimes.setUserId(users.getId());
        mapMerchantPrimes.setMerchantCode(objectMerchantCode);
        mapMerchantPrimes.setState("");
        mapMerchantPrimes.setType("");
        mapMerchantPrimes.setTel(phoneNum);
        mapMerchantPrimes.setCreateAt(new Date());
        mapMerchantPrimes.setUpdateAt(new Date());
        mapMerchantPrimesClientService.saveOrUpdate(mapMerchantPrimes);
        try {
            //用户注册调取通联开卡接口 作为用户账户余额 钱包
            String content = OpenCardUtil.callOpenCard(phoneNum);
            PpcsCloudCardOpenReturnData returnData = JSONObject.parseObject(content, PpcsCloudCardOpenReturnData.class);
            PpcsCloudCardOpenResponse ppcsCloudCardOpenResponse = returnData.getPpcs_cloud_card_open_response();
            if (0 == ppcsCloudCardOpenResponse.getResult()) {
                //绑定用户信息 与通联卡 钱包 关系
                cardMapUserClientService.createCardMapUserCards(TongLianCardState.CARD_CODE.getCode() + "", users.getId(), objectMerchantCode,
                        TongLianCardState.CARD_NAME.getDesc(),
                        TongLianCardState.CATEGORY.getCode() + "",
                        TongLianCardState.CATEGORY.getDesc(),
                        TongLianCardState.STATE_NORMAL.getDesc(),
                        TongLianCardState.TYPE.getDesc(),
                        ppcsCloudCardOpenResponse.getCard_id());
            } else {
                logger.info("手机号:" + phoneNum + ",已调取过通联接口,开通过通联虚拟卡");
            }
        } catch (Exception e) {
            logger.info("手机号:" + phoneNum + "开卡异常:" + e.getMessage());
        }
    }

    private PpcsCloudCardOpenResponse getPpcsCloudCardOpenResponse(String phoneNum) {
        String content = null;
        try {
            content = OpenCardUtil.callOpenCard(phoneNum);
        } catch (IOException e) {
            logger.info("调取通联开卡接口异常" + e.getMessage());
        }
        PpcsCloudCardOpenReturnData returnData = JSONObject.parseObject(content, PpcsCloudCardOpenReturnData.class);
        return returnData.getPpcs_cloud_card_open_response();
    }

    /**
     * 发送手机验证码
     *
     * @param phoneNum
     * @param code
     * @return
     */
    @Override
    public void sendCode(String phoneNum, String code) {
        try {
            //调用阿里工具类发送验证码
            AliMsgSendUtil.sendMsg(phoneNum, code);
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void sendOrderMsg(String phoneNum) throws ClientException {
        AliMsgSendUtil.sendNotifyMsg(phoneNum,"SMS_212275616");
    }

    @Override
    public void sendRefundMsg(String phoneNum,Integer count) throws ClientException {
        AliMsgSendUtil.sendNotifyMsg(phoneNum,"");
    }

    /**
     * 根据用户手机号,修改用户密码
     *
     * @param newPassword
     * @param phoneNum
     */
    @Override
    public void updatePassword(String newPassword, String phoneNum) {
        UserUsers userUsers = new UserUsers();
        userUsers.setPassword(newPassword);
        userUsers.setTel(phoneNum);
        userUsers.setAppCode(AppConstant.TONGLIAN_APP_CODE);
        authClientService.updatePasswordByTel(newPassword, phoneNum, AppConstant.TONGLIAN_APP_CODE);
    }

    /**
     * 重置用户密码
     *
     * @param newPassword
     * @param userId
     */
    @Override
    public void resetPassword(String newPassword, Long userId) {
        Result result = authClientService.getUserByIdTL(userId + "");
        String string = JSONObject.toJSONString(result);
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
        authClientService.updatePasswordByTel(newPassword, userUsers.getTel(), userUsers.getAppCode());
    }


    /**
     * 用户会员卡绑定
     *
     * @param userBindCardData
     * @return
     */
    @Override
    public void userBindCard(UserBindCardData userBindCardData) {
        //进行校验  用户是否注册
        UserUsers userUsers = merchantPrimeService.primeQueryUserByTelAndCode(userBindCardData.getPhoneNum(), userBindCardData.getMerchantCode());
        if (userUsers == null) {
            throw new CheckException(ResultTypeEnum.USER_NULL);
        }

        //进行校验  卡号是否已和用户进行过绑定
        CardMapUserCards cardMapUserCards = cardMapUserClientService.queryByIcCardIdAndUserId(userBindCardData.getIcCardId(), userUsers.getId()).getData();
        if (cardMapUserCards != null) {
            throw new CheckException(ResultTypeEnum.CARD_BIND);
        }

        //绑卡关联
        cardMapUserClientService.bindIcCardId(userBindCardData.getCardNo(),
                userUsers.getId(),
                userBindCardData.getIcCardId());
    }

    @Override
    public UserUsers getUserByAccount(String account) {
        UserUsers userUsers = authClientService.queryUserByAccount(account, AppConstant.TONGLIAN_APP_CODE).getData();
        return userUsers;
    }


    /**
     * 获取openid和用户信息
     *
     * @param code
     * @param merchantCode
     * @return
     */
    @Override
    public UserUsersVO getOpenidAndUserMsg(String code, String merchantCode) {
        String wxAppId = merchantsConfigClientService.getConfigByKey(merchantCode, "WX_APPID").getData();
        String wxAppSecret = merchantsConfigClientService.getConfigByKey(merchantCode, "WX_APPSECRET").getData();
        WXData wxData = getOpenid(code, wxAppId, wxAppSecret);
        CacheManager.putCacheInfo(WeChatConfig.ACCESS_TOKEN +merchantCode,
                new Cache(wxData.getAccessToken(), wxData.getRefreshToken(), System.currentTimeMillis(),
                        false),
                System.currentTimeMillis());
        if (StringUtils.isEmpty(wxData.getOpenid()) || "null".equals(wxData.getOpenid())) {
            throw new OpenIdException(ResultTypeEnum.OPENID_ERROR.getCode(), wxData.getErrMsg());
        }
        Result result = authClientService.queryByOpenid(wxData.getOpenid());
        String string = JSONObject.toJSONString(result.getData());
        UserUsers usrUsers = JSONObject.parseObject(string, UserUsers.class);
        if (usrUsers == null) {
            WXUser wechatUserInfo = getWechatUserInfo(wxData.getOpenid(), wxData.getAccessToken());
            if (!StringUtils.isEmpty(wechatUserInfo.getErrCode())) {
                throw new OpenIdException(Integer.parseInt(wechatUserInfo.getErrCode()), wechatUserInfo.getErrMsg());
            }
            UserUsersVO userUsersVO = new UserUsersVO();
            userUsersVO.setNickName(wechatUserInfo.getNickname());
            userUsersVO.setOpenId(wechatUserInfo.getOpenid());
            userUsersVO.setHeadPicUrl(wechatUserInfo.getPictureURL());
            userUsersVO.setGender(wechatUserInfo.getSex() == 1 ? "男" : "女");
            userUsersVO.setCardNum(0);
            userUsersVO.setPoint(0);
            userUsersVO.setMoney(BigDecimal.ZERO);
            userUsersVO.setIsVip(false);
            userUsersVO.setVipType("非会员用户");
            userUsersVO.setTel("无绑定手机号");
            return userUsersVO;
        }
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByUserIdAndMerchantCode(usrUsers.getId(), merchantCode).getData();
        if (mrcMapMerchantPrimes == null) {
            UserUsersVO userUsersVO = new UserUsersVO();
            BeanUtils.copyProperties(usrUsers, userUsersVO);
            userUsersVO.setCardNum(0);
            userUsersVO.setPoint(0);
            userUsersVO.setMoney(BigDecimal.ZERO);
            userUsersVO.setIsVip(false);
            userUsersVO.setVipType("非会员用户");
            userUsersVO.setTel("无绑定手机号");
            return userUsersVO;
        }
        UserUsersVO userUsersVO = new UserUsersVO();
        BeanUtils.copyProperties(usrUsers, userUsersVO);
        //获取卡券数量
        Integer cardNum = cardUserService.queryCardNum(usrUsers.getId(), merchantCode);
        userUsersVO.setCardNum(cardNum);
        userUsersVO.setPoint(0);
        BigDecimal money = cardMapUserClientService.queryUserMoney(usrUsers.getId()).getData();
        userUsersVO.setMoney(money);
        userUsersVO.setIsVip(true);
        userUsersVO.setVipType(mrcMapMerchantPrimes.getType());
        return userUsersVO;
    }


    /**
     * C端用户绑定手机号注册为商户会员
     *
     * @param phoneNum
     * @param openid
     * @param merchantCode
     */
    @Override
    public void bindPhoneNum(String phoneNum, String openid, String merchantCode) {
        Cache cacheInfo = CacheManager.getCacheInfo(WeChatConfig.ACCESS_TOKEN +merchantCode);
        Cache cache = (Cache) cacheInfo.getValue();
        String wxAppId = merchantsConfigClientService.getConfigByKey(merchantCode, "WX_APPID").getData();
        WXData wxData = getAccessToken((String) cache.getValue(), wxAppId);
        WXUser wechatUserInfo = getWechatUserInfo(openid, wxData.getAccessToken());

        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByTelAndMerchantCode(phoneNum, merchantCode).getData();
        if (mrcMapMerchantPrimes != null && StringUtils.isEmpty(mrcMapMerchantPrimes.getOpenId())){
            UserUsers userUsers = authClientService.getUserByIdTL(mrcMapMerchantPrimes.getUserId().toString()).getData();
            userUsers.setOpenId(openid);
            try {
                if (StringUtils.isEmpty(wechatUserInfo.getErrCode())) {
                    String sex = wechatUserInfo.getSex() == 1 ? "男" : "女";
                    userUsers.setHeadPicUrl(wechatUserInfo.getPictureURL());
                    userUsers.setNickName(wechatUserInfo.getNickname());
                    userUsers.setGender(sex);
                    authClientService.updateUserTL(userUsers);
                } else {
                    logger.info("更新pos端用户信息失败openid为" + openid);
                }
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
            mrcMapMerchantPrimes.setOpenId(openid);
            mapMerchantPrimesClientService.saveOrUpdate(mrcMapMerchantPrimes);
        }else {
            UserUsers queryUser = authClientService.queryByOpenid(openid).getData();
            Long userId = -1L;
            if (queryUser==null){
                UserUsers usrUsersSave = new UserUsers();
                usrUsersSave.setOpenId(openid);
                usrUsersSave.setTel(phoneNum);
                try {
                    if (StringUtils.isEmpty(wechatUserInfo.getErrCode())) {
                        usrUsersSave.setNickName(wechatUserInfo.getNickname());
                        usrUsersSave.setHeadPicUrl(wechatUserInfo.getPictureURL());
                        String sex = wechatUserInfo.getSex() == 1 ? "男" : "女";
                        usrUsersSave.setGender(sex);
                    }
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
                usrUsersSave.setAppCode(AppConstant.TONGLIAN_APP_CODE);
                usrUsersSave.setAppName(AppConstant.TONGLIAN_APP_NAME);
                Object userObj = authClientService.register(usrUsersSave).getData().getData();
//            Result result1 = authClientService.queryByOpenid(openid);
                String string = JSONObject.toJSONString(userObj);
                UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
                userId = userUsers.getId();
            }else {
                userId = queryUser.getId();
            }
            //添加商户与用户关联
            MrcMapMerchantPrimes mrcMapMerchantPrimesSave = new MrcMapMerchantPrimes();
            mrcMapMerchantPrimesSave.setOpenId(openid);
            mrcMapMerchantPrimesSave.setTel(phoneNum);
            mrcMapMerchantPrimesSave.setMerchantCode(merchantCode);
            mrcMapMerchantPrimesSave.setUserId(userId);
            mrcMapMerchantPrimesSave.setState("正常");
            mrcMapMerchantPrimesSave.setType("黄金会员");
            mrcMapMerchantPrimesSave.setCreateAt(new Date());
            mrcMapMerchantPrimesSave.setUpdateAt(new Date());
            Integer perPaymentLimit = Integer.parseInt(merchantsConfigClientService.getConfigByKey(merchantCode, "PER_PAYMENT_LIMIT").getData());
            Integer dailyPaymentLimit = Integer.parseInt(merchantsConfigClientService.getConfigByKey(merchantCode, "DAILY_PAYMENT_LIMIT").getData());
            mrcMapMerchantPrimesSave.setDailyPaymentLimit(dailyPaymentLimit);
            mrcMapMerchantPrimesSave.setPerPaymentLimit(perPaymentLimit);
            mrcMapMerchantPrimesSave.setPrimePoints(0);
            mapMerchantPrimesClientService.saveOrUpdate(mrcMapMerchantPrimesSave);

            try {
                String romCode = String.valueOf((long) ((Math.random() * 9 + 1) * 10000000000L));
                String content = OpenCardUtil.callOpenCard(romCode);
                PpcsCloudCardOpenReturnData returnData = JSONObject.parseObject(content, PpcsCloudCardOpenReturnData.class);
                PpcsCloudCardOpenResponse ppcsCloudCardOpenResponse = returnData.getPpcs_cloud_card_open_response();
                if (0 == ppcsCloudCardOpenResponse.getResult()) {
                    logger.info("开卡成功,创建用户余额关联");
                    cardMapUserClientService.createCardMapUserCards(TongLianCardState.CARD_CODE.getCode() + "", userId, merchantCode,
                            TongLianCardState.CARD_NAME.getDesc(),
                            TongLianCardState.CATEGORY.getCode() + "",
                            TongLianCardState.CATEGORY.getDesc(),
                            TongLianCardState.STATE_NORMAL.getDesc(),
                            TongLianCardState.TYPE.getDesc(),
                            ppcsCloudCardOpenResponse.getCard_id());
                    CardMoneyAddUtil.cardMoneyAdd(ppcsCloudCardOpenResponse.getCard_id(),0);
                } else {
                    logger.info("手机号:" + phoneNum + ",已调取过通联接口,开通过通联虚拟卡");
                }
            } catch (Exception e) {
                logger.info("手机号:" + phoneNum + "开卡异常:" + e.getMessage());
            }
        }
    }

    /**
     * 完善用户信息
     *
     * @param openid
     * @param realName
     * @param gender
     * @param birthday
     * @param marriage
     * @param job
     * @param idCardType
     * @param idCardNum
     */
    @Override
    public void updateUserMsg(String openid, String realName, String gender, String birthday, String
            marriage, String job, String idCardType, String idCardNum) {
        Result result = authClientService.queryByOpenid(openid);
        String string = JSONObject.toJSONString(result.getData());
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
        userUsers.setRealName(realName);
        userUsers.setGender(gender);
        userUsers.setBirthday(birthday);
        userUsers.setMarriage(marriage);
        userUsers.setJob(job);
        userUsers.setIdCardType(idCardType);
        userUsers.setIdCardNum(idCardNum);
        authClientService.updateUserTL(userUsers);
    }

    /**
     * 修改用户手机号
     *  @param openid
     * @param tel
     * @param merchantCode
     */
    @Override
    public void updateTelByOpenid(String openid, String tel, String merchantCode) {
        Result result = authClientService.queryByOpenid(openid);
        String string = JSONObject.toJSONString(result.getData());
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
        try {
            Cache cacheInfo = CacheManager.getCacheInfo(WeChatConfig.ACCESS_TOKEN +merchantCode);
            Cache cache = (Cache) cacheInfo.getValue();
            String wxAppId = merchantsConfigClientService.getConfigByKey(merchantCode, "WX_APPID").getData();
            WXData wxData = getAccessToken((String) cache.getValue(), wxAppId);
            WXUser wechatUserInfo = getWechatUserInfo(openid, wxData.getAccessToken());
            userUsers.setNickName(wechatUserInfo.getNickname());
            userUsers.setHeadPicUrl(wechatUserInfo.getPictureURL());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            throw new OpenIdException(ResultTypeEnum.WX_AUTH_ERROR);
        }
        userUsers.setTel(tel);
        authClientService.updateUserTL(userUsers);
    }


    /**
     * 根据openid查询数据库用户信息
     *
     * @param openid
     * @param merchantCode
     * @return
     */
    @Override
    public UserUsersVO queryUserByOpenid(String openid, String merchantCode) {
        Result result = authClientService.queryByOpenid(openid);
        String string = JSONObject.toJSONString(result.getData());
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
        UserUsersVO userUsersVO = new UserUsersVO();
        if (userUsers == null) {
            try {
                Cache cacheInfo = CacheManager.getCacheInfo(WeChatConfig.ACCESS_TOKEN +merchantCode);
                Cache cache = (Cache) cacheInfo.getValue();
                String wxAppId = merchantsConfigClientService.getConfigByKey(merchantCode, "WX_APPID").getData();
                WXData wxData = getAccessToken((String) cache.getValue(), wxAppId);
                WXUser wechatUserInfo = getWechatUserInfo(openid, wxData.getAccessToken());
                userUsersVO.setNickName(wechatUserInfo.getNickname());
                userUsersVO.setHeadPicUrl(wechatUserInfo.getPictureURL());
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.getMessage());
                userUsersVO.setNickName(userUsers.getTel());
                userUsersVO.setHeadPicUrl(UserDefaultHeadPicUrl.defaultHeadPic);
            }
            userUsersVO.setMoney(BigDecimal.ZERO);
            userUsersVO.setPoint(0);
            userUsersVO.setCardNum(0);
            userUsersVO.setVipType("非会员用户");
            userUsersVO.setTel("无绑定手机号");
            userUsersVO.setIsVip(false);
            return userUsersVO;
        }
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByUserIdAndMerchantCode(userUsers.getId(), merchantCode).getData();
        if (mrcMapMerchantPrimes == null) {
            if (userUsers.getOpenId() == null) {
                userUsers.setOpenId(openid);
                authClientService.updateUserTL(userUsers);
            }
            BeanUtils.copyProperties(userUsers, userUsersVO);
            userUsersVO.setCardNum(0);
            userUsersVO.setPoint(0);
            userUsersVO.setVipType("非会员用户");
            userUsersVO.setTel("无绑定手机号");
            userUsersVO.setMoney(BigDecimal.ZERO);
            userUsersVO.setIsVip(false);
            return userUsersVO;
        }
        try {
            if (StringUtils.isEmpty(userUsers.getNickName())){
                Cache cacheInfo = CacheManager.getCacheInfo(WeChatConfig.ACCESS_TOKEN +merchantCode);
                Cache cache = (Cache) cacheInfo.getValue();
                String wxAppId = merchantsConfigClientService.getConfigByKey(merchantCode, "WX_APPID").getData();
                WXData wxData = getAccessToken((String) cache.getValue(), wxAppId);
                WXUser wechatUserInfo = getWechatUserInfo(openid, wxData.getAccessToken());
                userUsers.setNickName(wechatUserInfo.getNickname());
                userUsers.setHeadPicUrl(wechatUserInfo.getPictureURL());
                authClientService.updateUserTL(userUsers);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            userUsers.setNickName(userUsers.getTel());
            userUsers.setHeadPicUrl(UserDefaultHeadPicUrl.defaultHeadPic);
        }
        BeanUtils.copyProperties(userUsers, userUsersVO);
        //获取卡券数量
        Integer cardNum = cardUserService.queryCardNum(userUsers.getId(), merchantCode);
        userUsersVO.setCardNum(cardNum);
        userUsersVO.setPoint(0);
        BigDecimal money = cardMapUserClientService.queryUserMoney(userUsers.getId()).getData();
        userUsersVO.setMoney(money);
        userUsersVO.setIsVip(true);
        userUsersVO.setVipType(mrcMapMerchantPrimes.getType());
        return userUsersVO;
    }

    /**
     * 用户绑定实体储值卡
     *
     * @param icCardId
     * @param userId
     * @param merchantCode
     */
    @Override
    public void userBindActualCard(String icCardId, String userId, String merchantCode) {
        //进行校验  卡号是否已和用户进行过绑定
        CardMapUserCards cardMapUserCards = cardMapUserClientService.queryByIcCardIdAndUserId(icCardId, Long.parseLong(userId)).getData();
        if (cardMapUserCards != null) {
            throw new CheckException(ResultTypeEnum.CARD_BIND);
        }
        cardMapUserClientService.createCardMapUserCards(icCardId, userId, merchantCode);
    }


    /**
     * 用户余额查询
     *
     * @param queryAccountMoneyData
     * @return
     */
    @Override
    public BigDecimal queryAccountMoney(QueryAccountMoneyData queryAccountMoneyData) {
        if (!StringUtils.isEmpty(queryAccountMoneyData.getIcCardId())) {
            CardMapUserCards cardMapUserCards = cardMapUserClientService.queryByIcCardId(queryAccountMoneyData.getIcCardId()).getData();
            BigDecimal userMoney = cardMapUserClientService.queryUserMoney(cardMapUserCards.getUserId()).getData();
            return userMoney;
        }
        if (!StringUtils.isEmpty(queryAccountMoneyData.getTel())) {
            UserUsers userUsers = merchantPrimeService.primeQueryUserByTelAndCode(queryAccountMoneyData.getTel(), queryAccountMoneyData.getMerchantCode());
            BigDecimal bigDecimal = cardMapUserClientService.queryUserMoney(userUsers.getId()).getData();
            return bigDecimal;
        }
        if (!StringUtils.isEmpty(queryAccountMoneyData.getUserFlagCode())) {
            String userFlagCode = queryAccountMoneyData.getUserFlagCode();
            String userOpenid = RequestQrCodeDataStrUtil.subStringQrCodeData(userFlagCode);
            UserUsers userUsers = queryUserByOpenid(userOpenid);
            BigDecimal bigDecimal = cardMapUserClientService.queryUserMoney(userUsers.getId()).getData();
            return bigDecimal;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public void updatePasswordByOpenid(String newPassword, String phoneNum, String openid) {
        authClientService.updatePasswordByOpenid(newPassword, openid);
    }

    @Override
    public void saveMember(List<MemberImportVo> list, String merchantCode, String memberType) {
        list.forEach(e -> {
            saveUser(e, merchantCode, memberType);
            PpcsCloudCardOpenResponse ppcsCloudCardOpenResponse = this.getPpcsCloudCardOpenResponse(e.getTel());
            Assert.notNull(ppcsCloudCardOpenResponse, "开卡失败");
            if ("0".equals(ppcsCloudCardOpenResponse.getResult())) {
                UserUsers userUsers = authClientService.queryByOpenid(e.getOpenId()).getData();
                cardMapUserClientService.createCardMapUserCards(TongLianCardState.CARD_CODE.getCode() + "", userUsers.getId(), merchantCode,
                        TongLianCardState.CARD_NAME.getDesc(),
                        TongLianCardState.CATEGORY.getCode() + "",
                        TongLianCardState.CATEGORY.getDesc(),
                        TongLianCardState.STATE_NORMAL.getDesc(),
                        TongLianCardState.TYPE.getDesc(),
                        ppcsCloudCardOpenResponse.getCard_id());
            }
        });
    }


    /**
     * 用户账户余额增加
     *
     * @param userId
     * @param amount
     */
    @Override
    public void userAccountMoneyAdd(Long userId, Integer amount) {
        CardMapUserCards cardMapUserCards = cardMapUserClientService.queryByUserIdAndAccount(userId, UserCardsTypeConfig.ACCOUNT).getData();
        try {
            CardMoneyAddUtil.cardMoneyAdd(cardMapUserCards.getCardNo(), amount);
        } catch (IOException e) {
            logger.info("调取通联余额增加接口" + userId + "失败" + e);
            e.printStackTrace();
        }
    }


    /**
     * 计算卡券应用 和  扣除余额 之后的钱
     *
     * @param cardMapUserCardsVO
     * @param amount
     * @param userId
     * @param userMoneyBD
     * @return
     */
    @Override
    public RetSettlementUserMoneyData settlementUserMoney(CardMapUserCardsVO cardMapUserCardsVO, Integer
            amount, Long userId, BigDecimal userMoneyBD) {
        Integer paymoney = amount;
        RetSettlementUserMoneyData retSettlementUserMoneyData = new RetSettlementUserMoneyData();
        if (cardMapUserCardsVO != null && !StringUtils.isEmpty(cardMapUserCardsVO.getCardFaceValue())) {
            if ("discount".equals(cardMapUserCardsVO.getCardCardsType())) {
                Integer cardCouponMoney = discountTypeMoney(cardMapUserCardsVO.getCardFaceValue(), amount);
                paymoney = amount - cardCouponMoney;
                if (paymoney <= 0) {
                    retSettlementUserMoneyData.setCardDiscountMoney(cardCouponMoney);
                    retSettlementUserMoneyData.setAmount(0);
                    return retSettlementUserMoneyData;
                }
                retSettlementUserMoneyData.setCardDiscountMoney(cardCouponMoney);
            } else {
                int parseIntCardValue = Integer.parseInt(cardMapUserCardsVO.getCardFaceValue());
                paymoney = amount - parseIntCardValue;
                if (paymoney <= 0) {
                    retSettlementUserMoneyData.setCardDiscountMoney(parseIntCardValue);
                    retSettlementUserMoneyData.setAmount(0);
                    return retSettlementUserMoneyData;
                }
                retSettlementUserMoneyData.setCardDiscountMoney(parseIntCardValue);
            }
        }
//        BigDecimal accountMoney = cardMapUserClientService.queryUserMoney(userId).getData();
        if (userMoneyBD.compareTo(BigDecimal.ZERO) == 0) {
            retSettlementUserMoneyData.setAmount(paymoney);
            return retSettlementUserMoneyData;
        } else {
            int anInt = Integer.parseInt(userMoneyBD.toString());
            Integer needPayMoney = paymoney - anInt;
            if (needPayMoney <= 0) {
                retSettlementUserMoneyData.setAmount(0);
                return retSettlementUserMoneyData;
            } else {
                retSettlementUserMoneyData.setAmount(needPayMoney);
                return retSettlementUserMoneyData;
            }
        }
    }


    /**
     * 折扣券 计算 折扣金额
     *
     * @param cardFaceValue
     * @param amount
     * @return
     */
    @Override
    public Integer discountTypeMoney(String cardFaceValue, Integer amount) {
        int discount = Integer.parseInt(cardFaceValue);
        Double d = (100 - discount) * 0.01;
        Double v = amount * d;
        Integer cardCouponMoney = v.intValue();
        return cardCouponMoney;
    }

    @Override
    public UserUsers queryUserByOpenid(String openid) {
        Result result = authClientService.queryByOpenid(openid);
        String string = JSONObject.toJSONString(result.getData());
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
        return userUsers;
    }

    /**
     * 用户核算金额  直接结算
     *
     * @param cardNoList
     * @param amount
     * @param userId
     * @return
     */
    @Override
    public RetAccountPayData settlementUserMoneyEnd(List<PosSelectCardNo> cardNoList, Integer amount, Long userId,String orderCode) {
        RetAccountPayData retAccountPayData = new RetAccountPayData();
        Integer cardCouponMoney = 0;
        String orderMerchantCode = "";
        Map<String, Integer> couponMoneyMap = new HashMap<>();
        if (cardNoList != null && cardNoList.size() > 0) {
            for (PosSelectCardNo posSelectCardNO : cardNoList) {
                CardMapUserCards cardMapUserCards = cardMapUserClientService.getByCardNo(posSelectCardNO.getCardNo()).getData();
                orderMerchantCode = cardMapUserCards.getMerchantCode();
                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.queryByCardCodeAndMerchantCodeBatchCode(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getBatchCode()).getData();
                if (cardMapMerchantCards != null) {
                    if ("discount".equals(cardMapMerchantCards.getCardType())) {
                        cardCouponMoney = discountTypeMoney(cardMapMerchantCards.getCardFaceValue(), amount);
                        couponMoneyMap.put(posSelectCardNO.getCardNo(),cardCouponMoney);
                    } else {
                        int parseIntCardValue = Integer.parseInt(cardMapMerchantCards.getCardFaceValue());
                        couponMoneyMap.put(posSelectCardNO.getCardNo(),parseIntCardValue);
                        cardCouponMoney = cardCouponMoney + parseIntCardValue;
                    }
                } else {
                    CardCards cardCards = cardCardsClientService.getCard(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getBatchCode()).getData();
                    if ("discount".equals(cardCards.getType())) {
                        cardCouponMoney = discountTypeMoney(cardCards.getFaceValue().toString(), amount);
                        couponMoneyMap.put(posSelectCardNO.getCardNo(),cardCouponMoney);
                    } else {
                        int parseIntCardValue = cardCards.getFaceValue();
                        couponMoneyMap.put(posSelectCardNO.getCardNo(),parseIntCardValue);
                        cardCouponMoney = cardCouponMoney + parseIntCardValue;
                    }
                }
            }
        }
        Integer needPayMoney = amount - cardCouponMoney;
        BigDecimal userMoney = cardMapUserClientService.queryUserMoney(userId).getData();
        int userMoneyInt = Integer.parseInt(userMoney.toString());
        CardMapUserCards cardMapUserCards = cardMapUserClientService.queryByUserIdAndAccount(userId, UserCardsTypeConfig.ACCOUNT).getData();
        if (StringUtils.isEmpty(orderMerchantCode)) {
            orderMerchantCode = cardMapUserCards.getMerchantCode();
        }

        if(StringUtils.isEmpty(orderCode)){
            orderCode = IdWorker.getIdStr();
        }else {
            CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
            if (CardOrdersTypeConfig.POS_MIS_ORDER.equals(cardOrdersVO.getType())){
                orderCode = "mis" + orderCode;
            }
        }

        retAccountPayData.setOrderCode(orderCode);

        CardPayDetailData cardPayDetailData = new CardPayDetailData();
        cardPayDetailData.setCardNoList(cardNoList);
        cardPayDetailData.setCouponMoneyMap(couponMoneyMap);

        if (needPayMoney > 0) {
            if (needPayMoney >= userMoneyInt) {
                try {
                    if (userMoneyInt>0) {
                        CardMoneyPayUtil.CardMoneyPay(userMoneyInt + "", cardMapUserCards.getCardNo(), orderCode);
                        // 记录支付数据 流水 订单  订单明细
                        cardOrderClientService.accountPayCreateOrderAndDetailAndTrace(userId, amount, cardCouponMoney, userMoneyInt, orderMerchantCode, orderCode, cardMapUserCards.getCardNo(), cardPayDetailData);
                    }
                } catch (IOException e) {
                    logger.info("通联卡号:" + cardMapUserCards.getCardNo() + ",账户卡券支付失败" + e.getMessage());
                    retAccountPayData.setAmount((userMoneyInt - needPayMoney) * -1);
                    return retAccountPayData;
                }
            } else {
                try {
                    if (userMoneyInt>0) {
                        CardMoneyPayUtil.CardMoneyPay(needPayMoney.toString(), cardMapUserCards.getCardNo(), orderCode);
                        // 记录支付数据 流水 订单  订单明细
                        cardOrderClientService.accountPayCreateOrderAndDetailAndTrace(userId, amount, cardCouponMoney, needPayMoney, orderMerchantCode, orderCode, cardMapUserCards.getCardNo(), cardPayDetailData);
                    }
                } catch (IOException e) {
                    logger.info("通联卡号:" + cardMapUserCards.getCardCode() + ",账户卡券支付失败" + e.getMessage());
                    retAccountPayData.setAmount(userMoneyInt - needPayMoney);
                    return retAccountPayData;
                }
            }
        }
        //修改卡券状态
        if (cardNoList != null && cardNoList.size() > 0) {
            CardPayDetailData cardPayDetailDataCoupon = new CardPayDetailData();
            cardPayDetailDataCoupon.setCardNoList(cardNoList);
            cardPayDetailDataCoupon.setCouponMoneyMap(couponMoneyMap);
            cardMapUserClientService.updateUserCardsState(cardNoList, UserCardsStateConfig.USED);
            cardOrderPayTraceClientService.createCouponCardPayTrace(userId,orderMerchantCode,orderCode,cardPayDetailDataCoupon);
        }
        if (userMoneyInt - needPayMoney > 0) {
            retAccountPayData.setAmount(0);
            return retAccountPayData;
        } else {
            retAccountPayData.setAmount((userMoneyInt - needPayMoney) * -1);
            return retAccountPayData;
        }
    }

    /**
     * 计算用户 需支付的金额  不直接结算
     *
     * @param cardNoList
     * @param amount
     * @param userId
     * @return
     */
    @Override
    public RetCalculationData calculationAmount(List<PosSelectCardNo> cardNoList, Integer amount, Long userId) {
        Integer cardCouponMoney = 0;
        RetCalculationData retCalculationData = new RetCalculationData();
        if (cardNoList != null && cardNoList.size() > 0) {
            for (PosSelectCardNo posSelectCardNO : cardNoList) {
                CardMapUserCards cardMapUserCards = cardMapUserClientService.getByCardNo(posSelectCardNO.getCardNo()).getData();
                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.queryByCardCodeAndMerchantCodeBatchCode(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getBatchCode()).getData();
                if (cardMapMerchantCards != null) {
                    if ("discount".equals(cardMapMerchantCards.getCardType())) {
                        cardCouponMoney = discountTypeMoney(cardMapMerchantCards.getCardFaceValue(), amount);
                        retCalculationData.setCardDiscountMoney(cardCouponMoney);
                    } else {
                        int parseIntCardValue = Integer.parseInt(cardMapMerchantCards.getCardFaceValue());
                        cardCouponMoney = cardCouponMoney + parseIntCardValue;
                        retCalculationData.setCardDiscountMoney(cardCouponMoney);
                    }
                } else {
                    CardCards cardCards = cardCardsClientService.getCard(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getBatchCode()).getData();
                    if ("discount".equals(cardCards.getType())) {
                        cardCouponMoney = discountTypeMoney(cardCards.getFaceValue().toString(), amount);
                        retCalculationData.setCardDiscountMoney(cardCouponMoney);
                    } else {
                        int parseIntCardValue = cardCards.getFaceValue();
                        cardCouponMoney = cardCouponMoney + parseIntCardValue;
                        retCalculationData.setCardDiscountMoney(cardCouponMoney);
                    }
                }
            }
        } else {
            retCalculationData.setCardDiscountMoney(0);
        }
        Integer needPayMoney = amount - cardCouponMoney;
        BigDecimal userMoney = cardMapUserClientService.queryUserMoney(userId).getData();
        int userMoneyInt = Integer.parseInt(userMoney.toString());
        retCalculationData.setOriUserMoneyInt(userMoneyInt);
        if (userMoneyInt - needPayMoney > 0) {
            retCalculationData.setAmount(0);
            return retCalculationData;
        } else {
            retCalculationData.setAmount((userMoneyInt - needPayMoney) * -1);
            return retCalculationData;
        }
    }

    @Override
    public UserUsers getUserByPhone(String phone,String merchantCode) {
        UserUsers userUsers = merchantPrimeService.primeQueryUserByTelAndCode(phone, merchantCode);
        return userUsers;
    }


    @Transactional(rollbackFor = RuntimeException.class)
    public void saveUser(MemberImportVo memberVo, String merchantCode, String memberType) {
        UserUsers userUsers = this.getUserByPhone(memberVo.getTel(),merchantCode);
        if (userUsers == null) {
            userUsers = new UserUsers();
        }
        BeanUtils.copyProperties(memberVo, userUsers);
        userUsers.setIdCardType("车牌号");
        userUsers.setIdCardNum(memberVo.getPlateNumbers());
        userUsers.setAppCode(AppConstant.TONGLIAN_APP_CODE);
        userUsers.setAppName(AppConstant.TONGLIAN_APP_NAME);
        userUsers.setId(authClientService.saveOrUpdateUser(userUsers).getData());
        System.out.println("用户id:**********************************" + userUsers.getId());
        saveMerchantPrimeInfo(memberVo, merchantCode, memberType, userUsers.getId());
        saveMemberCardInfo(merchantCode, userUsers.getId(), memberVo.getCardCode());
    }

    private void saveMemberCardInfo(String merchantCode, Long id, String memberCard) {
        CardMapUserCards cardMapUserCards = new CardMapUserCards();
        cardMapUserCards.setUserId(id);
        cardMapUserCards.setMerchantCode(merchantCode);
        cardMapUserCards.setIcCardId(memberCard);
        cardMapUserCards.setType("memberCard");
        cardMapUserCards.setState("正常");
        cardMapUserClientService.saveOrUpdate(cardMapUserCards);
    }

    private void saveMerchantPrimeInfo(MemberImportVo memberVo, String merchantCode, String memberType, Long userId) {
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByUserIdAndMerchantCode(userId, merchantCode).getData();
        if (mrcMapMerchantPrimes == null) {
            mrcMapMerchantPrimes = new MrcMapMerchantPrimes();
            Integer perPaymentLimit = Integer.parseInt(merchantsConfigClientService.getConfigByKey(merchantCode, "PER_PAYMENT_LIMIT").getData());
            Integer dailyPaymentLimit = Integer.parseInt(merchantsConfigClientService.getConfigByKey(merchantCode, "DAILY_PAYMENT_LIMIT").getData());
            mrcMapMerchantPrimes.setDailyPaymentLimit(dailyPaymentLimit);
            mrcMapMerchantPrimes.setPerPaymentLimit(perPaymentLimit);
        }
        mrcMapMerchantPrimes.setUserId(userId);
        mrcMapMerchantPrimes.setType(memberType);
        mrcMapMerchantPrimes.setTel(memberVo.getTel());
        mrcMapMerchantPrimes.setMerchantCode(merchantCode);
        mrcMapMerchantPrimes.setState("有效");
        mrcMapMerchantPrimes.setEx1("推荐人ID：" + String.valueOf(memberVo.getRecommendPersonId()));
        mrcMapMerchantPrimes.setEx2("推荐人姓名：" + memberVo.getRecommendPersonName());
        mrcMapMerchantPrimes.setEx3("推荐商户ID：" + String.valueOf(memberVo.getRecommendMerchantId()));
        mrcMapMerchantPrimes.setEx4("推荐商户名" + memberVo.getRecommendMerchantName());
        mapMerchantPrimesClientService.saveOrUpdate(mrcMapMerchantPrimes);
    }

    /**
     * 获取用户openid
     *
     * @param code
     * @return
     */
    public WXData getOpenid(String code, String appId, String appSecret) {
        String content = "";
        String openId = "";
        String unionId = "";
        String accessToken = "";
        String errmsg = "";
        String errcode = "";
        String refreshToken = "";
        String expiresIn = "";
        //封装获取openId的微信API
        StringBuffer url = new StringBuffer();
        url.append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=")
                .append(appId)
                .append("&secret=")
                .append(appSecret)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpClient httpClient = new HttpClient(url.toString());
            httpClient.get();
            content = httpClient.getContent();
            Map map = objectMapper.readValue(content, Map.class);
            openId = String.valueOf(map.get("openid"));
            accessToken = String.valueOf(map.get("access_token"));
            refreshToken = String.valueOf(map.get("refresh_token"));
            unionId = String.valueOf(map.get("unionid"));
            errcode =  map.get("errcode")+"";
            errmsg =  map.get("errmsg")+"";
            expiresIn = map.get("expires_in")+"";
            logger.info("获取的openID：" + openId);
        } catch (JsonParseException e) {
            logger.error("json解析失败：", e);
        } catch (JsonMappingException e) {
            logger.error("map转换成json失败：", e);
        } catch (Exception e) {
            logger.error("http获取openId请求失败：", e);
        }
        WXData wxData = new WXData();
        logger.info("openid============" +openId);
        wxData.setOpenid(openId);
        logger.info("accessToken============" +accessToken);
        wxData.setAccessToken(accessToken);
        logger.info("refreshToken============" +refreshToken);
        wxData.setRefreshToken(refreshToken);
        logger.info("errcode============" +errcode);
        wxData.setErrCode(errcode);
        logger.info("errmsg============" +errmsg);
        wxData.setErrMsg(errmsg);
        wxData.setExpiresIn(expiresIn);
        return wxData;
    }

    /**
     * 获取微信用户信息
     *
     * @param openId
     * @return
     */
    public WXUser getWechatUserInfo(String openId, String accessToken) {
        logger.info("token>>" + accessToken);
        //构造获取用户基本信息api
        StringBuffer url = new StringBuffer();
        url.append("https://api.weixin.qq.com/sns/userinfo?")
                .append("access_token=").append(accessToken)
                .append("&openid=").append(openId);
        String content = "";
        ObjectMapper objectMapper = new ObjectMapper();
        WXUser wxUser = new WXUser();
        try {
            HttpClient httpClient = new HttpClient(url.toString());
            httpClient.setHttps(true);
            httpClient.get();
            content = httpClient.getContent();
            logger.info("获取到的微信用户数据" + content);
            logger.info("获取微信用户请求响应信息:>>" + content);
            Map map = objectMapper.readValue(content, Map.class);
            logger.info("获取到的微信用户数据" + map);
            Object mopenId = map.get("openid");
            Object nickName = map.get("nickname");
            if (openId.equals(mopenId) && nickName != null) {
                /*
                 * 获取微信用户基本信息成功，并将信息封装到平台用户对象中。
                 */
                wxUser.setNickname(String.valueOf(nickName));
                wxUser.setSex((Integer) map.get("sex"));
                wxUser.setPictureURL(String.valueOf(map.get("headimgurl")));
                wxUser.setOpenid(String.valueOf(mopenId));
                wxUser.setUnionID(String.valueOf(map.get("unionid")));
                logger.info("调用微信得到的用户信息:>>" + wxUser.getNickname() + ",photo>>" + wxUser.getPictureURL());
                return wxUser;
            }
            wxUser.setErrMsg( map.get("errcode")+"");
            wxUser.setErrCode( map.get("errmsg")+"");
            logger.info("获取openId=" + openId + "的微信用户信息失败!!");
        } catch (JsonParseException e) {
            logger.error("获取微信基本用户信息时,json转换失败：>>", e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("http请求执行错误:>>", e);
            e.printStackTrace();
        }
        return wxUser;
    }

    /**
     * 获取微信用户 access_token
     *
     * @return
     */
    public WXData getAccessToken(String refreshToken, String appId) {
        StringBuffer url = new StringBuffer();
        url.append("https://api.weixin.qq.com/sns/oauth2/refresh_token?")
                .append("appid=").append(appId)
                .append("&grant_type=").append("refresh_token")
                .append("&refresh_token=").append(refreshToken);
        logger.info("获取全局accesss_token的请求:>>" + url.toString());
        WXData wxData = new WXData();
        try {
            String content;
            ObjectMapper objectMapper = new ObjectMapper();
            HttpClient httpClient = new HttpClient(url.toString());
            httpClient.setHttps(true);
            httpClient.get();
            content = httpClient.getContent();
            System.out.println(content);
            try {
                Map map = objectMapper.readValue(content, Map.class);
                Object at = map.get("access_token");
                logger.info("获取全局access_token结果为:>>" + at);
                if (null != at) {
                    logger.info("获取access_token成功!!");
                    wxData.setAccessToken(String.valueOf(at));
                    wxData.setOpenid(map.get("openid")+"");
                    wxData.setErrCode(map.get("errcode")+"");
                    wxData.setErrMsg(map.get("errmsg")+"");
                    wxData.setRefreshToken(map.get("refresh_token")+"");
                    wxData.setExpiresIn(map.get("expires_in")+"");
                    return wxData;
                }
            } catch (Exception e) {
                logger.error("获取全局access_token时,json转换失败：" + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("获取全局access_token失败：" + e.getMessage());
        }
        wxData.setErrCode("10112");
        wxData.setErrMsg("access_token error");
        return wxData;
    }

}
