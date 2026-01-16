package com.ht.user.card.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.*;

import com.ht.user.card.service.*;
import com.ht.user.card.vo.*;
import com.ht.user.common.Result;
import com.ht.user.common.StatusCode;

import com.ht.user.config.UserCardsStateConfig;
import com.ht.user.config.UserCardsTypeConfig;
import com.ht.user.mall.constant.CardUserMallConstant;
import com.ht.user.mall.entity.OrderOrderDetails;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户，卡绑定关系 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("/user-card")
public class CardMapUserCardsController {

    @Autowired
    private CardMapUserCardsService cardMapUserCardsService;

    @Autowired
    private CardMapUserCardsTraceService cardMapUserCardsTraceService;


    /**
     * C端公众号 用户领券 (免费领券)
     *
     * @param userGetCardData
     * @return
     */
    @PostMapping("/userGetCard")
    public boolean userGetCard(@RequestBody UserGetCardData userGetCardData) {
        return cardMapUserCardsService.userGetCard(userGetCardData.getUserId(), userGetCardData.getCardCode(), userGetCardData.getMerchantCode(), userGetCardData.getBatchCode());
    }

    /**
     * 根据绑定的cardNo 生成二维码图片
     *
     * @param cardNo
     * @return
     */
    @GetMapping("/qrCode/{cardNo}")
    public Result getQrCode(@PathVariable("cardNo") String cardNo) {
        String qrCode = cardMapUserCardsService.createQrCode(cardNo);
        return new Result(true, StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getDesc(), qrCode);
    }


    //************************************************** 修改后代码 ******************************************************//

    /**
     * 获取会员卡信息
     * @param merchantCode
     * @param userId
     * @return
     */
    @GetMapping("/{merchantCode}/vipCard/{userId}")
    public CardMapUserCards getUserVipCard(@PathVariable("merchantCode") String merchantCode,@PathVariable("userId") Long userId){
        return cardMapUserCardsService.getUserVipCard(merchantCode,userId);
    }

    /**
     * 保存商户用户会员信息
     * @param userCard
     */
    @PostMapping
    public void save(@RequestBody CardMapUserCards userCard){
        cardMapUserCardsService.saveOrUpdate(userCard);
    }

    /**
     * 查询用户余额
     * @param userId
     * @return
     */
    @GetMapping("/money/{userId}")
    public BigDecimal queryUserMoney(@PathVariable("userId") Long userId){
        return cardMapUserCardsService.queryUserMoney(userId);
    }

    /**
     * 用户绑定实体储值卡(xxxxxxxxx CardMapUserCardsService类方法同名 )
     * @param icCardId
     * @param userId
     * @param merchantCode
     */
    @PostMapping("/realCard")
    public void createCardMapUserCards(@RequestParam("icCardId") String icCardId,
                                @RequestParam("userId") String userId,
                                @RequestParam("merchantCode") String merchantCode){
        cardMapUserCardsService.createCardMapUserCards(icCardId,userId,merchantCode);
    }

    /**
     * 发放card
     * @param merchantCode
     * @param cardCode
     * @param userId
     * @param cardNo
     * @param batchCode
     * @return 创建结果
     */
    @PostMapping("/sendCard")
    public boolean createCardMapUserCards(@RequestParam("merchantCode") String merchantCode,
                                   @RequestParam("cardCode") String cardCode,
                                   @RequestParam("userId") Long userId,
                                   @RequestParam("cardNo") String cardNo,
                                   @RequestParam("batchCode") String batchCode){
        return cardMapUserCardsService.createCardMapUserCards(merchantCode,cardCode,userId,cardNo,batchCode);
    }

    /**
     * 查询用户与卡的关联关系(xxxxxxxx CardMapUserCardsService类方法同名 )
     * @param icCardId
     * @param userId
     * @return
     */
    @GetMapping("/icCard")
    public CardMapUserCards queryByIcCardIdAndUserId(@RequestParam("icCardId") String icCardId,@RequestParam("userId") Long userId){
        return cardMapUserCardsService.queryByIcCardIdAndUserId(icCardId,userId);
    }

    /**
     * 用户实体卡绑定(xxxxxxxxxx  CardMapUserCardsService类方法同名 )
     * @param cardNo
     * @param userId
     * @param icCardId
     */
    @PostMapping("/bindIcCard")
    public void bindIcCardId(@RequestParam("cardNo") String cardNo,@RequestParam("userId") Long userId,@RequestParam("icCardId") String icCardId){
        cardMapUserCardsService.bindIcCardId(cardNo,userId,icCardId);
    }

