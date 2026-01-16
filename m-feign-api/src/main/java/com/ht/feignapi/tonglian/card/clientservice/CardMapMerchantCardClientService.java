package com.ht.feignapi.tonglian.card.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.admin.entity.MerchantCardEditVo;
import com.ht.feignapi.tonglian.admin.entity.MerchantUserCardVo;
import com.ht.feignapi.tonglian.admin.entity.UserFreeCard;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/13 14:51
 */
@FeignClient(name = "${custom.client.user.name}",contextId = "cardMapMerchant")
public interface CardMapMerchantCardClientService {

    /**
     * 根据 卡编码 , 商户编码,批次号 查询商户卡(xxxxx    CardMapMerchantCardsService 类 queryByCardCodeAndMerchantCodeBatchCode方法)
     * @param cardCode
     * @param merchantCode
     * @param batchCode
     * @return
     */
    @GetMapping("/merchant-card/{cardCode}/{merchantCode}/{batchCode}")
    Result<CardMapMerchantCards> queryByCardCodeAndMerchantCodeBatchCode(@PathVariable("cardCode") String cardCode, @PathVariable("merchantCode") String merchantCode, @PathVariable("batchCode") String batchCode);

    /**
     * 获取到cardCode所有的merchantCode
     * @param cardCode
     * @return
     */
    @GetMapping("/merchant-card/owners/{cardCode}")
    Result<List<String>> getCardMerchantCodes(@PathVariable("cardCode") String cardCode);

    /**
     * 获取主体与子商户所有的卡券
     * @param merchantCardSearch
     * @return
     */
    @PostMapping("/merchant-card/allMerchantCards")
    Result<Page<MerchantCardListVo>> getObjectAndSonMerchantCards(@RequestBody MerchantCardSearch merchantCardSearch, @RequestParam("pageNo") Long pageNo, @RequestParam("pageSize") Long pageSize);

    /**
     * 获取商户所有的卡券
     * @param merchantCode
     * @param merchantCardSearch
     * @return
     */
    @PostMapping("/merchant-card/selfCards/{merchantCode}")
    Result<Page<MerchantCardListVo>> getCardProductsByMerchantCode(@PathVariable("merchantCode") String merchantCode,@RequestBody MerchantCardSearch merchantCardSearch, @RequestParam("pageNo") Long pageNo,@RequestParam("pageSize") Long pageSize);

    /**
     * 通过卡号批次号，获取卡实例信息
     * @param cardCode
     * @param batchCode
     * @return
     */
    @GetMapping("/merchant-card/msg/{cardCode}/{batchCode}")
    Result<CardMapMerchantCards> queryByCardCodeAndBatchCode(@PathVariable("cardCode") String cardCode,@PathVariable("batchCode") String batchCode);

    /**
     * 获取需要显示属性
     * @param cardMapMerchantCards
     * @return
     */
    @GetMapping("/merchant-card/cardMsg")
    Result<CardCards> getShowOtherData(@RequestBody CardMapMerchantCards cardMapMerchantCards);

    /**
     * 获取某个商户下的某种类型卡券
     * @param merchantCode
     * @param type
     * @return
     */
    @GetMapping("/merchant-card/{merchantCode}/type/{type}")
    Result<List<CardMapMerchantCards>> queryListByMerchantCode(@PathVariable("merchantCode") String merchantCode,@PathVariable("type") String type);

    /**
     * 获取用户虚拟卡券列表 不包含计次券
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @GetMapping("/merchant-card/{merchantCode}/userCard/{userId}/{state}")
    Result<List<CardMapUserCards>> selectByUserIdAndMerchantCodeNoNumber(@PathVariable("userId") Long userId,
                                                                 @PathVariable("merchantCode") String merchantCode,
                                                                 @PathVariable("state") String state);

    /**
     * 售券管理--列表
     * @param merchantCode
     * @param cardName
     * @param cardType
     * @param cardState
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/admin/merchantCard/{merchantCode}")
    Result<Page<CardListVo>> getCardsByMerchantCode(@PathVariable("merchantCode") String merchantCode,
                                                    @RequestParam("cardName") String cardName,
                                                    @RequestParam("cardType") String cardType,
                                                    @RequestParam("cardState") String cardState,
                                                    @RequestParam("pageNo") Long pageNo,
                                                    @RequestParam("pageSize") Long pageSize);

    /**
     * 获取电子券列表
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/admin/merchantCard/virtualCards/{merchantCode}")
    Result<Page<MerchantUserCardVo>> getUserCardList(@PathVariable("merchantCode") String merchantCode,
                                                     @RequestParam("pageNo") Long pageNo,
                                                     @RequestParam("pageSize") Long pageSize);


    /**
     * 查询 卡券类商品
     * @param cardCode
     * @param storeMerchantCode
     * @return
     */
    @GetMapping("/merchant-card/mallQueryCodeMerchantCodeType")
    Result<CardMapMerchantCards> mallQueryCodeMerchantCodeType(@RequestParam("cardCode") String cardCode,
                                                           @RequestParam("storeMerchantCode") String storeMerchantCode,
                                                           @RequestParam("type")String type);

