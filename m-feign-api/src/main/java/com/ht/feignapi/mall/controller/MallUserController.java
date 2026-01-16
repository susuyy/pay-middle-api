package com.ht.feignapi.mall.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.MallAppShowClientService;
import com.ht.feignapi.mall.constant.MerchantCardConstant;
import com.ht.feignapi.mall.entity.BindTelData;
import com.ht.feignapi.mall.entity.MallUserGetCardData;
import com.ht.feignapi.mall.entity.QueryMyCardData;
import com.ht.feignapi.mall.service.InventoryService;
import com.ht.feignapi.mall.service.MallUserService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.card.service.CardLimitsService;
import com.ht.feignapi.tonglian.card.service.CardMapMerchantCardService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.user.entity.UserUsersVO;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.tongshangyun.service.MemberService;
import com.ht.feignapi.util.KtMsg;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/mall/user")
@CrossOrigin(allowCredentials = "true")
public class MallUserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MallUserService mallUserService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private CardLimitsService cardLimitsService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private CardMapMerchantCardService cardMapMerchantCardService;

    @Autowired
    private CardMapMerchantCardClientService merchantCardClientService;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberService memberService;



    /**
     * C端商城  前端提交code码获取openid和用户信息(登录接口)
     *
     * @param code
     * @param merchantCode
     * @return
     */
    @GetMapping("/getOpenidAndUserInfo")
    public UserUsersVO getOpenid(@RequestParam("code") String code, @RequestParam("merchantCode") String merchantCode) {
        logger.info("code=====================" + code);
        logger.info("merchantCode===================" + merchantCode);
        if (StringUtils.isEmpty(merchantCode)){
            throw new CheckException(ResultTypeEnum.MERCHANT_ERROR);
        }
        if (StringUtils.isEmpty(code)){
            throw new CheckException(ResultTypeEnum.OPENID_ERROR);
        }
        UserUsersVO userUsersVO = mallUserService.getOpenidAndUserMsg(code, merchantCode);
        return userUsersVO;
    }


    /**
     * C端商城  根据openid 查询用户信息
     *
     * @param openid
     * @param merchantCode
     * @return
     */
    @GetMapping("/getUserInfo")
    public UserUsersVO getUserByOpenid(@RequestParam("openid") String openid, @RequestParam("merchantCode") String merchantCode) {
        logger.info("openid========="+openid);
        logger.info("merchantCode =============="+merchantCode);
        if (StringUtils.isEmpty(openid)){
            throw new CheckException(ResultTypeEnum.OPENID_ERROR);
        }
        if (StringUtils.isEmpty(merchantCode)){
            throw new CheckException(ResultTypeEnum.MERCHANT_ERROR);
        }
        UserUsersVO usrUsersVO = mallUserService.queryUserByOpenid(openid, merchantCode);
        return usrUsersVO;
    }

    /**
     * 商城 用户领券 (免费领券)
     *
     * @param mallUserGetCardData
     * @return
     */
    @PostMapping("/userGetCard")
    public String userGetCard(@RequestBody MallUserGetCardData mallUserGetCardData) {
        UserUsers userUsers = authClientService.queryByOpenid(mallUserGetCardData.getOpenid()).getData();
        mallUserGetCardData.setUserId(userUsers.getId());
        Result<CardMapMerchantCards> cardMapMerchantCards = merchantCardClientService.getMerchantCard(mallUserGetCardData.getMerchantCode(),mallUserGetCardData.getCardCode());
        Assert.notNull(cardMapMerchantCards,"获取merchantCard失败");
        Assert.notNull(cardMapMerchantCards.getData(),"获取merchantCard失败");
        cardLimitsService.checkCardGetLimit(mallUserGetCardData.getCardCode(), mallUserGetCardData.getMerchantCode(), mallUserGetCardData.getUserId(), cardMapMerchantCards.getData().getBatchCode());
        cardMapUserClientService.mallUserGetCard(mallUserGetCardData).getData();
        return "领取成功";
    }

    /**
     * 查询用户卡列表
     * @param queryMyCardData
     * @return
     */
    @PostMapping("/userMallFreeCards")
    public IPage<CardMapUserCards> getUserMallFreeCards(@RequestBody QueryMyCardData queryMyCardData){
        return mallUserService.getCardMapUserCardsPage(queryMyCardData);
    }



    /**
     * 商城 获取用户 卡券列表
     *
     * @param queryMyCardData
     * @return
     */
    @GetMapping("/getMyCardList")
    public Page<UserCardVO> getUserCardList(@RequestBody QueryMyCardData queryMyCardData) throws ParseException {
        UserUsers usrUsers = authClientService.queryByOpenid(queryMyCardData.getOpenId()).getData();
        List<UserCardVO> userCardVOList = new ArrayList<>();
        if (usrUsers == null) {
            Page<UserCardVO> userCardVOPage = new Page<>();
            userCardVOPage.setRecords(userCardVOList);
            userCardVOPage.setTotal(0);
            userCardVOPage.setSize(0);
            userCardVOPage.setCurrent(1);
            return userCardVOPage;
        }
        List<Merchants> merchantAndSonList = merchantsClientService.getSubMerchants(queryMyCardData.getMerchantCode()).getData();
        Merchants objMerchant = merchantsClientService.getMerchantByCode(queryMyCardData.getMerchantCode()).getData();
        merchantAndSonList.add(objMerchant);
        queryMyCardData.setMerchantsList(merchantAndSonList);
        Page<CardMapUserCards> cardMapUserCardsPage = cardMapUserClientService.mallQueryUserCard(queryMyCardData).getData();
        Page<UserCardVO> userCardVOPage = new Page<>();
        List<CardMapUserCards> records = cardMapUserCardsPage.getRecords();
        for (CardMapUserCards cardMapUserCards : records) {
            CardCards cardCards = cardMapMerchantCardService.queryByCardCode(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode());
            UserCardVO userCardVO = new UserCardVO();
            BeanUtils.copyProperties(cardCards, userCardVO);
            userCardVO.setCardNo(cardMapUserCards.getCardNo());
            userCardVO.setIcCardId(cardMapUserCards.getIcCardId());
            userCardVO.setType(cardMapUserCards.getType());
            userCardVO.setNotice(cardCards.getNotice());
            userCardVO.setCardName(cardMapUserCards.getCardName());
            userCardVO.setCardCardsType(cardCards.getType());
            userCardVO.setCardPicUrl(cardCards.getCardPicUrl());
            CardMapMerchantCards cardMapMerchantCards = merchantCardClientService.queryByCardCodeAndMerchantCodeBatchCode(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getBatchCode()).getData();
            String showTimeScope;
            if (cardMapMerchantCards!=null) {
                showTimeScope = cardMapMerchantCardService.getShowTimeScope(cardMapMerchantCards);
            }else {
                showTimeScope = "免费赠送";
            }
            userCardVO.setShowTimeScope(showTimeScope);
            userCardVOList.add(userCardVO);
        }
        userCardVOPage.setRecords(userCardVOList);
        userCardVOPage.setTotal(cardMapUserCardsPage.getTotal());
        userCardVOPage.setSize(cardMapUserCardsPage.getSize());
        userCardVOPage.setCurrent(cardMapUserCardsPage.getCurrent());
        userCardVOPage.setSearchCount(cardMapUserCardsPage.isSearchCount());
        userCardVOPage.setOptimizeCountSql(cardMapUserCardsPage.optimizeCountSql());
        return userCardVOPage;
    }


    /**
     *  根据手机号发送验证码接口
     *
     * @param phoneNum
     * @return
     */
    @GetMapping("/sendCode")
    public void sendCode(@RequestParam("phoneNum") String phoneNum) {
        //生成验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        stringRedisTemplate.opsForValue().set(phoneNum + "&authCode",code,5, TimeUnit.MINUTES);
        KtMsg.sendMsg(phoneNum, code);
    }

    /**
     *  通商云 根据手机号发送验证码接口
     *
     * @param phoneNum
     * @return
     */
    @GetMapping("/tsySendCode")
    public void sendVerificationCode(@RequestParam("phoneNum") String phoneNum,@RequestParam("userId")String userId,
                                     @RequestParam("merchantCode")String merchantCode) {
        memberService.sendVerificationCode(phoneNum,userId,merchantCode);
    }


    /**
     *  用户信息绑定手机
     * @param bindTelData
     * @return
     */
    @PostMapping("/bindTel")
    public void bindPhoneNum(@RequestBody BindTelData bindTelData) {
        String authCode = stringRedisTemplate.opsForValue().get(bindTelData.getPhoneNum() + "&authCode");
        System.out.println(authCode);
        System.out.println(bindTelData.getAuthCode());
        if (StringUtils.isEmpty(authCode) || !authCode.equals(bindTelData.getAuthCode())) {
            throw new CheckException(ResultTypeEnum.AUTH_CODE_ERROR);
        }
        UserUsers userUsers = authClientService.queryByOpenid(bindTelData.getOpenid()).getData();
        userUsers.setTel(bindTelData.getPhoneNum());
        authClientService.updateUserTL(userUsers);
    }

    /**
     *  通商云 用户信息绑定手机
     * @param bindTelData
     * @return
     */
    @PostMapping("/tsyBindTel")
    public void tsyBindPhoneNum(@RequestBody BindTelData bindTelData) {
        UserUsers userUsers = authClientService.queryByOpenid(bindTelData.getOpenid()).getData();
        memberService.bindPhoneNum(userUsers.getId(),bindTelData.getPhoneNum(),bindTelData.getMerchantCode(),bindTelData.getAuthCode());
        userUsers.setTel(bindTelData.getPhoneNum());
        authClientService.updateUserTL(userUsers);
    }


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
