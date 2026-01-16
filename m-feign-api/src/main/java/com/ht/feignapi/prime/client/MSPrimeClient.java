package com.ht.feignapi.prime.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@FeignClient(name = "${custom.client.prime.name}",contextId = "msPrime")
public interface MSPrimeClient {

    /**
     * 查询用户绑定卡券数量
     * @param userId
     * @param openid
     * @return
     */
    @GetMapping("/actualCard/queryCardNum")
    Result<Integer> queryCardNum(@RequestParam("userId") Long userId,@RequestParam("openId") String openid);

    /**
     * 绑定手机号同时开通 通联虚拟卡
     * @param phoneNum
     * @param openid
     * @param userId
     */
    @PostMapping("/virtualCard/bindTelAndOpenCard")
    void bindTelAndOpenCard(@RequestParam("phoneNum") String phoneNum,@RequestParam("openid")  String openid,@RequestParam("userId")  Long userId);

    /**
     * 查询绑定的卡
     * @param cardId
     * @return
     */
    @GetMapping("/actualCard/queryByCardNo")
    CardActualMapUser queryByCardNo(@RequestParam("cardId") String cardId);

    /**
     * 创建卡关联
     * @param openId
     * @param cardId
     * @param cardType
     */
    @PostMapping("/actualCard/createActualCard")
    void createActualCard(@RequestParam("openId")String openId, @RequestParam("cardId")String cardId, @RequestParam("cardType")String cardType);

    /**
     * 查询用户余额
     * @param openId
     * @param userAccountFlagCode
     * @return
     */
    @GetMapping("/virtualCard/queryMsUserAccount")
    Result<MsUserAccount> queryMsUserAccount(@RequestParam("openId") String openId,
                                             @RequestParam("userAccountFlagCode") String userAccountFlagCode);

    /**
     * 消费用户余额
     * @param cardId
     * @param amount
     * @param userAccountFlagCode
     * @param ext1
     * @param ext2
     * @param ext3
     */
    @PostMapping("/virtualCard/consumerUserAccount")
    Result<PayResultInfo> consumerUserAccount(@RequestParam("cardId") String cardId,
                                              @RequestParam("amount") Integer amount,
                                              @RequestParam("userAccountFlagCode") String userAccountFlagCode,
                                              @RequestParam("ext1")String ext1,
                                              @RequestParam("ext2")String ext2,
                                              @RequestParam("ext3")String ext3);

    /**
     * 校验二维码的时效性 是否可用
     * @param qrUserMessageData
     * @return
     */
    @PostMapping("/virtualCard/checkQrAuthCode")
    Result<Boolean> checkQrAuthCode(@RequestBody QrUserMessageData qrUserMessageData);

    /**
     * 根据绑定的实体卡号查询关联的电子卡
     * @param icCardId
     * @return
     */
    @GetMapping("/cardPhysical/queryByIcCardId")
    Result<CardPhysical> queryByIcCardId(@RequestParam("icCardId") String icCardId);

    /**
     * 根据绑定的实体卡号查询关联的电子卡
     * @param cardCode
     * @return
     */
    @GetMapping("/cardPhysical/queryByCardCode")
    Result<CardPhysical> queryByCardCode(@RequestParam("cardCode") String cardCode);

    /**
     * 修改电子卡金额
     * @param consumeMoney
     * @param cardPhysical
     */
    @PostMapping("/cardPhysical/updateCardPhysicalMoney")
    Result<CardPhysical> updateCardPhysicalMoney(@RequestParam("consumeMoney") int consumeMoney, @RequestBody CardPhysical cardPhysical);

    /**
     * 电子虚拟卡 核销或计算
     * @param userFlagCode
     * @param amount
     */
    @PostMapping("/cardElectronic/posCombinationPayEleCard")
    Result<RetPosCombinationPayEleCard> posCombinationPayEleCard(@RequestParam("userFlagCode") String userFlagCode, @RequestParam("amount") Integer amount);

