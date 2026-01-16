package com.ht.feignapi.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.appshow.entity.MallCoupon;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.RetServiceData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.MallAppShowClientService;
import com.ht.feignapi.mall.entity.QueryMyCardData;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.OpenIdException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.card.service.CardUserService;
import com.ht.feignapi.tonglian.config.CardConstant;
import com.ht.feignapi.tonglian.config.UserDefaultHeadPicUrl;
import com.ht.feignapi.tonglian.config.WeChatConfig;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tonglian.merchant.service.MerchantPrimeService;
import com.ht.feignapi.tonglian.user.entity.UserUsersVO;
import com.ht.feignapi.tonglian.user.entity.WXData;
import com.ht.feignapi.tonglian.user.entity.WXUser;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.tonglian.utils.TimeUtil;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MallUserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private MapMerchantPrimesClientService mapMerchantPrimesClientService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MallAppShowClientService appShowClientService;

    @Autowired
    private MerchantPrimeService merchantPrimeService;

    /**
     * 根据 code 获取openid 和用户数据
     * @param code
     * @param merchantCode
     * @return
     */
    public UserUsersVO getOpenidAndUserMsg(String code, String merchantCode) {

        logger.info("上送的merchantCode========================="+merchantCode);
        if (StringUtils.isEmpty(merchantCode)){
            throw new CheckException(ResultTypeEnum.MERCHANT_ERROR);
        }

        String wxAppId = merchantsConfigClientService.getConfigByKey(merchantCode, "WX_APPID").getData();
        String wxAppSecret = merchantsConfigClientService.getConfigByKey(merchantCode, "WX_APPSECRET").getData();
        WXData wxData = userUsersService.getOpenid(code, wxAppId, wxAppSecret);
        logger.info("获取到的微信数据为========================================="+wxData);
        if (StringUtils.isEmpty(wxData.getOpenid()) || "null".equals(wxData.getOpenid())) {
            throw new OpenIdException(ResultTypeEnum.OPENID_ERROR.getCode(), "获取openid失败");
        }
        stringRedisTemplate.opsForValue().set(WeChatConfig.ACCESS_TOKEN +merchantCode,wxData.getAccessToken(),9000, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(WeChatConfig.REFRESH_TOKEN +merchantCode,wxData.getRefreshToken(),25,TimeUnit.DAYS);
        Result result = authClientService.queryByOpenid(wxData.getOpenid());
        String string = JSONObject.toJSONString(result.getData());
        UserUsers usrUsers = JSONObject.parseObject(string, UserUsers.class);
        if (usrUsers == null) {
            WXUser wechatUserInfo = userUsersService.getWechatUserInfo(wxData.getOpenid(), wxData.getAccessToken());
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
            UserUsers userUsers = new UserUsers();
            BeanUtils.copyProperties(userUsersVO, userUsers);
            userUsers.setAppCode(AppConstant.MALL_APP_CODE);
            userUsers.setAppName(AppConstant.MALL_APP_NAME);
            RetServiceData retServiceData = authClientService.register(userUsers).getData();
            UserUsers registerUser = retServiceData.getData();
            mapMerchantPrimesClientService.add(registerUser.getId(),merchantCode,"normal","黄金会员",registerUser.getOpenId());
//            merchantPrimeService.addAndRegisterTsyMember(registerUser.getId(),merchantCode,"normal","黄金会员",registerUser.getOpenId());
            return userUsersVO;
        }
        UserUsersVO userUsersVO = new UserUsersVO();
        BeanUtils.copyProperties(usrUsers, userUsersVO);
        //获取卡券数量
        Integer cardNum =0;
        try {
            cardNum = cardUserService.queryCardNum(usrUsers.getId(), merchantCode);
        } catch (Exception e) {
            logger.info(e.getMessage());
            cardNum=0;
        }
        if (cardNum==null){
            cardNum=0;
        }
        userUsersVO.setCardNum(cardNum);

        MrcMapMerchantPrimes myPrimes = getMyPrimes(usrUsers.getId(), merchantCode);
        if (myPrimes!=null){
            if (myPrimes.getPrimePoints()==null){
                userUsersVO.setPoint(0);
            }else {
                userUsersVO.setPoint(myPrimes.getPrimePoints());
            }
            userUsersVO.setVipType(myPrimes.getType());
            userUsersVO.setIsVip(true);
        }else {
            userUsersVO.setPoint(0);
            userUsersVO.setVipType("非会员用户");
            userUsersVO.setIsVip(false);
        }
        return userUsersVO;
    }

    /**
     * 根据openid 查询用户数据
     * @param openid
     * @param merchantCode
     * @return
     */
    public UserUsersVO queryUserByOpenid(String openid, String merchantCode) {
        Result result = authClientService.queryByOpenid(openid);
        String string = JSONObject.toJSONString(result.getData());
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
        UserUsersVO userUsersVO = new UserUsersVO();
        if (userUsers == null) {
            try {
                String wxAppId = merchantsConfigClientService.getConfigByKey(merchantCode, "WX_APPID").getData();
                String accessToken = "";
                String redisAccessToken = stringRedisTemplate.opsForValue().get(WeChatConfig.ACCESS_TOKEN + merchantCode);
                if (StringUtils.isEmpty(redisAccessToken)){
                    String refreshToken = stringRedisTemplate.opsForValue().get(WeChatConfig.REFRESH_TOKEN + merchantCode);
                    WXData wxData = userUsersService.getAccessToken(refreshToken, wxAppId);
                    accessToken = wxData.getAccessToken();
                    stringRedisTemplate.opsForValue().set(WeChatConfig.ACCESS_TOKEN +merchantCode,wxData.getAccessToken(),9000, TimeUnit.SECONDS);
                    stringRedisTemplate.opsForValue().set(WeChatConfig.REFRESH_TOKEN +merchantCode,wxData.getRefreshToken(),25,TimeUnit.DAYS);
                }
                WXUser wechatUserInfo = userUsersService.getWechatUserInfo(openid, accessToken);
                userUsersVO.setNickName(wechatUserInfo.getNickname());
                userUsersVO.setHeadPicUrl(wechatUserInfo.getPictureURL());
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.getMessage());
                userUsersVO.setNickName("用户"+openid);
                userUsersVO.setHeadPicUrl(UserDefaultHeadPicUrl.defaultHeadPic);
            }
            userUsersVO.setMoney(BigDecimal.ZERO);
            userUsersVO.setPoint(0);
            userUsersVO.setCardNum(0);
            userUsersVO.setVipType("非会员用户");
            userUsersVO.setIsVip(false);
            UserUsers userUsersSave = new UserUsers();
            BeanUtils.copyProperties(userUsersVO, userUsersSave);
            userUsersSave.setAppCode(AppConstant.MALL_APP_CODE);
            userUsersSave.setAppName(AppConstant.MALL_APP_NAME);
            userUsersSave.setOpenId(openid);
            RetServiceData retServiceData = authClientService.register(userUsersSave).getData();
            UserUsers registerUser = retServiceData.getData();
            mapMerchantPrimesClientService.add(registerUser.getId(),merchantCode,"normal","黄金会员",openid);
//            merchantPrimeService.addAndRegisterTsyMember(registerUser.getId(),merchantCode,"normal","黄金会员",registerUser.getOpenId());
            return userUsersVO;
        }

        BeanUtils.copyProperties(userUsers, userUsersVO);
        //获取卡券数量
        Integer cardNum =0;
        try {
            cardNum = cardUserService.queryCardNum(userUsers.getId(), merchantCode);
        } catch (Exception e) {
            logger.info(e.getMessage());
            cardNum=0;
        }
        if (cardNum==null){
            cardNum=0;
        }
        userUsersVO.setCardNum(cardNum);

        MrcMapMerchantPrimes myPrimes = getMyPrimes(userUsers.getId(), merchantCode);
        if (myPrimes!=null){
            if (myPrimes.getPrimePoints()==null){
                userUsersVO.setPoint(0);
            }else {
                userUsersVO.setPoint(myPrimes.getPrimePoints());
            }
            userUsersVO.setVipType(myPrimes.getType());
            userUsersVO.setIsVip(true);
        }else {
            userUsersVO.setPoint(0);
            userUsersVO.setVipType("非会员用户");
            userUsersVO.setIsVip(false);
        }
        return userUsersVO;
    }

    /**
     * 获取用户会员信息
     * @param userId
     * @param merchantCode
     * @return
     */
    public MrcMapMerchantPrimes getMyPrimes(Long userId,String merchantCode){
        return mapMerchantPrimesClientService.queryByUserIdAndMerchantCode(userId, merchantCode).getData();
    }

    public IPage<CardMapUserCards> getCardMapUserCardsPage(@RequestBody QueryMyCardData queryMyCardData) {
        UserUsers usrUsers = authClientService.queryByOpenid(queryMyCardData.getOpenId()).getData();
        if (usrUsers==null){
            return new Page<>();
        }
        List<Merchants> merchantsList = merchantsClientService.getSubMerchants(queryMyCardData.getMerchantCode()).getData();
        Assert.isTrue(!CollectionUtils.isEmpty(merchantsList),"非法商户号");
        //从card_map_user_cards表中找到属于这个用户的所有cardCode
        //关联到card_map_merchant_cards表，找到type为：mall-free的卡券code返回
        if (queryMyCardData.getState().equals("all")) {
            queryMyCardData.setState("");
        }
        Result<Page<CardMapUserCards>> result = cardMapUserClientService.getUserCardInMerchants(usrUsers.getId(),
                merchantsList.stream().map(Merchants::getMerchantCode).collect(Collectors.toList()),
                queryMyCardData.getState(), queryMyCardData.getPageNo() , queryMyCardData.getPageSize());
        Assert.notNull(result,"feign请求出错");
        result.getData().getRecords().forEach(e->{
            if(!e.getValidityType().equals(CardConstant.BEGIN_TO_END)){
                Date beginTime = TimeUtil.addHours(e.getCreateAt(),e.getValidGapAfterApplied());
                e.setValidFrom(beginTime);
                e.setValidTo(TimeUtil.addHours(beginTime,e.getPeriodOfValidity()));
            }
            Result<MallCoupon> mallCouponResult = appShowClientService.getCouponByCardCode(e.getCardCode(),e.getMerchantCode());
            Assert.notNull(mallCouponResult,"feign获取appshow-coupon出错");
            Assert.notNull(mallCouponResult.getData(),"卡号对应卡券为空");
            e.setMallCoupon(mallCouponResult.getData());
        });
        return result.getData();
    }
}