    /**
     * 查询用户卡券数量
     * @param userId
     * @param merchantCodeList
     * @param type
     * @param state
     * @return
     */
    @PostMapping("/selectCount")
    public Integer selectCount(@RequestParam("userId") Long userId,
                               @RequestBody List<String> merchantCodeList,
                               @RequestParam("type")String type,
                               @RequestParam("state")String state){
        return cardMapUserCardsService.selectCount(userId,merchantCodeList,type,state);
    }
    /**
     * 根据实体卡卡号查询 关联关系(xxxxxx  CardMapUserCardsService类方法同名)
     * @param icCardId
     * @return
     */
    @GetMapping("/cardUserInfo/{icCardId}")
    public CardMapUserCards queryByIcCardId(@PathVariable("icCardId") String icCardId){
        return cardMapUserCardsService.queryByIcCardId(icCardId);
    }

    /**
     * 查询用户账户 对应的通联卡号关系(xxxxxx  CardMapUserCardsService类方法同名)
     * @param userId
     * @param type
     * @return
     */
    @GetMapping("/cardUserTlInfo/{userId}/{type}")
    public CardMapUserCards queryByUserIdAndAccount(@PathVariable("userId") Long userId,@PathVariable("type") String type){
        return cardMapUserCardsService.queryByUserIdAndAccount(userId,type);
    }

    /**
     * 根据卡 cardNo 查询关系(xxxxxx  CardMapUserCardsService类方法同名)
     * @param cardNo
     * @return
     */
    @GetMapping("/cardNoCard/{cardNo}")
    public CardMapUserCards queryByCardNo(@PathVariable("cardNo") String cardNo){
        return cardMapUserCardsService.queryByCardNo(cardNo);
    }

    /**
     *  更新用户卡状态
     * @param cardNoList
     * @param state
     */
    @PostMapping("/cardState/{state}")
    public void updateUserCardsState(@RequestBody List<PosSelectCardNo> cardNoList, @PathVariable("state") String state){
        cardMapUserCardsService.updateUserCardsState(cardNoList,state);
    }

    /**
     * pos发券
     * @param merchantCode
     * @param cardCode
     * @param userId
     * @param batchCode
     * @return
     */
    @GetMapping("/pos/sendCard")
    public boolean posSendCard(@RequestParam("merchantCode") String merchantCode,
                        @RequestParam("cardCode") String cardCode,
                        @RequestParam("userId") Long userId,
                        @RequestParam("batchCode") String batchCode){
        return cardMapUserCardsService.posSendCard(merchantCode,cardCode,userId,batchCode);
    }

    /**
     * 通过userId，商户号，获取用户拥有的卡券列表
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @GetMapping("/userAllCard/{merchantCode}/{userId}/{state}")
    public List<CardMapUserCards> getByUserIdAndMerchantCode(@PathVariable("userId") Long userId,
                                                      @PathVariable("merchantCode") String merchantCode,
                                                      @PathVariable("state") String state){
        return cardMapUserCardsService.queryByUserIdAndMerchantCode(userId,merchantCode,state);
    }

    /**
     * 根据用户id 和商户编码 查询用户卡列表 (已绑定实体卡)
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @GetMapping("/userNotIcCard/{merchantCode}/{userId}/{state}")
    public List<CardMapUserCards> getByUserIdAndMerchantCodeNotIc(@PathVariable("userId") Long userId,
                                                           @PathVariable("merchantCode") String merchantCode,
                                                           @PathVariable("state") String state){
        return cardMapUserCardsService.queryByUserIdAndMerchantCodeNotIc(userId,merchantCode,state);
    }

    /**
     * 通过实体卡号获取用户卡
     * @param icCard
     * @return
     */
    @GetMapping("/icCard/{icCard}")
    public CardMapUserCards getByIcCard(@PathVariable("icCard") String icCard){
        return cardMapUserCardsService.queryByIcCardId(icCard);
    }


    /**
     * 获取pos卡券列表
     * @param cardMapUserCardsVOList
     * @return
     */
    @GetMapping("/posUserCardVos")
    public List<PosUserCardVO> packagePosUserCardVOList(@RequestBody List<CardMapUserCardsVO> cardMapUserCardsVOList){
        return cardMapUserCardsService.packagePosUserCardVOList(cardMapUserCardsVOList);
    }


    /**
     * 获取余额卡券
     * @param cardMapUserCardsVOList
     * @param amount
     * @return
     */
    @GetMapping("/cardMapUserCardsVo/{amount}")
    public CardMapUserCardsVO settlementCardMoney(@RequestBody List<CardMapUserCardsVO> cardMapUserCardsVOList,@PathVariable("amount") Integer amount){
        return cardMapUserCardsService.settlementCardMoney(cardMapUserCardsVOList,amount);
    }


