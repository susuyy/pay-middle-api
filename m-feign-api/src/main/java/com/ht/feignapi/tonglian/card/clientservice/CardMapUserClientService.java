package com.ht.feignapi.tonglian.card.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.mall.entity.MallUserGetCardData;
import com.ht.feignapi.mall.entity.OrderOrderDetails;
import com.ht.feignapi.mall.entity.QueryMyCardData;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.user.entity.PosSelectCardNo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/13 14:25
 */
@FeignClient(name = "${custom.client.user.name}",contextId = "cardMapUser")
public interface CardMapUserClientService {
    /**
     * 获取会员卡信息
     * @param merchantCode
     * @param userId
     * @return
     */
    @GetMapping("/user-card/{merchantCode}/vipCard/{userId}")
    Result<CardMapUserCards> getUserVipCard(@PathVariable("merchantCode") String merchantCode,@PathVariable("userId") Long userId);

    /**
     * 保存商户用户会员信息
     * @param userCard
     */
    @PostMapping("/user-card/")
    void saveOrUpdate(@RequestBody CardMapUserCards userCard);

    /**
     * 查询用户余额
     * @param userId
     * @return
     */
    @GetMapping("/user-card/money/{userId}")
    Result<BigDecimal> queryUserMoney(@PathVariable("userId") Long userId);

    /**
     * 创建通联账户卡 与用户绑定关联关系(xxxxxxxxxx  CardMapUserCardsService类方法同名 )
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
    @PostMapping("/user-card/cardMapUser")
    void createCardMapUserCards(@RequestParam("cardCode") String cardCode,
                                @RequestParam("userId")Long userId,
                                @RequestParam("objectMerchantCode")String objectMerchantCode,
                                @RequestParam("cardName")String cardName,
                                @RequestParam("categoryCode")String categoryCode,
                                @RequestParam("categoryName")String categoryName,
                                @RequestParam("state")String state,
                                @RequestParam("type")String type,
                                @RequestParam("cardId")String cardId);

    /**
     * 用户绑定实体储值卡(xxxxxxxxx CardMapUserCardsService类方法同名 )
     * @param icCardId
     * @param userId
     * @param merchantCode
     */
    @PostMapping("/user-card/realCard")
    void createCardMapUserCards(@RequestParam("icCardId") String icCardId,
                                @RequestParam("userId") String userId,
                                @RequestParam("merchantCode") String merchantCode);

    /**
     * 发放card
     * @param merchantCode
     * @param cardCode
     * @param userId
     * @param cardNo
     * @param batchCode
     * @return 创建结果
     */
    @PostMapping("/user-card/sendCard")
    Result<Boolean> createCardMapUserCards(@RequestParam("merchantCode") String merchantCode,
                                   @RequestParam("cardCode") String cardCode,
                                   @RequestParam("userId") Long userId,
                                   @RequestParam("cardNo") String cardNo,
                                   @RequestParam("batchCode") String batchCode);

    /**
     * 查询用户与卡的关联关系(xxxxxxxx CardMapUserCardsService类方法同名 )
     * @param icCardId
     * @param userId
     * @return
     */
    @GetMapping("/user-card/icCard")
    Result<CardMapUserCards> queryByIcCardIdAndUserId(@RequestParam("icCardId") String icCardId,@RequestParam("userId") Long userId);

    /**
     * 用户实体卡绑定(xxxxxxxxxx  CardMapUserCardsService类方法同名 )
     * @param cardNo
     * @param userId
     * @param icCardId
     */
    @PostMapping("/user-card/bindIcCard")
    void bindIcCardId(@RequestParam("cardNo") String cardNo,@RequestParam("userId") Long userId,@RequestParam("icCardId") String icCardId);

    /**
     * 根据实体卡卡号查询 关联关系(xxxxxx  CardMapUserCardsService类方法同名)
     * @param icCardId
     * @return
     */
    @GetMapping("/user-card/cardUserInfo/{icCardId}")
    Result<CardMapUserCards> queryByIcCardId(@PathVariable("icCardId") String icCardId);

    /**
     * 查询用户账户 对应的通联卡号关系(xxxxxx  CardMapUserCardsService类方法同名)
     * @param userId
     * @param type
     * @return
     */
    @GetMapping("/user-card/cardUserTlInfo/{userId}/{type}")
    Result<CardMapUserCards> queryByUserIdAndAccount(@PathVariable("userId") Long userId,@PathVariable("type") String type);

    /**
     * 根据卡 cardNo 查询关系(xxxxxx  CardMapUserCardsService类方法同名)
     * @param cardNo
     * @return
     */
    @GetMapping("/user-card/cardNoCard/{cardNo}")
    Result<CardMapUserCards> getByCardNo(@PathVariable("cardNo") String cardNo);