    /**
     * 保存商城出售的门票之类的一次性券
     * @param card
     * @param merchantCode
     */
    @PostMapping("/merchant-card/cardProduction/{merchantCode}")
    void saveMallSellCards(@PathVariable("merchantCode") String merchantCode,@RequestBody CardCards card);

    /**
     * 保存出售卡券
     * @param merchantCode
     * @param cardCode
     * @param merchantCard
     */
    @PostMapping("/admin/merchantCard/saleCard/{merchantCode}/{cardCode}")
    void saveSaleCard(@PathVariable("merchantCode") String merchantCode,@PathVariable("cardCode") String cardCode,@RequestBody MerchantCardEditVo merchantCard);

    @PostMapping("/admin/merchantCard/posCard/{merchantCode}")
    Result<List<CardMapMerchantCards>> getPostCard(@PathVariable("merchantCode") String merchantCode, @RequestBody(required = false) HashMap<String, String> cardName);

    @PostMapping("/admin/merchantCard/userCard/free/{merchantCode}")
    Result<String> createUserFreeCards(
            @RequestBody UserFreeCard userFreeCard,
            @PathVariable("merchantCode") String merchantCode);

    /**
     * 获取商户的商城出售卡券
     * @param merchantCodes
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/merchant-card/{merchantCodes}/type/mallSell")
    Result<Page<CardMapMerchantCards>> getMallSellCard(
            @PathVariable("merchantCodes") List<String> merchantCodes,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize);

    /**
     * 获取商户的商城出售卡券(列表专用)
     * @param merchantCodes
     * @param pageNo
     * @param pageSize
     * @param productionCode
     * @param productionName
     * @param state
     * @return
     */
    @GetMapping("/merchant-card/{merchantCodes}/type/mallSell")
    Result<Page<CardMapMerchantCards>> getMallSellCardObject(
            @PathVariable("merchantCodes") List<String> merchantCodes,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize,
            @RequestParam("productionName") String productionName,
            @RequestParam("productionCode") String productionCode,
            @RequestParam("state") String state);

    /**
     * 商城v1.0,卡券v2.0  每次新建一个规则不同的卡券，则创建一个cardCode
     * 不会存在同个商户下，同个cardCode的merchantCard数据
     * @param merchantCode
     * @param cardCode
     * @return
     */
    @GetMapping("/merchant-card/mallCard/{merchantCode}/{cardCode}")
    Result<CardMapMerchantCards> getMerchantCard(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardCode") String cardCode);

    /**
     * 获取主体卡券列表
     * @param subMerchantList
     * @param state
     * @param pageNo
     * @param pageSize
     * @param type mall_sell售卖，mall_free免费
     * @return
     */
    @GetMapping("/merchant-card/mallCardList/{merchantCodes}")
    Result<Page<Object>> getCardProducts(
            @PathVariable("merchantCodes") List<Merchants> subMerchantList,
            @RequestParam("state") String state,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize,
            @RequestParam("type") String type);

    /**
     * 保存商户发布的免费卡券
     * @param card
     * @param merchantCode
     */
    @PostMapping("/merchant-card/mallCoupon/{merchantCode}")
    void saveMallCoupon(@RequestBody CardCards card,@PathVariable("merchantCode") String merchantCode);

    /**
     * 保存更新卡券
     * @param cardMapMerchantCards
     */
    @PostMapping("/merchant-card")
    void save(CardMapMerchantCards cardMapMerchantCards);
}
