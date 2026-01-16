package com.ht.user.card.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.card.vo.CardMapUserCardsVO;
import com.ht.user.card.vo.PosSelectCardNo;
import com.ht.user.card.vo.PosUserCardVO;
import com.ht.user.mall.entity.OrderOrderDetails;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 用户，卡绑定关系 服务类
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
public interface CardMapUserCardsService extends IService<CardMapUserCards> {

    /**
     * 查询用户与卡的关联信息
     *
     * @param cardCode
     * @param userId
     * @return
     */
    CardMapUserCards queryByCardCodeAndUserId(String cardCode, Long userId);

    /**
     * 创建卡与用户的关联信息
     * @param cardCode
     * @param userId
     * @param merchantCode
     * @param cardName
     * @param categoryCode
     * @param categoryName
     * @param state
     * @param type
     * @param cardNo
     * @param actionType
     * @param traceState
     * @param faceValue
     * @param batchCode
     * @param cardType
     */
    void createCardMapUserCards(String cardCode,
                                Long userId,
                                String merchantCode,
                                String cardName,
                                String categoryCode,
                                String categoryName,
                                String state,
                                String type,
                                String cardNo,
                                String actionType,
                                String traceState,
                                String faceValue,
                                String batchCode, String cardType);


    /**
     * 发放card
     * @param merchantCode
     * @param cardCode
     * @param userId
     * @param cardNo
     * @param batchCode
     * @return 创建结果
     */
    boolean createCardMapUserCards(String merchantCode, String cardCode, Long userId, String cardNo, String batchCode);


    /**
     * 根据userId 查询用户下的卡
     *
     * @param userId
     * @return
     */
    List<CardMapUserCardsVO> queryByUserId(long userId);



    /**
     * 根据userId 查询用户钱包数据
     *
     * @return
     * @Param userId
     */
    BigDecimal queryUserMoney(Long userId);

    /**
     * 根据user_id获取用户在商户下的卡券 列表
     *
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    List<CardMapUserCards>  queryByUserIdAndMerchantCode(Long userId, String merchantCode, String state);

    /**
     * 根据手机号获取在商户下的虚拟卡列表
     *
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    List<CardMapUserCards> queryByUserIdAndMerchantCodeNotIc(Long userId, String merchantCode, String state);

    /**
     * 根据实体卡号 和用户id 查询绑定关系
     * @param icCardId
     * @param userId
     * @return
     */
    CardMapUserCards queryByIcCardIdAndUserId(String icCardId, Long userId);

    /**
     * 用户 根据 cardNo 绑定 实体卡卡号
     * @param cardNo
     * @param userId
     * @param icCardId
     */
    void bindIcCardId(String cardNo, Long userId, String icCardId);

    /**
     * 用户绑定实体储值卡
     * @param icCardId
     * @param userId
     * @param merchantCode
     */
    void createCardMapUserCards(String icCardId, String userId, String merchantCode);

    /**
     * 根据实体卡卡号 和 商户编码获取 用户信息
     * @param icCardId
     * @param merchantCode
     * @return
     */
    CardMapUserCards queryByIcCardIdAndMerchantCode(String icCardId,String merchantCode);

    /**
     * 更新用户卡号
     * @param userId 用户id
     * @param code 卡号
     * @param merchantCode 商户号
     */
    void updateUserCode(Long userId, String code,String  merchantCode);

    /**
     * 用户免费领取卡券
     * @param userId
     * @param cardCode
     * @param merchantCode
     * @param batchCode
     */
    Boolean userGetCard(Long userId, String cardCode, String merchantCode, String batchCode);

    /**
     * 根据实体卡号查询用户与卡关联
     * @param icCardId
     * @return
     */
    CardMapUserCards queryByIcCardId(String icCardId);

    /**
     * 通过 userId
     * @param userId
     * @param type
     */
    CardMapUserCards queryByUserIdAndAccount(Long userId, String type);


    /**
     * 计算出 优惠力度最大的卡券
     * @param cardMapUserCardsVOList
     * @param amount
     */
    CardMapUserCardsVO settlementCardMoney(List<CardMapUserCardsVO> cardMapUserCardsVOList, Integer amount);

    /**
     * 封装pos端需要的卡券信息
     * @param cardMapUserCardsVO
     * @return
     */
    PosUserCardVO packagePosUserCardVO(CardMapUserCardsVO cardMapUserCardsVO);

    /**
     * 封装pos端需要的卡券列表信息
     * @param cardMapUserCardsVOList
     * @return
     */
    List<PosUserCardVO> packagePosUserCardVOList(List<CardMapUserCardsVO> cardMapUserCardsVOList);

    /**
     * 根据卡编号 查询卡券信息
     * @param cardNo
     * @return
     */
    CardMapUserCards queryByCardNo(String cardNo);

    /**
     * 修改卡券状态
     * @param cardNoList
     * @param state
     */
    void updateUserCardsState(List<PosSelectCardNo> cardNoList, String state);

    /**
     * 根据 cardCode生成二维码图片
     * @param cardNo
     * @return
     */
    String createQrCode(String cardNo);