    /**
     * 获取pos机卡券信息
     * @param cardMapUserCardsVO
     * @return
     */
    @GetMapping("/posUserCardVo")
    public PosUserCardVO packagePosUserCardVO(@RequestBody CardMapUserCardsVO cardMapUserCardsVO){
        return cardMapUserCardsService.packagePosUserCardVO(cardMapUserCardsVO);
    }

    /**
     * 封装 计算后的余额返回
     * @param list
     * @param userAccountMoney 账户余额
     * @param settlementAmount 金额
     */
    @GetMapping("/userAccountMoney/{userAccountMoney}/{settlementAmount}")
    public Integer afterUserAccount(@RequestBody List<CardMapUserCardsVO> list,
                             @PathVariable("userAccountMoney") Integer userAccountMoney,
                             @PathVariable("settlementAmount") Integer settlementAmount){
        return cardMapUserCardsService.afterUserAccount(list,userAccountMoney,settlementAmount);
    }

    /**
     * 计算扣除后的用户余额
     * @param list
     * @param userAccountMoney
     * @param settlementAmount
     * @param flag
     * @return
     */
    @GetMapping("/userAccountMoneyAfterPaid/{userAccountMoney}/{settlementAmount}/{flag}")
    public Integer afterUserAccount(@RequestBody List<PosSelectCardNo> list,
                             @PathVariable("userAccountMoney") Integer userAccountMoney,
                             @PathVariable("settlementAmount") Integer settlementAmount,
                             @PathVariable("flag") Boolean flag) {
        return cardMapUserCardsService.afterUserAccount(list,userAccountMoney,settlementAmount,false);
    }

    /**
     *  获取用户的计次卡券列表
     * @param userId
     * @param merchantCode
     * @return
     */
    @GetMapping("/userNumberCardList")
    public List<CardMapUserCardsVO> selectUserNumberList(
            @RequestParam String userId,
            @RequestParam String merchantCode){
        return cardMapUserCardsService.selectUserNumberList(userId, merchantCode);
    }

    /**
     * c扫B 核销用户 计次券
     * @param cardMapUserCards
     */
    @PostMapping("/qrUseNumberCard")
    public Integer qrUseNumberCard(@RequestBody CardMapUserCards cardMapUserCards){
        return cardMapUserCardsService.qrUseNumberCard(cardMapUserCards.getCardNo());
    }

    /**
     * 获取用户卡券数目
     * @param userId
     * @param batchCode
     * @param cardCode
     * @param date
     * @return
     */
    @GetMapping("/userCardAmount")
    public Integer getUserCardAmount(
            @RequestParam Long userId,
            @RequestParam String batchCode,
            @RequestParam String cardCode,
            @RequestParam String date){
        return cardMapUserCardsService.getUserCardAmount(userId,batchCode,cardCode,date);
    }

    /**
     *  获取用户可用的虚拟卡券
     * @param userId
     * @param merchantCode
     * @param type
     * @param state
     * @param objMerchantCode
     * @return
     */
    @GetMapping("/posCanUseCard")
    public List<CardMapUserCards> posCanUseCard(@RequestParam("userId") Long userId,
                                                 @RequestParam("merchantCode")String merchantCode,
                                                 @RequestParam("type")String type,
                                                 @RequestParam("state")String state,
                                                @RequestParam("objMerchantCode")String objMerchantCode){
        return cardMapUserCardsService.posCanUseCard(userId,merchantCode,type,state,objMerchantCode);
    }

    /**
     * 创建通联账户卡 与用户绑定关联关系(CardMapUserCardsService类方法同名 )
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
    @PostMapping("/cardMapUser")
    public void createCardMapUserCards(@RequestParam("cardCode") String cardCode,
                                @RequestParam("userId")Long userId,
                                @RequestParam("objectMerchantCode")String objectMerchantCode,
                                @RequestParam("cardName")String cardName,
                                @RequestParam("categoryCode")String categoryCode,
                                @RequestParam("categoryName")String categoryName,
                                @RequestParam("state")String state,
                                @RequestParam("type")String type,
                                @RequestParam("cardId")String cardId){
        cardMapUserCardsService.createCardMapUserCards(cardCode,
                userId,
                objectMerchantCode,
                cardName,
                categoryCode,
                categoryName,
                state,
                type,
                cardId);
    }


    /**
     * 商城 创建用户商品关系 与流水
     * @param cardMapUserCards
     */
    @PostMapping("/createMallUserProductions")
    public void createMallUserProductionsAndTrace(@RequestBody CardMapUserCards cardMapUserCards){
        cardMapUserCardsService.saveCardMapUserAndTrace(cardMapUserCards);
    }

