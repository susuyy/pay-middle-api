package com.ht.user.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.admin.vo.*;
import com.ht.user.card.common.CardConstant;
import com.ht.user.card.common.CardType;
import com.ht.user.card.entity.*;
import com.ht.user.card.service.*;
import com.ht.user.common.Result;
import com.ht.user.sysconstant.DbConstantGroupConfig;
import com.ht.user.sysconstant.service.DicConstantService;
import com.ht.user.utils.RequestQrCodeDataStrUtil;
import com.ht.user.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/21 11:03
 */
@RestController
@RequestMapping("/admin/merchantCard")
@CrossOrigin
public class AdminMerchantsCardController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DistributeTraceService distributeTraceService;

    @Autowired
    private DicConstantService dicConstantService;

    @Autowired
    private CardMapMerchantCardsService cardMapMerchantCardsService;

    @Autowired
    private CardCardsService cardCardsService;

    /**
     * 创建卡券--列表
     *
     * @param merchantCode 商户号
     * @param cardName
     * @param cardType
     * @param cardState
     * @param pageNo       第几页
     * @param pageSize     每页展示几条数据
     * @return 封装Result
     */
    @GetMapping("/{merchantCode}")
    public IPage<CardListVo> getCardList(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(required = false,defaultValue = "") String cardName,
            @RequestParam(required = false,defaultValue = "") String cardType,
            @RequestParam(required = false,defaultValue = "") String cardState,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        CodeSearch cardSearch = getCodeSearch(cardName, cardType, cardState);
        IPage<CardListVo> page = new Page<>(pageNo, pageSize);
        List<CardListVo> list = cardMapMerchantCardsService.getCardsByMerchantCode(merchantCode, page, cardSearch);
        list.forEach(getCardListVoConsumer());
        page.setRecords(list);
        return page;
    }

    /**
     * 创建卡券--新建
     *
     * @param cardEditVo 优惠券模板信息
     * @return 保存结果
     */
    @PostMapping
    public String saveCard(@RequestBody @Valid CardEditVo cardEditVo) {
        cardMapMerchantCardsService.createMerchantCards(cardEditVo);
        return "优惠券保存成功";
    }

    private Consumer<CardListVo> getCardListVoConsumer() {
        return e -> {
            if ("beginToEnd".equals(e.getValidityType())) {
                e.setValidTimeStr("有效期：" + e.getValidFrom().substring(0, 16) + " ~ " + e.getValidTo().substring(0, 16));
            } else if ("validDuration".equals(e.getValidityType())) {
                e.setValidTimeStr("领券后" + e.getValidGapAfterApplied() / 24 + "天生效，有效" + e.getPeriodOfValidity() / 24 + "天");
            }
            if ("coupon".equals(e.getType())) {
                e.setFaceValue(String.valueOf(Integer.parseInt(e.getFaceValue()) / 100));
            }
        };
    }

    private CodeSearch getCodeSearch(@RequestParam(required = false, defaultValue = "") String cardName, @RequestParam(required = false, defaultValue = "") String cardType, @RequestParam(required = false, defaultValue = "") String cardState) {
        CodeSearch cardSearch = new CodeSearch();
        cardSearch.setCardName(cardName);
        cardSearch.setCardState(cardState);
        cardSearch.setCardType(cardType);
        return cardSearch;
    }

    /**
     * 获取卡券类型下拉框
     *
     * @return
     */
    @GetMapping("/category")
    public Result getCategoryList() {
        return ResultUtil.success(null);
    }

    /**
     * 售券管理--查看
     *
     * @param merchantCode
     * @param cardCode
     * @param batchCode
     * @return
     */
    @GetMapping("/merchantCardInfo/{merchantCode}/{cardCode}/{batchCode}")
    public CardMapMerchantCards getMerchantCardInfo(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardCode") String cardCode,
            @PathVariable("batchCode") String batchCode) {
        CardMapMerchantCards merchantCards = cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(cardCode, merchantCode, batchCode);
        Assert.notNull(merchantCards.getPrice(), "非法出售金额");
        merchantCards.setPrice(merchantCards.getPrice() / 100);
        if (merchantCards.getReferencePrice() != null) {
            merchantCards.setReferencePrice(merchantCards.getReferencePrice() / 100);
        }
        return merchantCards;
    }

    /**
     * 售券管理--发布，撤销发布功能
     *
     * @param merchantCode 商户号
     * @param cardCode     卡号
     * @return
     */
    @PutMapping("/state/{merchantCode}/{cardCode}")
    public String publishCard(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardCode") String cardCode,
            @RequestBody HashMap<String, String> map) {
        Assert.isTrue(map.containsKey("state"), "参数有误");
        Assert.isTrue(map.containsKey("batchCode"), "请填写批次号");
        CardMapMerchantCards card = cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(cardCode, merchantCode, map.get("batchCode"));
        card.setOnSaleState(map.get("state"));
        cardMapMerchantCardsService.saveOrUpdate(card);
        return "保存成功";
    }

    /**
     * 售券管理--删除
     *
     * @param merchantCode 商户号
     * @param cardCode     卡号
     * @param batchCode    批次号
     * @return
     */
    @DeleteMapping("/{merchantCode}/{cardCode}/{batchCode}")
    public String deleteCard(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardCode") String cardCode,
            @PathVariable("batchCode") String batchCode) {
        CardMapMerchantCards card = cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(cardCode, merchantCode, batchCode);
        card.setState("disabled");
        cardMapMerchantCardsService.saveOrUpdate(card);
        return "删除成功";
    }



    /**
     * 创建卡券--更新卡状态，冻结，解冻
     *
     * @param cardCode 卡号
     * @param map      state字段必填
     * @return
     */
    @PutMapping("/state/{cardCode}")
    public String updateCardState(@PathVariable("cardCode") String cardCode, @RequestBody HashMap<String, String> map) {
        Assert.isTrue(map.containsKey("state"), "参数有误");
        CardCards card = cardCardsService.selectByCardCode(cardCode);
        card.setState(map.get("state"));
        Boolean result = cardCardsService.updateById(card);
        Assert.isTrue(result, "保存失败");
        return "保存成功";
    }

    /**
     * 创建卡券--更新卡券，目前只有更新notice功能。
     *
     * @param cardCode 卡号
     * @param map      notice
     * @return 保存结果
     */
    @PutMapping("/{cardCode}")
    public String updateCard(@PathVariable("cardCode") String cardCode, @RequestBody HashMap<String, String> map) {
        CardCards card = cardCardsService.selectByCardCode(cardCode);
        card.setNotice(map.containsKey("notice")?map.get("notice"):card.getNotice());
        card.setCardName(map.containsKey("cardName")?map.get("cardName"):card.getCardName());
        Boolean result = cardCardsService.updateById(card);
        Assert.isTrue(result, "保存失败");
        return "保存成功";
    }

    /**
     * 获取卡券类型下拉框
     *
     * @return 卡券类型
     */
    @GetMapping("/cardType")
    public Map<String, String> getCardTypeList() {
        Map<String, String> map = dicConstantService.getConstantMap(DbConstantGroupConfig.CARD_TYPE);
        return map;
    }

    /**
     * 获取卡券状态
     *
     * @return 卡券状态列表
     */
    @GetMapping("/cardValidState")
    public Map<String, String> getCardValidState() {
        Map<String, String> map = dicConstantService.getConstantMap(DbConstantGroupConfig.CARD_STATE);
        return map;
    }

    /**
     * pos端--获取商户下pos卡券
     *
     * @param merchantCode
     * @param cardName
     * @return
     */
    @PostMapping("/posCard/{merchantCode}")
    public List<CardMapMerchantCards> getPostCard(@PathVariable("merchantCode") String merchantCode, @RequestBody(required = false) HashMap<String, String> cardName) {
        String cardNameStr = "";
        Date now = new Date();
        if (cardName != null && cardName.containsKey("cardName")) {
            cardNameStr = cardName.get("cardName");
        }
        List<CardMapMerchantCards> merchantCards = cardMapMerchantCardsService.getPosCardList(merchantCode, cardNameStr);
        List<CardMapMerchantCards> onSaleCard = merchantCards.stream().filter(e -> {
            if (e.getOnSaleDate() != null && e.getHaltSaleDate() != null) {
                return now.after(e.getOnSaleDate()) && now.before(e.getHaltSaleDate());
            }
            return true;
        }).collect(Collectors.toList());
        return onSaleCard;
    }

    /**
     * 售券管理--新增
     *
     * @param merchantCode
     * @param cardCode
     * @param merchantCard 出售卡券对象
     * @return
     */
    @PostMapping("/saleCard/{merchantCode}/{cardCode}")
    @Transactional(rollbackFor = Exception.class)
    public String saveSaleCard(@PathVariable("merchantCode") String merchantCode,
                               @PathVariable("cardCode") String cardCode,
                               @RequestBody MerchantCardEditVo merchantCard) {
        merchantCard.setPrice(merchantCard.getPrice().multiply(new BigDecimal(100)));
        Assert.notNull(merchantCard.getReferencePrice(), "请输入市场价");
        merchantCard.setReferencePrice(merchantCard.getReferencePrice().multiply(new BigDecimal(100)));
        CardCards cards = cardCardsService.selectByCardCode(cardCode);
        Assert.notNull(cards, "卡号不存在");
        cardCardsService.updateById(cards);
        CardMapMerchantCards card = cardMapMerchantCardsService.getCard(merchantCode, cardCode);
        Assert.notNull(card, "卡号不存在");
        cardMapMerchantCardsService.createBatchMerchantCard(merchantCard, merchantCode, cardCode);
        return "保存成功";
    }

    /**
     * 电子券查询
     *
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/virtualCards/{merchantCode}")
    public IPage<MerchantUserCardVo> virtualCardList(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        IPage<MerchantUserCardVo> page = new Page<>(pageNo, pageSize);
        List<MerchantUserCardVo> list = cardMapMerchantCardsService.getUserCardList(merchantCode, page);
        page.setRecords(list);
        return page;
    }


    /**
     * 用户领券
     *
     * @param merchantCode
     * @param userFreeCard
     * @return
     */
    /**
     * 用户领券
     *
     * @param merchantCode
     * @param userFreeCard
     * @return
     */
    @PostMapping("/userCard/free/{merchantCode}")
    @Transactional(rollbackFor = RuntimeException.class)
    public String sendCardToUserFree(
            @PathVariable("merchantCode") String merchantCode,
            @Valid @RequestBody UserFreeCard userFreeCard) {
        cardMapMerchantCardsService.createUserFreeCards(userFreeCard, merchantCode);
        return "保存成功";
    }


    /**
     * 群发券列表
     *
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/cardDistributeList/send/{merchantCode}")
    public IPage<DistributeTrace> getCardDistributeList(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        IPage<DistributeTrace> page = new Page<>(pageNo, pageSize);
        List<DistributeTrace> list = distributeTraceService.getList(CardConstant.CARD_DISTRIBUTE_TYPE_SEND, merchantCode, page);
        page.setRecords(list);
        return page;
    }

    /**
     * 用户领券列表
     *
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/cardDistributeList/user/{merchantCode}")
    public IPage<DistributeTrace> getCardUserSendDistributeList(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        IPage<DistributeTrace> page = new Page<>(pageNo, pageSize);
        List<DistributeTrace> list = distributeTraceService.getList(CardConstant.CARD_DISTRIBUTE_TYPE_USER, merchantCode, page);
        page.setRecords(list);
        return page;
    }

    /**
     * pos发券列表
     *
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/cardDistributeList/pos/{merchantCode}")
    public IPage<DistributeTrace> getCardPosSendDistributeList(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        IPage<DistributeTrace> page = new Page<>(pageNo, pageSize);
        List<DistributeTrace> list = distributeTraceService.getList(CardConstant.CARD_DISTRIBUTE_TYPE_POS, merchantCode, page);
        page.setRecords(list);
        return page;
    }


    private boolean checkFile(MultipartFile file) {
        return true;
    }

}
