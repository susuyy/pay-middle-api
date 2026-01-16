package com.ht.user.card.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.admin.vo.*;
import com.ht.user.card.entity.CardCards;
import com.ht.user.card.entity.CardMapMerchantCards;
import com.baomidou.mybatisplus.extension.service.IService;
import java.text.ParseException;
import java.util.List;

/**
 * <p>
 * 商家卡券 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
public interface CardMapMerchantCardsService extends IService<CardMapMerchantCards> {

    /**
     * 根据卡号查询商家卡券
     *
     * @param cardCode
     * @param batchCode
     * @return
     */
    CardMapMerchantCards getCardMsgByCardCode(String cardCode, String batchCode);

    /**
     * 根据商家编号 列表分类 查询商家 上架的  卡券列表
     *
     * @param merchantCode
     * @param type
     * @return
     */
    List<CardMapMerchantCards> queryListByMerchantCode(String merchantCode, String type) throws ParseException;

    /**
     * 根据卡号和商家编码查询关联信息
     *
     * @param cardCode
     * @param merchantCode
     * @return
     */
    CardMapMerchantCards queryByCardCodeAndMerchantCode(String cardCode, String merchantCode);

    /**
     * 根据卡号和商家编码和批次号查询关联信息
     *
     * @param cardCode
     * @param merchantCode
     * @return
     */
    CardMapMerchantCards queryByCardCodeAndMerchantCodeBatchCode(String cardCode, String merchantCode,String batchCode);

    /**
     * 通过商户号，获取商家所有基础卡
     * @param merchantCode 商户号
     * @param page
     * @param codeSearch
     * @return
     */
    List<CardListVo> getCardsByMerchantCode(String merchantCode, IPage<CardListVo> page, CodeSearch codeSearch);

    /**
     * 通过商户号，获取商家所有发布的卡券商品
     * @param merchantCode 商户号
     * @param search
     * @param page
     * @return
     */
    List<MerchantCardListVo> getCardProductsByMerchantCode(String merchantCode, MerchantCardSearch search, IPage<MerchantCardListVo> page);

    /**
     * 保存商户会员信息
     * @param card 会员信息类
     */
    void createMerchantCards(CardEditVo card);

    /**
     * 通过商户号和卡号获取卡券信息
     * @param merchantCode
     * @param cardCode
     * @return
     */
    CardMapMerchantCards getCard(String merchantCode, String cardCode);

    void createUserFreeCards(UserFreeCard userFreeCard,String merchantCode);

    /**
     * 获取商户下所有用户领取卡券列表
     * @param merchantCode
     * @param page
     * @return
     */
    List<MerchantUserCardVo> getUserCardList(String merchantCode, IPage<MerchantUserCardVo> page);

    /**
     * 获取商户下所有的pos卡券
     * @param merchantCode
     * @param cardName
     * @return
     */
    List<CardMapMerchantCards> getPosCardList(String merchantCode,String cardName);


    /**
     * 创建不同批次的卡券
     * @param cards MerchantCardEditVo
     * @param merchantCode
     * @param cardCode
     * @return
     */
    boolean createBatchMerchantCard(MerchantCardEditVo cards,String merchantCode,String cardCode);

    /**
     * 获取前端展示时间
     * @param cardMapMerchantCards
     * @return
     */
    String getShowTimeScope(CardMapMerchantCards cardMapMerchantCards) throws ParseException;

    /**
     * 获取cardCards其余数据
     * @param cardMapMerchantCards
     * @return
     */
    CardCards getShowOtherData(CardMapMerchantCards cardMapMerchantCards);


    /**
     * 获取cardCode对应所有的商户code
     * @param cardCode
     * @return
     */
    List<String> getCardMerchants(String cardCode);

    /**
     * 获取主体下所有的卡券
     * @param merchantCodes
     * @param merchantCardSearch
     * @param page
     * @return
     */
    List<MerchantCardListVo> getObjectAndSonMerchantCards(List<String> merchantCodes, MerchantCardSearch merchantCardSearch, IPage<MerchantCardListVo> page);

    /**
     * 获取卡券使用时间
     * @param cardCards
     * @return
     */
    String packageValidTimeStr(CardCards cardCards);

    /**
     * 获取支付具体商户卡
     * @param cardCode
     * @param batchCode
     * @return
     */
    CardMapMerchantCards queryByCardCodeAndBatchCode(String cardCode, String batchCode);

    /**
     * 商城 查询卡券商品
     * @param cardCode
     * @param storeMerchantCode 子商户的商户号
     * @param type
     * @return
     */
    CardMapMerchantCards mallQueryCodeMerchantCodeType(String cardCode, String storeMerchantCode, String type);

    /**
     * 获取在cardCodes里，类型为type的卡券列表
     * @param cardCodes
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<CardMapMerchantCards> getUserCardInCardCodes(List<String> cardCodes, String type, Integer pageNo, Integer pageSize);

    /**
     * 获取商城售卖卡券
     * @param merchantCodes
     * @param pageNo
     * @param pageSize
     * @param productionName
     * @param productionCode
     * @param onSaleState
     * @return
     */
    IPage<CardMapMerchantCards> getMallSellCard(List<String> merchantCodes, Long pageNo, Long pageSize, String productionName, String productionCode, String onSaleState);

    /**
     * 通过商户号和卡号获取商户卡券
     * @param merchantCode
     * @param cardCode
     * @return
     */
    CardMapMerchantCards getMerchantCard(String merchantCode, String cardCode);

    /**
     * 获取商户商城下的卡券列表
     * @param merchantCodes
     * @param state
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    IPage<CardMapMerchantCards> getCardProductionPage(List<String> merchantCodes, String state, Long pageNo, Long pageSize, String type);

    /**
     * 保存商城端免费优惠券
     * @param card
     * @param merchantCode
     */
    void saveMallCoupon(CardCards card, String merchantCode);

    @Override
    boolean save(CardMapMerchantCards cardMapMerchantCards);

    /**
     * 保存活动的卡券
     * @param cards
     * @param merchantCode
     * @param activityCode
     */
    void createActivityCard(CardCards cards, String merchantCode, String activityCode);

    /**
     * 获取活动类型的卡券
     * @param cardCode
     * @param merchantCode
     * @return
     */
    CardMapMerchantCards getActivityCard(String cardCode, String merchantCode);
}