    /**
     * 商城购买 查用户关联商品列表
     * @param refSourceKey
     * @return
     */
    @GetMapping("/mallQueryListByRefSourceKey")
    public List<CardMapUserCards> mallQueryListByBatchCode(@RequestParam("refSourceKey") String refSourceKey){
        QueryWrapper<CardMapUserCards> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("ref_source_key",refSourceKey);
        return cardMapUserCardsService.list(queryWrapper);
    }

    /**
     * 商城用户 免费 领券
     * @param mallUserGetCardData
     * @return
     */
    @PostMapping("/mallUserGetCard")
    public Boolean mallUserGetCard(@RequestBody MallUserGetCardData mallUserGetCardData){
        return cardMapUserCardsService.mallUserGetCard(mallUserGetCardData);
    }

    /**
     * 商城 修改用户 使用 免费优惠券状态
     * @param cardNo
     * @param state
     */
    @PostMapping("/user-card/mallUserGetCard")
    public void mallUpdateUserCardsState(@RequestParam("cardNo") String cardNo,
                                  @RequestParam("state") String state){
        cardMapUserCardsService.updateStateByCardNo(cardNo,state);
        CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByCardNo(cardNo);
        cardMapUserCardsTraceService.createCardMapUserCardsTrace(cardMapUserCards.getUserId(),
                cardMapUserCards.getMerchantCode(),
                cardMapUserCards.getCardCode(),
                cardMapUserCards.getCardNo(),
                "mall_pay_use",
                new Date(),
                "normal",
                cardMapUserCards.getBatchCode());
    }

    /**
     * 查询商城 用户卡券
     * @param queryMyCardData
     * @return
     */
    @PostMapping("/mallQueryUserCard")
    public Page<CardMapUserCards> mallQueryUserCard(@RequestBody QueryMyCardData queryMyCardData){
        return cardMapUserCardsService.mallQueryUserCard(queryMyCardData);
    }

    /**
     * 获取主体下，用户的卡券
     * @param userId
     * @param merchantCodes
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/objectUserCard/{merchantCodes}/{userId}")
    public IPage<CardMapUserCards> getMallCouponTypeCardInMerchants(
            @PathVariable("userId") Long userId,
            @PathVariable("merchantCodes") List<String> merchantCodes,
            @RequestParam(value = "state",defaultValue = "") String state,
            @RequestParam(value = "pageNo",defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return cardMapUserCardsService.getUserCardInMerchants(userId,merchantCodes,state,pageNo,pageSize, UserCardsStateConfig.MALL_FREE );
    }

    /**
     * 获取主体下，商城用户购买的卡券
     * @param userId
     * @param state
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/queryUserBuyCardList")
    public Page<CardMapUserCards> queryUserBuyCardList(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "state",defaultValue = "") String state,
            @RequestParam(value = "type",defaultValue = "") String type,
            @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return cardMapUserCardsService.queryUserBuyCardList(userId,state,type,pageNo,pageSize);
    }

    /**
     * 更新用户卡券为退款状态 , 锁定卡券
     * @param orderOrderDetailsList
     */
    @PostMapping("/updateRefundState")
    public void updateRefundState(@RequestBody List<OrderOrderDetails> orderOrderDetailsList){
        cardMapUserCardsService.updateRefundState(orderOrderDetailsList);
    }

    /**
     * 获取购买订单明细的卡券
     * @param refKey
     * @param state
     * @return
     */
    @GetMapping("/countByRefKeyAndState")
    public Integer queryCountByRefKeyAndState(@RequestParam("refKey") Long refKey,@RequestParam("state") String state){
        return cardMapUserCardsService.queryCountByRefKeyAndState(refKey,state);
    }

    /**
     * 退款时，移除用户卡券
     * @param userId
     * @param productionCode
     * @param refKey
     * @return
     */
    @PutMapping("/user/{userId}/card/{productionCode}")
    public Boolean removeUserCard(
            @PathVariable("userId") Long userId,
            @PathVariable("productionCode") String productionCode,
            @RequestParam("refKey") String refKey){
        return cardMapUserCardsService.removeUserCardForRefund(userId,productionCode,refKey);
    }

    /**
     * 锁定单张退款卡券
     * @param cardNo
     */
    @PostMapping("/updateRefundStateByCardNo")
    public void updateRefundStateByCardNo(@RequestParam("cardNo") String cardNo){
        cardMapUserCardsService.updateStateByCardNo(cardNo,UserCardsStateConfig.REFUND_ING);
    }
}