    /**
     *  更新用户卡状态(xxxxxx  CardMapUserCardsService类方法同名)
     * @param cardNoList
     * @param state
     */
    @PostMapping("/user-card/cardState/{state}")
    void updateUserCardsState(@RequestBody List<PosSelectCardNo> cardNoList, @PathVariable("state") String state);

    /**
     * pos发券
     * @param merchantCode
     * @param cardCode
     * @param userId
     * @param batchCode
     * @return
     */
    @GetMapping("/user-card/pos/sendCard")
    Result<Boolean> posSendCard(@RequestParam("merchantCode") String merchantCode,
                        @RequestParam("cardCode") String cardCode,
                        @RequestParam("userId") Long userId,
                        @RequestParam("batchCode") String batchCode);

    /**
     * 通过userId，商户号，获取用户拥有的卡券列表
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @GetMapping("/user-card/userAllCard/{merchantCode}/{userId}/{state}")
    Result<List<CardMapUserCards>> getByUserIdAndMerchantCode(@PathVariable("userId") Long userId,
                                                      @PathVariable("merchantCode") String merchantCode,
                                                      @PathVariable("state") String state);

    /**
     * 获取主体下，用户的卡券
     * @param userId
     * @param merchantCodes
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/user-card/objectUserCard/{merchantCodes}/{userId}")
    Result<Page<CardMapUserCards>> getUserCardInMerchants(@PathVariable("userId") Long userId,
                                                              @PathVariable("merchantCodes") List<String> merchantCodes,
                                                              @RequestParam("state") String state,
                                                              @RequestParam("pageNo") Integer pageNo,
                                                              @RequestParam("pageSize") Integer pageSize);

    /**
     * 根据用户id 和商户编码 查询用户卡列表 (已绑定实体卡)
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @GetMapping("/user-card/userNotIcCard/{merchantCode}/{userId}/{state}")
    Result<List<CardMapUserCards>> getByUserIdAndMerchantCodeNotIc(@PathVariable("userId") Long userId,
                                                           @PathVariable("merchantCode") String merchantCode,
                                                           @PathVariable("state") String state);

    /**
     * 通过实体卡号获取用户卡
     * @param icCard
     * @return
     */
    @GetMapping("/user-card/icCard/{icCard}")
    Result<CardMapUserCards> getByIcCard(@PathVariable("icCard") String icCard);


    /**
     * 封装pos 端需要的卡券列表信息
     * @param cardMapUserCardsVOList
     * @return
     */
    @GetMapping("/user-card/posUserCardVos")
    Result<List<PosUserCardVO>> packagePosUserCardVOList(@RequestBody List<CardMapUserCardsVO> cardMapUserCardsVOList);

    /**
     * 计算对用户 优惠力度最大的 卡券
     * @param cardMapUserCardsVOList
     * @param amount
     * @return
     */
    @GetMapping("/user-card/cardMapUserCardsVo/{amount}")
    Result<CardMapUserCardsVO> settlementCardMoney(@RequestBody List<CardMapUserCardsVO> cardMapUserCardsVOList,@PathVariable("amount") Integer amount);

    /**
     * 封装 pos 端需要的卡券信息
     * @param cardMapUserCardsVO
     * @return
     */
    @GetMapping("/user-card/posUserCardVo")
    Result<PosUserCardVO> packagePosUserCardVO(@RequestBody CardMapUserCardsVO cardMapUserCardsVO);

    /**
     * 封装 计算后的余额返回
     * @param list
     * @param userAccountMoney 账户余额
     * @param settlementAmount 金额
     */
    @GetMapping("/user-card/userAccountMoney/{userAccountMoney}/{settlementAmount}")
    Result<Integer> afterUserAccount(@RequestBody List<CardMapUserCardsVO> list,
                             @PathVariable("userAccountMoney") Integer userAccountMoney,
                             @PathVariable("settlementAmount") Integer settlementAmount);

    /**
     * 计算扣除后的用户余额
     * @param list
     * @param userAccountMoney
     * @param settlementAmount
     * @return
     */
    @GetMapping("/user-card/userAccountMoneyAfterPaid/{userAccountMoney}/{settlementAmount}/{flag}")
    Result<Integer> afterUserAccount(@RequestBody List<PosSelectCardNo> list,
                             @PathVariable("userAccountMoney") Integer userAccountMoney,
                             @PathVariable("settlementAmount") Integer settlementAmount,
                             @PathVariable("flag") Boolean flag);

    /**
     * c扫B 核销用户 计次券
     * @param cardMapUserCards
     */
    @PostMapping("/user-card/qrUseNumberCard")
    Result<Integer> qrUseNumberCard(@RequestBody CardMapUserCards cardMapUserCards);

