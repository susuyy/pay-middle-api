package com.ht.feignapi.prime.service;


import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.RetServiceData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.result.OpenIdException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.user.entity.UserUsersVO;
import com.ht.feignapi.tonglian.user.entity.WXData;
import com.ht.feignapi.tonglian.user.entity.WXUser;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
public class PrimeUserService {

    private Logger logger = LoggerFactory.getLogger(PrimeUserService.class);

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private AuthClientService authClientService;


    /**
     * 根据 code 获取openid 和用户数据
     * @param code
     * @return
     */
    public UserUsersVO getOpenidAndUserMsg(String code) {
        String wxAppId = "wx7bc28f9b428822cc";
        String wxAppSecret = "4c9d069c4f2544bf720cc444c6826cf9";
        WXData wxData = userUsersService.getOpenid(code, wxAppId, wxAppSecret);
        logger.info("获取到的微信数据为========================================="+wxData);
        if (StringUtils.isEmpty(wxData.getOpenid()) || "null".equals(wxData.getOpenid())) {
            throw new OpenIdException(ResultTypeEnum.OPENID_ERROR.getCode(), "获取openid失败");
        }
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
            userUsers.setAppCode(AppConstant.MS_APP_CODE);
            userUsers.setAppName(AppConstant.MS_APP_NAME);
            RetServiceData retServiceData = authClientService.register(userUsers).getData();
            UserUsers registerUser = retServiceData.getData();
            return userUsersVO;
        }
        UserUsersVO userUsersVO = new UserUsersVO();
        BeanUtils.copyProperties(usrUsers, userUsersVO);
        //获取卡券数量
        Integer cardNum =0;
        try {
            cardNum = msPrimeClient.queryCardNum(usrUsers.getId(),wxData.getOpenid()).getData();
        } catch (Exception e) {
            logger.info(e.getMessage());
            cardNum=0;
        }
        if (cardNum==null){
            cardNum=0;
        }
        userUsersVO.setCardNum(cardNum);
        userUsersVO.setPoint(0);
        userUsersVO.setIsVip(true);
        userUsersVO.setVipType("黄金会员");
        return userUsersVO;
    }

    /**
     * 根据openid 查询用户数据
     * @param openid
     * @return
     */
    public UserUsersVO queryUserByOpenid(String openid) {
        Result result = authClientService.queryByOpenid(openid);
        String string = JSONObject.toJSONString(result.getData());
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
        UserUsersVO userUsersVO = new UserUsersVO();
        BeanUtils.copyProperties(userUsers, userUsersVO);
        //获取卡券数量
        Integer cardNum =0;
        try {
            cardNum = msPrimeClient.queryCardNum(userUsers.getId(), openid).getData();
        } catch (Exception e) {
            logger.info(e.getMessage());
            cardNum=0;
        }
        if (cardNum==null){
            cardNum=0;
        }
        userUsersVO.setCardNum(cardNum);
        userUsersVO.setPoint(0);
        userUsersVO.setIsVip(true);
        userUsersVO.setVipType("黄金会员");
        return userUsersVO;
    }

    /**
     * 免税用户绑定手机 同时开卡
     * @param phoneNum
     * @param openid
     * @param userUsers
     */
    public void bindPhoneNum(String phoneNum, String openid, UserUsers userUsers) {
        userUsers.setTel(phoneNum);
        authClientService.updateUserTL(userUsers);
        msPrimeClient.bindTelAndOpenCard(phoneNum,openid,userUsers.getId());
    }


}