    /**
     * 电子虚拟卡 核销
     * @param userFlagCode
     * @param paidAmount
     */
    @PostMapping("/cardElectronic/consumeEleCardMoney")
    Result<RetPosCombinationPayEleCard> consumeEleCardMoney(@RequestParam("paidAmount") Integer paidAmount,@RequestParam("userFlagCode") String userFlagCode);

    /**
     * 电子卡退款
     * @param refundCardOrderPayTraceList
     * @param operator
     */
    @PostMapping("/refund/posCombinationRefund")
    void posCombinationRefund(@RequestBody List<CardOrderPayTrace> refundCardOrderPayTraceList, @RequestParam("operator") String operator,
                              @RequestParam(value = "refundCode",required = false)String refundCode);

    /**
     * 用户买卡成功
     * @param cardOrderDetailsList
     * @param userId
     */
    @PostMapping("/cardElectronic/buyCardSuccessBindUser")
    Result<List<CardElectronic>> buyCardSuccessBindUser(@RequestBody List<CardOrderDetails> cardOrderDetailsList, @RequestParam("userId") String userId);

    /**
     * 校验库存
     * @param primeBuyCardData
     */
    @PostMapping("/cardElectronic/checkQuantity")
    Result<RetCheckQuantityData> checkQuantity(@RequestBody PrimeBuyCardData primeBuyCardData);

    /**
     * 获取用户
     * @param openId
     * @return
     */
    @GetMapping("/userVip/queryByOpenId/")
    Result<VipUser> queryByOpenId(@RequestParam("openId") String openId);

    /**
     * 获取用户
     * @param userId
     * @return
     */
    @GetMapping("/userVip/queryUserById")
    Result<VipUser> queryUserById(@RequestParam("userId") String userId);

    /**
     * 获取用户
     * @param phone
     * @return
     */
    @GetMapping("/userVip/queryUserByPhone/{phone}")
    Result<VipUser> queryUserByPhone(@PathVariable("phone") String phone);

    /**
     * 获取所有批次不分页
     * @return
     */
    @GetMapping("/cardElectronicSell/card-electronic-sell/noPageSellAll")
    Result<List<CardElectronicSell>> noPageSellAll();

    /**
     * 执行退款逻辑
     * @param backOrderCode
     * @param operator
     * @param doRefundData
     */
    @PostMapping("/refund/doBuyCardOrderRefund")
    void doBuyCardOrderRefund(@RequestParam("backOrderCode") String backOrderCode,
                              @RequestParam("operator") String operator,
                              @RequestBody DoRefundData doRefundData);

    /**
     * 校验计算退款金额等数据
     * @param cardNoList
     * @return
     */
    @PostMapping("/refund/checkRefundCardAndMoney")
    Result<RetCheckRefund> checkRefundCardAndMoney(@RequestBody List<String> cardNoList);

    /**
     * 校验点卡是否可退
     * @param cardNo
     * @param orderCode
     * @return
     */
    @GetMapping("/refund/checkCardCanRefund")
    Result<RefundCardMsgDetail> checkCardCanRefund(@RequestParam("cardNo") String cardNo,
                                                   @RequestParam("orderCode") String orderCode);

    /**
     * 根据cardNo查卡信息
     * @param cardNo
     * @return
     */
    @GetMapping("/cardElectronic/cardNo/{cardNo}")
    Result<CardElectronic> queryCardElectronicByCardNo(@PathVariable("cardNo") String cardNo);

    /**
     * 根据原单号 查询退款单
     * @param orderCode
     * @param merchantCode
     * @return
     */
    @GetMapping("/refund/queryRefundOrder")
    Result<List<CardRefundOrder>> queryRefundOrder(@RequestParam("orderCode") String orderCode,
                                                   @RequestParam("merchantCode") String merchantCode,
                                                   @RequestParam(value = "refundCode",required = false)String refundCode);


    /**
     * 查询用户卡总额
     * @param openId
     * @return
     */
    @GetMapping("/userVip/getUserAccountMoney")
    Result<Integer> getUserAllCardMoney(@RequestParam("openId")String openId);


