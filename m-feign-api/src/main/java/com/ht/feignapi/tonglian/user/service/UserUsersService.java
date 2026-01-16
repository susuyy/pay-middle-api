package com.ht.feignapi.tonglian.user.service;

import com.aliyuncs.exceptions.ClientException;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.tonglian.admin.excel.entity.MemberImportVo;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCardsVO;

import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.user.entity.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Service
public interface UserUsersService{

    WXData getOpenid(String code, String wxAppId, String wxAppSecret);

    /**
     * 添加会员信息
     * @param cardCode
     * @param gender
     * @param password
     * @param phoneNum
     * @param realName
     * @param merchantCode
     * @param birthday
     */
    void add(String cardCode, String gender, String password, String phoneNum, String realName, String merchantCode, String birthday);

    /**
     * 发送手机验证码
     *
     * @param phoneNum
     * @param code
     * @return
     */
    void sendCode(String phoneNum, String code);

    /**
     * 发送用户下单付款短信通知
     * @param phoneNum
     */
    void sendOrderMsg(String phoneNum) throws ClientException;

    /**
     * 发送用户退款申请短信通知
     * @param phoneNum
     * @param count
     */
    void sendRefundMsg(String phoneNum,Integer count) throws ClientException;

    /**
     * 用户密码修改
     *
     * @param newPassword
     * @param phoneNum
     */
    void updatePassword(String newPassword, String phoneNum);

    /**
     * 后台重置用户密码
     * @param newPassword
     * @param userId
     */
    void resetPassword(String newPassword, Long userId);

    /**
     * 用户会员卡绑定
     * @param userBindCardData
     * @return
     */
    void userBindCard(UserBindCardData userBindCardData);

    /**
     * 通过用户账号查询用于
     *
     * @param account 用户账号
     * @return 用户实例
     */
    UserUsers getUserByAccount(String account);

    /**
     * 获取openid和用户信息
     *
     * @param code
     * @param merchantCode
     * @return
     */
    UserUsersVO getOpenidAndUserMsg(String code, String merchantCode);

    /**
     * C端公众号 用户绑定手机号 注册为商户会员
     *
     * @param phoneNum
     * @param openid
     * @param merchantCode
     */
    void bindPhoneNum(String phoneNum, String openid, String merchantCode);

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
    void updateUserMsg(String openid, String realName, String gender, String birthday, String marriage, String job, String idCardType, String idCardNum);

    /**
     * 修改用户手机号
     *  @param openid
     * @param tel
     * @param merchantCode
     */
    void updateTelByOpenid(String openid, String tel, String merchantCode);

    /**
     * 根据openid查询数据库用户信息
     *
     * @param openid
     * @param merchantCode
     * @return
     */
    UserUsersVO queryUserByOpenid(String openid, String merchantCode);

    /**
     * 用户绑定实体储值卡
     * @param icCardId
     * @param userId
     * @param merchantCode
     */
    void userBindActualCard(String icCardId, String userId, String merchantCode);

    /**
     * 用户余额查询
     * @param queryAccountMoneyData
     * @return
     */
    BigDecimal queryAccountMoney(QueryAccountMoneyData queryAccountMoneyData);

    /**
     * 根据openid修改密码
     * @param newPassword
     * @param phoneNum
     * @param openid
     */
    void updatePasswordByOpenid(String newPassword, String phoneNum, String openid);

    /**
     * 保存导入的会员数据
     * @param list 导入list
     * @param merchantCode
     * @param memberType
     */
    void saveMember(List<MemberImportVo> list, String merchantCode, String memberType);



    /**
     * 用户账户余额 增加
     * @param userId
     * @param amount
     */
    void userAccountMoneyAdd(Long userId, Integer amount);

//    /**
//     * 通过手机号/openId获取用户信息
//     * @param phone
//     * @param merchantCode
//     * @return
//     */
//    UserUsers getUserByPhoneOrOpenId(String phone, String merchantCode);

    /**
     * 核算钱
     * @param cardMapUserCardsVO
     * @param amount
     * @param userId
     * @param userMoneyBD
     * @return
     */
    RetSettlementUserMoneyData settlementUserMoney(CardMapUserCardsVO cardMapUserCardsVO, Integer amount, Long userId, BigDecimal userMoneyBD);

    /**
     * 折扣券 获取折扣 金额
     * @param cardFaceValue
     * @param amount
     * @return
     */
    Integer discountTypeMoney(String cardFaceValue, Integer amount);

    /**
     * 根据openid查询用户信息
     * @param openid
     * @return
     */
    UserUsers queryUserByOpenid(String openid);

    /**
     *  支付用户余额
     * @param cardNoList
     * @param amount
     * @param userId
     * @return
     */
    RetAccountPayData settlementUserMoneyEnd(List<PosSelectCardNo> cardNoList, Integer amount, Long userId,String orderCode);

    /**
     * 计算用户支付金额
     * @param cardNoList
     * @param amount
     * @param userId
     * @return
     */
    RetCalculationData calculationAmount(List<PosSelectCardNo> cardNoList, Integer amount, Long userId);

    /**
     * 根据手机号查询用户
     * @param phone
     * @param merchantCode
     * @return
     */
    UserUsers getUserByPhone(String phone,String merchantCode);


    /**
     * 获取用户 微信用户 数据
     * @param openid
     * @param accessToken
     * @return
     */
    WXUser getWechatUserInfo(String openid, String accessToken);

    /**
     * 获取 AccessToken
     * @param refreshToken
     * @param wxAppId
     * @return
     */
    WXData getAccessToken(String refreshToken, String wxAppId);
}