    /**
     * 用户免费领券
     * @param userGetCardData
     * @return
     */
    @PostMapping("/user-card/userGetCard")
    Result<Boolean> userGetCard(@RequestBody UserGetCardData userGetCardData);



    /**
     * 获取用户卡券数目
     * @param userId
     * @param batchCode
     * @param cardCode
     * @param date
     * @return
     */
    @GetMapping("/user-card/userCardAmount")
    Result<Integer> getUserCardAmount(
            @RequestParam("userId") Long userId,
            @RequestParam("batchCode") String batchCode,
            @RequestParam("cardCode") String cardCode,
            @RequestParam("date") String date);

    /**
     * 获取用户 计次卡券列表
     * @param userId
     * @param merchantCode
     * @return
     */
    @GetMapping("/user-card/userNumberCardList")
    Result<List<CardMapUserCardsVO>> selectUserNumberList(@RequestParam("userId") Long userId, @RequestParam("merchantCode") String merchantCode);

    /**
     *  获取用户可用的虚拟卡券
     * @param userId
     * @param merchantCode
     * @param type
     * @param state
     * @return
     */
    @GetMapping("/user-card/posCanUseCard")
    Result<List<CardMapUserCards>> posCanUseCard(@RequestParam("userId") Long userId,
                                                 @RequestParam("merchantCode")String merchantCode,
                                                 @RequestParam("type")String type,
                                                 @RequestParam("state")String state,
                                                 @RequestParam("objMerchantCode")String objMerchantCode);

    /**
     * 查询用户卡券数量
     * @param userId
     * @param merchantCodeList
     * @param type
     * @param state
     * @return
     */
    @PostMapping("/user-card/selectCount")
    Result<Integer> selectCount(@RequestParam("userId") Long userId,
                                @RequestBody List<String> merchantCodeList,
                                @RequestParam("type")String type,
                                @RequestParam("state")String state);

    /**
     * 商城 创建用户商品关系 与流水
     * @param cardMapUserCards
     */
    @PostMapping("/user-card/createMallUserProductions")
    void createMallUserProductionsAndTrace(@RequestBody CardMapUserCards cardMapUserCards);

    /**
     * 商城购买 查用户关联商品列表
     * @param refSourceKey
     * @return
     */
    @GetMapping("/user-card/mallQueryListByRefSourceKey")
    Result<List<CardMapUserCards>> mallQueryListByRefSourceKey(@RequestParam("refSourceKey") String refSourceKey);

    /**
     * 商城用户 免费 领券
     * @param mallUserGetCardData
     * @return
     */
    @PostMapping("/user-card/mallUserGetCard")
    Result<Boolean> mallUserGetCard(@RequestBody MallUserGetCardData mallUserGetCardData);

    /**
     * 修改用户 使用 免费优惠券状态
     * @param cardNo
     * @param state
     */
    @PostMapping("/user-card/mallUserGetCard")
    void mallUpdateUserCardsState(@RequestParam("cardNo") String cardNo,
                                  @RequestParam("state") String state);


    /**
     * 查询商城 用户卡券 包括购买 和 免费领取
     * @param queryMyCardData
     * @return
     */
    @PostMapping("/user-card/mallQueryUserCard")
    Result<Page<CardMapUserCards>> mallQueryUserCard(@RequestBody QueryMyCardData queryMyCardData);

    /**
     * 获取主体下，商城用户购买的卡券
     * @param userId
     * @param state
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/user-card/queryUserBuyCardList")
    Result<Page<CardMapUserCards>> queryUserBuyCardList(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "state",defaultValue = "") String state,
            @RequestParam("type")String type,
            @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize);

    /**
     * 更新用户卡券为退款状态 , 锁定卡券
     * @param orderOrderDetailsList
     */
    @PostMapping("/user-card/updateRefundState")
    void updateRefundState(@RequestBody List<OrderOrderDetails> orderOrderDetailsList);

    /**
     * 获取购买订单明细的卡券
     * @param refKey
     * @param state
     * @return
     */
    @GetMapping("/user-card/countByRefKeyAndState")
    Result<Integer> queryCountByRefKeyAndState(@RequestParam("refKey") Long refKey,@RequestParam("state") String state);

    /**
     * 退款时，移除用户卡券
     * @param userId
     * @param productionCode
     * @param refKey
     * @return
     */
    @PutMapping("/user-card/user/{userId}/card/{productionCode}")
    Result<Boolean> removeUserCard(
            @PathVariable("userId") Long userId,
            @PathVariable("productionCode") String productionCode,
            @RequestParam("refKey") String refKey);

    /**
     * 锁定单张退款卡券
     * @param cardNo
     */
    @PostMapping("/user-card/updateRefundStateByCardNo")
    void updateRefundStateByCardNo(@RequestParam("cardNo") String cardNo);
}