    /**
     * 后台录入用户卡
     * @param cardActualMapUser
     */
    @PostMapping(value = "/actualCard/adminSetUserCard",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Result<CardElectronic> adminSetUserCard(CardActualMapUser cardActualMapUser);

    /**
     * 机构渠道 后台录入用户卡
     * @param cardReceiveUserVo
     */
    @PostMapping(value = "/partyCardReceiveUser/adminSetUserCard")
    Result<CardElectronic> adminChannelSetUserCard(PartyCardReceiveUserVo cardReceiveUserVo);

    /**
     * excel导入
     * @param file
     * @param operatorId
     * @param operatorAccount
     * @return
     */
    @PostMapping("/actualCard/adminExcelImportCard/{operatorId}/{operatorAccount}")
    Result adminExcelImportCard(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "certFile",required = false)MultipartFile certFile,
            @PathVariable("operatorId")String operatorId,
            @PathVariable("operatorAccount")String operatorAccount);


    /**
     * 通联卡 核销 汇总
     * @return
     */
    @GetMapping("/cardBatch/summary")
    Result<List<SummaryData>> summary();

    /**
     * 机构渠道 卡核销 汇总
     * @return
     */
    @GetMapping("/partyCardBatch/channelSummary")
    Result<List<SummaryData>> channelSummary(@RequestParam("channelId") String channelId);


    @GetMapping("/cardBatch/queryBatchByCode")
    Result<CardBatch> queryBatchByCode(@RequestParam("batchCode") String batchCode);

    @GetMapping("/actualCard/summaryDataFree")
    Result<Long> summaryDataFreeCardFaceValueAmount(@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime);

    @PostMapping("/actualCard/queryFixCardOrderDataId")
    Result<List<CardActualMapUser>> queryFixCardOrderDataId(@RequestBody List<String> acmuId);

    @GetMapping("/cardPhysical/summaryData")
    Result<Long> summaryPhyCard(@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime);

    @GetMapping("/refund/adminRefundOrderList")
    Result<Page<CardRefundOrder>> adminRefundOrderList(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize,
                                                       @RequestParam("cardNo")String cardNo, @RequestParam("oriOrderId")String oriOrderId,
                                                       @RequestParam("orderId")String orderId,@RequestParam("userPhone")String userPhone,
                                                       @RequestParam("startTime") String startTime, @RequestParam("endTime")String endTime);

    @GetMapping("/refund/adminRefundOrderListNoPage")
    Result<List<CardRefundOrder>> adminRefundOrderListNoPage(@RequestParam("cardNo")String cardNo, @RequestParam("oriOrderId")String oriOrderId,
                                                       @RequestParam("orderId")String orderId, @RequestParam("userPhone")String userPhone,
                                                             @RequestParam("startTime") String startTime, @RequestParam("endTime")String endTime, @RequestParam("merId")String merId);

    @GetMapping("/refund/allList")
    Result<List<CardRefundOrder>> allRefundList();

    @GetMapping("/refund/allList/merId")
    Result<List<CardRefundOrder>> allListForMerId(@RequestParam("merId") String merId);

    @PostMapping("/cardElectronic/summaryCardNoType")
    Result<List<SummaryCardNoData>> summaryCardNoType(@RequestBody List<SummaryCardNoData> summaryCardNoDataList);

    @PostMapping("/partyCardElectronic/summaryCardNoTypeChannel")
    Result<List<SummaryCardNoData>> summaryCardNoTypeChannel(@RequestParam("channelId") String channelId,@RequestBody List<SummaryCardNoData> summaryCardNoDataList);


    @PostMapping("/cardBatch/summaryCardNoDetailData")
    Result<List<SummaryCardNoDetailData>> summaryCardNoDetailDataList(@RequestBody List<SummaryCardNoDetailData> summaryCardNoDetailDataList);

    @PostMapping("/partyCardBatch/summaryCardNoDetailDataChannel")
    Result<List<SummaryCardNoDetailData>> summaryCardNoDetailDataChannel(@RequestParam("channelId") String channelId,@RequestBody List<SummaryCardNoDetailData> summaryCardNoDetailDataList);



    @PostMapping("/refund/summaryRefundCardNoData")
    Result<List<SummaryCardNoRefundData>> summaryRefundCardNoData(@RequestBody List<SummaryCardNoRefundData> summaryCardNoRefundDataList);