    /**
     * 获取某个用户某个类型的某个卡券的总数
     * @param userId
     * @param batchCode
     * @param cardCode
     * @param date
     * @return
     */
    Integer getUserCardAmount(Long userId, String batchCode, String cardCode, String date);

    /**
     * pos领券
     * @param merchantCode
     * @param cardCode
     * @param userId
     * @param batchCode
     * @return
     */
    boolean posSendCard(String merchantCode, String cardCode, Long userId, String batchCode);

    /**
     * 根据cardNo 修改卡状态
     * @param cardNo
     * @param state
     */
    void updateStateByCardNo(String cardNo,String state);

    /**
     * 计算扣除后的用户余额
     * @param list
     * @param userMoney
     * @param amount
     * @return
     */
    Integer afterUserAccount(List<CardMapUserCardsVO> list, int userMoney, Integer amount);

    /**
     * 计算扣除后的用户余额
     * @param list
     * @param userMoney
     * @param amount
     * @return
     */
    Integer afterUserAccount(List<PosSelectCardNo> list, int userMoney, Integer amount,Boolean flag);


    /**
     * 获取用户虚拟卡券(不包含计次券)
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    List<CardMapUserCards> getCardPayList(long userId, String merchantCode, String state);

    /**
     *
     * @param merchantCode
     * @param userId
     * @return
     */
    CardMapUserCards getUserVipCard(String merchantCode, Long userId);


    /**
     * 获取用户的计次卡券列表
     * @param userId
     * @param merchantCode
     * @return
     */
    List<CardMapUserCardsVO> selectUserNumberList(String userId, String merchantCode);

    /**
     * 核销用户 计次卡券
     * @param cardNo
     * @return
     */
    Integer qrUseNumberCard(String cardNo);

    /**
     * 获取用户的可用卡券列表
     * @param userId
     * @param merchantCode
     * @param type
     * @param state
     * @param objMerchantCode
     * @return
     */
    List<CardMapUserCards> posCanUseCard(Long userId, String merchantCode, String type, String state,String objMerchantCode);

    /**
     * 获取用户在商户下的卡券数量
     * @param userId
     * @param merchantCodeList
     * @param type
     * @param state
     * @return
     */
    Integer selectCount(Long userId, List<String> merchantCodeList, String type, String state);

    /**
     * 创建用户 开通通联卡 关联数据  账户余额数据
     * @param cardCode
     * @param userId
     * @param objectMerchantCode
     * @param cardName
     * @param categoryCode
     * @param categoryName
     * @param state
     * @param type
     * @param cardId
     */
    void createCardMapUserCards(String cardCode, Long userId, String objectMerchantCode, String cardName, String categoryCode, String categoryName, String state, String type, String cardId);


    /**
     * 商城 创建商品用户关系 和流水
     * @param cardMapUserCards
     */
    void saveCardMapUserAndTrace(CardMapUserCards cardMapUserCards);

    /**
     * 商城用户 免费领券
     * @param mallUserGetCardData
     * @return
     */
    Boolean mallUserGetCard(MallUserGetCardData mallUserGetCardData);

    /**
     * 查询用户 商城卡券 包括购买 和 领取
     * @param queryMyCardData
     * @return
     */
    Page<CardMapUserCards> mallQueryUserCard(QueryMyCardData queryMyCardData);

    /**
     * 获取用户在一系列商户下的所有卡券
     * @param userId
     * @param merchantCodes
     * @param state
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    IPage<CardMapUserCards> getUserCardInMerchants(Long userId, List<String> merchantCodes, String state, Integer pageNo, Integer pageSize, String type);

    /**
     * 查询用户商城购买的卡券
     * @param userId
     * @param state
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<CardMapUserCards> queryUserBuyCardList(Long userId, String state, String type , Integer pageNo, Integer pageSize);

    /**
     *  计算折扣券 折扣的金额
     * @param amount
     * @param cardMapUserCards
     * @return
     */
    int calculateDiscountMoney(Integer amount, CardMapUserCards cardMapUserCards);

    /**
     * 查询用户在主体下的余额
     * @return
     */
    CardMapUserCards queryByUserIdAndAccountAndMerchantCode(Long userId,String merchantCode);

    /**
     * 满赠活动
     * @return 发放结果
     * @param merchantCode
     * @param amount
     * @param userId
     */
    boolean sendCardForFulfilQuota(String merchantCode, Integer amount, Long userId);

    /**
     * 锁定用户卡券
     * @param orderOrderDetailsList
     */
    void updateRefundState(List<OrderOrderDetails> orderOrderDetailsList);

    /**
     * 根据 refKey 和状态 查询指定条目
     * @param refKey
     * @param state
     * @param quantity
     * @return
     */
    List<CardMapUserCards> queryByRefKeyAndStateLimit(Long refKey, String state, int quantity);

    /**
     * 根据refKey和订单状态
     * @param refKey
     * @param state
     * @return
     */
    Integer queryCountByRefKeyAndState(Long refKey, String state);

    Boolean removeUserCardForRefund(Long userId, String productionCode, String refKey);
}