    @PostMapping("/refund/summaryRefundCardNoData/merId")
    Result<List<SummaryCardNoRefundData>> summaryRefundCardNoDataMerId(@RequestParam("merId") String merId,@RequestBody List<SummaryCardNoRefundData> summaryCardNoRefundDataList);

    @PostMapping("/cardElectronic/queryByCardNoList")
    Result<List<QueryExcelBuyCardData>> queryByCardNoList(@RequestBody Set<String> queryExcelBuyCardNoDataList);

    @PostMapping("/userVip/queryUserIdPhoneList")
    Result<List<QueryUserPhoneExcel>> queryUserIdPhoneList(@RequestBody Set<String> set);

    @PostMapping("/cardPhysical/queryByPhyCardNoList")
    Result<List<QueryExcelPhyCardData>> queryByPhyCardNoList(@RequestBody Set<String> phyCardNoList);

    @GetMapping("/dicConstant/queryRefundPassword")
    Result<String> queryRefundPassword();

    @GetMapping("/refund/queryRefundByTraceNo")
    Result<List<CardRefundOrder>> queryRefundByTraceNo(@RequestParam("traceNo") String traceNo);

    @GetMapping("/partyCardElectronic/findOneByCardNo")
    Result<PartyCardElectronic> queryPartyCardElectronicByCardNo(@RequestParam("cardNo") String cardNo);

    @PostMapping("/partyCard/findPartyCardSynData")
    Result<PartyCardSynDataVo> findPartyCardSynData(@RequestBody PartyCardQueryData partyCardQueryData);

    @GetMapping("/cardBatch/summaryPasswordCardTimeScope")
    Result<Long> summaryPasswordCardTimeScope(@RequestParam("cardType")String cardType, @RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime);

    @PostMapping("/actualCard/adminBatchBindCard")
    Result<AdminBatchBindCardRet> adminBatchBindCard(AdminBatchBindCardData adminBatchBindCardData);


    /**
     * 获取售卖的电子卡券
     * @param pageNo
     * @param pageSize
     * @param cardName
     * @return
     */
    @GetMapping("/cardElectronicSell/card-electronic-sell/byMerchant")
    Result<Page<CardElectronicSell>> getSellMerchantList(
            @RequestParam(value = "pageNo",defaultValue = "1",required = false) Long pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) Long pageSize,
            @RequestParam(value = "cardName",defaultValue = "",required = false) String cardName,
            @RequestParam(value = "vipProducts",defaultValue = "",required = false) String vipProducts,
            @RequestParam(value = "merchantCode",defaultValue = "",required = false) String merchantCode
    );

    /**
     * 获取售卖的电子卡券
     * @param batchCode
     * @return
     */
    @GetMapping("/cardElectronicSell/card-electronic-sell/{batchCode}")
    Result<CardElectronicSell> queryBatchSellByCode(@PathVariable("batchCode") String batchCode);

    /**
     * higo 小程序用户入库逻辑
     * @param vipUser
     * @return
     */
    @PostMapping("/userVip/hiGoUserInfo")
    Result<VipUser> hiGoUserInfo(@RequestBody VipUser vipUser);


    /**
     * 用户账号余额(线上商城预售买卡 )
     * @param userPhone
     * @return
     */
    @GetMapping("/cardElectronic/userShoppingMalAccountBalance")
    Result<Long>  userShoppingMalAccountBalance(@RequestParam("userPhone")String userPhone);

    /**
     * 用户卡列表(线上商城预售买卡 )
     * @param cardQueryData
     * @return
     */
    @PostMapping("/cardElectronic/cards/page")
    Result<Page<CardElectronicVo>> getCardsPage(@RequestBody CardQueryData cardQueryData);


    /**
     * 根据原单号 查询退款单
     * @param orderCode
     * @param merchantCode
     * @return
     */
    @PostMapping("/refund/order/refundAmount")
    Result<Long> findRefundAmount(@RequestParam("orderCode") String orderCode,
                                                   @RequestParam("merchantCode") String merchantCode,
                                                   @RequestParam(value = "refundCode",required = false)String refundCode);

}
