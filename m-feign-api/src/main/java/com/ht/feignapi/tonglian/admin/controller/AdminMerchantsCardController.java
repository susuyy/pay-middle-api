package com.ht.feignapi.tonglian.admin.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.mall.constant.CardUserMallConstant;
import com.ht.feignapi.mall.entity.Inventory;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.mall.service.CardMallCheckUseService;
import com.ht.feignapi.tonglian.admin.entity.MerchantCardEditVo;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.admin.entity.MerchantUserCardVo;
import com.ht.feignapi.tonglian.admin.entity.UserFreeCard;
import com.ht.feignapi.tonglian.admin.excel.entity.UserCardImportVo;
import com.ht.feignapi.tonglian.admin.excel.listener.UserCardListener;
import com.ht.feignapi.tonglian.card.clientservice.*;
import com.ht.feignapi.tonglian.card.entity.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.tonglian.card.service.CardLimitsService;
import com.ht.feignapi.tonglian.card.service.CardMapMerchantCardService;
import com.ht.feignapi.tonglian.card.service.CardUserService;
import com.ht.feignapi.tonglian.config.CardConstant;
import com.ht.feignapi.tonglian.config.CardType;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.service.MerchantPrimeService;
import com.ht.feignapi.tonglian.utils.RequestQrCodeDataStrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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
    private CardMapMerchantCardClientService merchantCardClientService;

    @Autowired
    private CardCardsClientService cardsClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private CardMapMerchantCardService cardMapMerchantCardService;

    @Autowired
    private CardLimitsService cardLimitsService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private CardMapUserCardsTraceClientService cardMapUserTraceClientService;


    @Autowired
    private CardMallCheckUseService cardMallCheckUseService;

    @Autowired
    private MerchantPrimeService merchantPrimeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InventoryClientService inventoryClientService;

    /**
     * 创建卡券--列表
     *
     * @param merchantCode 商户号
     * @param pageNo       第几页
     * @param pageSize     每页展示几条数据
     * @return 封装Result
     */
    @GetMapping("/{merchantCode}")
    public IPage<CardListVo> getCardList(
            @PathVariable("merchantCode") String merchantCode,
            CodeSearch cardSearch,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        Page<CardListVo> page = merchantCardClientService.getCardsByMerchantCode(merchantCode, cardSearch.getCardName()==null?"":cardSearch.getCardName(),
                cardSearch.getCardType()==null?"":cardSearch.getCardType(),cardSearch.getCardState()==null?"":cardSearch.getCardState(),pageNo,pageSize).getData();
        for (CardListVo record : page.getRecords()) {
            Merchants merchants = merchantsClientService.getMerchantByCode(record.getMerchantCode()).getData();
            record.setMerchantCode(merchants.getMerchantCode());
            record.setMerchantName(merchants.getMerchantName());
        }
        return page;
    }

    /**
     * 创建卡券--查看
     * @param merchantCode 商户号
     * @param cardCode     卡号
     * @return
     */
    @GetMapping("/info/{merchantCode}/{cardCode}")
    public CardCards getCard(@PathVariable("merchantCode") String merchantCode, @PathVariable("cardCode") String cardCode) {
        CardCards card = cardsClientService.getCardByCardCode(cardCode).getData();
        if (CardType.COUPON.getKey().equals(card.getType())) {
            card.setFaceValue(card.getFaceValue() / 100);
        }
        List<Merchants> merchants = cardMapMerchantCardService.getCardMerchants(merchantCode, cardCode);
        card.setMerchantList(merchants);
        return card;
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
        CardMapMerchantCards merchantCards = merchantCardClientService.queryByCardCodeAndMerchantCodeBatchCode(cardCode, merchantCode, batchCode).getData();
        Result<Integer> inventoryResult =inventoryClientService.getInventory(merchantCode,cardCode);
        Assert.notNull(inventoryResult, "获取库存失败");
        merchantCards.setInventory(inventoryResult.getData());
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
//    @PutMapping("/state/{merchantCode}/{cardCode}")
//    public String publishCard(
//            @PathVariable("merchantCode") String merchantCode,
//            @PathVariable("cardCode") String cardCode,
//            @RequestBody HashMap<String, String> map) {
//        Assert.isTrue(map.containsKey("state"), "参数有误");
//        Assert.isTrue(map.containsKey("batchCode"), "请填写批次号");
//        CardMapMerchantCards card = merchantCardClientService.queryByCardCodeAndMerchantCodeBatchCode(cardCode, merchantCode, map.get("batchCode"));
//        card.setOnSaleState(map.get("state"));
//        if (!merchantCardClientService.updateById(card)) {
//            return "保存失败";
//        }
//        return "保存成功";
//    }

    /**
     * 售券管理--删除
     *
     * @param merchantCode 商户号
     * @param cardCode     卡号
     * @param batchCode    批次号
     * @return
     */
//    @DeleteMapping("/{merchantCode}/{cardCode}/{batchCode}")
//    public String deleteCard(
//            @PathVariable("merchantCode") String merchantCode,
//            @PathVariable("cardCode") String cardCode,
//            @PathVariable("batchCode") String batchCode) {
//        CardMapMerchantCards card = merchantCardClientService.queryByCardCodeAndMerchantCodeBatchCode(cardCode, merchantCode, batchCode);
//        card.setState("disabled");
//        if (!merchantCardClientService.updateById(card)) {
//            return "删除失败";
//        }
//        return "删除成功";
//    }

    /**
     * 创建卡券--新建
     *
     * @param cardEditVo 优惠券模板信息
     * @return 保存结果
     */
//    @PostMapping
//    public String saveCard(@RequestBody @Valid CardEditVo cardEditVo) {
//        try {
//            merchantCardClientService.createMerchantCards(cardEditVo);
//            return "优惠券保存成功";
//        } catch (Exception e) {
//            logger.error("Admin_Card:优惠券保存失败。ExceptionMessage:" + e.getMessage() + "/br");
//            return "优惠券保存失败";
//        }
//    }

//    /**
//     * 创建卡券--更新卡状态，冻结，解冻
//     *
//     * @param cardCode 卡号
//     * @param map      state字段必填
//     * @return
//     */
//    @PutMapping("/state/{cardCode}")
//    public Result updateCardState(@PathVariable("cardCode") String cardCode, @RequestBody HashMap<String, String> map) {
//        Assert.isTrue(map.containsKey("state"), "参数有误");
//        CardCards card = cardsClientService.getCardByCode(cardCode);
//        card.setState(map.get("state"));
//        Boolean result = cardCardsService.updateById(card);
//        Assert.isTrue(result, "保存失败");
//        return ResultUtil.success("保存成功");
//    }

    /**
     * 创建卡券--更新卡券，目前只有更新notice功能。
     *
     * @param cardCode 卡号
     * @param map      notice
     * @return 保存结果
     */
//    @PutMapping("/{cardCode}")
//    public Result updateCard(@PathVariable("cardCode") String cardCode, @RequestBody HashMap<String, String> map) {
//        Assert.isTrue(map.containsKey("notice"), "参数有误");
//        Assert.isTrue(map.containsKey("cardName"), "参数有误");
//        CardCards card = cardCardsService.selectByCardCode(cardCode);
//        card.setNotice(map.get("notice"));
//        card.setCardName(map.get("cardName"));
//        Boolean result = cardCardsService.updateById(card);
//        Assert.isTrue(result, "保存失败");
//        return ResultUtil.success("保存成功");
//    }

    /**
     * 获取卡券类型下拉框
     *
     * @return 卡券类型
     */
//    @GetMapping("/cardType")
//    public Result getCardTypeList() {
//        Map<String, String> map = .getConstantMap(DbConstantGroupConfig.CARD_TYPE);
//        return ResultUtil.success(map);
//    }

    /**
     * 获取卡券状态
     *
     * @return 卡券状态列表
     */
//    @GetMapping("/cardValidState")
//    public Result getCardValidState() {
//        Map<String, String> map = dicConstantService.getConstantMap(DbConstantGroupConfig.CARD_STATE);
//        return ResultUtil.success(map);
//    }

    /**
     * 售券管理--列表
     *
     * @param merchantCode       商户号
     * @param merchantCardSearch 搜索信息
     * @param pageNo             页码
     * @param pageSize           页面展示数据条数
     * @return 商户下所有在售卡券信息
     */
    @GetMapping("/sale/{merchantCode}")
    public IPage<MerchantCardListVo> getMerchantSaleCard(@PathVariable("merchantCode") String merchantCode,
                                                         MerchantCardSearch merchantCardSearch,
                                                         @RequestParam(required = false, defaultValue = "0") Long pageNo,
                                                         @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        IPage<MerchantCardListVo> result = cardMapMerchantCardService.getMerchantCardListVos(merchantCode, merchantCardSearch, merchants,pageNo,pageSize);
        return result;
    }

    /**
     * pos端--验卡接口
     *
     * @param merchantCode 商户号
     * @param cardNo       卡号
     * @param userCardMsg  包含phone的请求体
     * @return
     */
//    @PostMapping("/checkCard/{merchantCode}/{cardNo}")
//    public Result checkCard(
//            @PathVariable("merchantCode") String merchantCode,
//            @PathVariable("cardNo") String cardNo,
//            @RequestBody HashMap<String, String> userCardMsg) {
////        Assert.isTrue(userCardMsg.containsKey("phone"), "请输入手机号");
//        CardMapUserCards cardMapUserCards = userCardsService.queryByCardNo(RequestQrCodeDataStrUtil.subStringQrCodeData(cardNo));
//        Assert.notNull(cardMapUserCards, "非法卡号");
//        Assert.isTrue(merchantCode.equals(cardMapUserCards.getMerchantCode()),"请在指定门店核销");
////        UsrUsers user = usrUsersService.getUserByPhoneOrOpenId(userCardMsg.get("phone"), merchantCode);
////        Assert.notNull(user, "非法手机号或商户号");
////        Assert.isTrue(cardMapUserCards.getUserId().equals(user.getId()), "非法手机号或商户号");
//        CardCards card = cardCardsService.selectByCardCode(cardMapUserCards.getCardCode());
//        Assert.isTrue(limitsService.checkCardUseLimit(cardMapUserCards.getCardCode(), merchantCode, cardMapUserCards.getUserId(), cardMapUserCards.getBatchCode()), "该卡不能使用");
//        Integer faceValue = Integer.parseInt(cardMapUserCards.getFaceValue());
//        if (("number").equals(card.getType())) {
//            if (faceValue > 0) {
//                cardMapUserCards.setFaceValue(String.valueOf(faceValue - 1));
//            } else {
//                return ResultUtil.error("计次卡次数已用完");
//            }
//        } else {
//            cardMapUserCards.setState(CardConstant.USER_CARD_STATE_USED);
//        }
//        userCardsService.updateById(cardMapUserCards);
//
//        //创建验券流水
//        CardMapUserCardsTrace cardMapUserCardsTrace = new CardMapUserCardsTrace();
//        cardMapUserCardsTrace.setUserId(cardMapUserCards.getUserId());
//        cardMapUserCardsTrace.setState("normal");
//        cardMapUserCardsTrace.setActionDate(new Date());
//        cardMapUserCardsTrace.setCardNo(cardMapUserCards.getCardNo());
//        cardMapUserCardsTrace.setCardCode(cardMapUserCards.getCardCode());
//        cardMapUserCardsTrace.setMerchantCode(merchantCode);
//        cardMapUserCardsTrace.setBatchCode(cardMapUserCards.getBatchCode());
//        cardMapUserCardsTrace.setCreateAt(new Date());
//        cardMapUserCardsTrace.setActionType("pos_use");
//        cardMapUserCardsTraceService.save(cardMapUserCardsTrace);
//
//        return ResultUtil.success("验卡成功");
//    }

    /**
     * pos端--验卡接口
     *
     * @param merchantCode 商户号
     * @param cardNo       卡号
     * @param userCardMsg  包含phone的请求体
     * @return
     */
//    @PostMapping("/checkQrCard/{merchantCode}/{cardNo}")
//    public Result checkQrCard(
//            @PathVariable("merchantCode") String merchantCode,
//            @PathVariable("cardNo") String cardNo,
//            @RequestBody HashMap<String, String> userCardMsg) {
////        Assert.isTrue(userCardMsg.containsKey("phone"), "请输入手机号");
//        CardMapUserCards cardMapUserCards = userCardsService.queryByCardNo(RequestQrCodeDataStrUtil.subStringQrCodeData(cardNo));
//        Assert.notNull(cardMapUserCards, "非法卡号");
////        UsrUsers user = usrUsersService.getUserByPhoneOrOpenId(userCardMsg.get("phone"), merchantCode);
////        Assert.notNull(user, "非法手机号或商户号");
////        Assert.isTrue(cardMapUserCards.getUserId().equals(user.getId()), "非法手机号或商户号");
//        CardCards card = cardCardsService.selectByCardCode(cardMapUserCards.getCardCode());
//        Assert.isTrue(limitsService.checkCardUseLimit(cardMapUserCards.getCardCode(), merchantCode, cardMapUserCards.getUserId(), cardMapUserCards.getBatchCode()), "该卡不能使用");
//        Integer faceValue = Integer.parseInt(cardMapUserCards.getFaceValue());
//        if (("number").equals(card.getType())) {
//            if (faceValue > 0) {
//                cardMapUserCards.setFaceValue(String.valueOf(faceValue - 1));
//            } else {
//                return ResultUtil.error("计次卡次数已用完");
//            }
//        } else {
//            cardMapUserCards.setState(CardConstant.USER_CARD_STATE_USED);
//        }
//        userCardsService.updateById(cardMapUserCards);
//        //创建验券流水
//        CardMapUserCardsTrace cardMapUserCardsTrace = new CardMapUserCardsTrace();
//        cardMapUserCardsTrace.setUserId(cardMapUserCards.getUserId());
//        cardMapUserCardsTrace.setState("normal");
//        cardMapUserCardsTrace.setActionDate(new Date());
//        cardMapUserCardsTrace.setCardNo(cardMapUserCards.getCardNo());
//        cardMapUserCardsTrace.setCardCode(cardMapUserCards.getCardCode());
//        cardMapUserCardsTrace.setMerchantCode(merchantCode);
//        cardMapUserCardsTrace.setBatchCode(cardMapUserCards.getBatchCode());
//        cardMapUserCardsTrace.setCreateAt(new Date());
//        cardMapUserCardsTrace.setActionType("pos_use");
//        cardMapUserCardsTraceService.save(cardMapUserCardsTrace);
//        return ResultUtil.success("验卡成功");
//    }

    /**
     * pos端--领券
     *
     * @param merchantCode
     * @param cardCode
     * @param phone        手机号
     * @return
     */
    @PostMapping("/posCard/{merchantCode}/{cardCode}")
    public String posSendCard(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardCode") String cardCode,
            @RequestBody HashMap<String, String> phone) {
        Assert.isTrue(phone.containsKey("phone"), "请输入手机号");
        Assert.isTrue(phone.containsKey("batchCode"), "请输入批次号");
        UserUsers user = merchantPrimeService.primeQueryUserByTelAndCode(phone.get("phone"), merchantCode);
        Assert.isTrue(cardLimitsService.checkCardGetLimit(cardCode, merchantCode, user.getId(), phone.get("batchCode")), "用户不满足领券条件");
        boolean result = cardMapUserClientService.posSendCard(merchantCode, cardCode, user.getId(), phone.get("batchCode")).getData();
        return result ? "发放成功" : "发放失败";
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
    public String saveSaleCard(@PathVariable("merchantCode") String merchantCode,
                               @PathVariable("cardCode") String cardCode,
                               @RequestBody MerchantCardEditVo merchantCard) {
        Map<String,Integer> inventory = new HashMap<>(1);
        inventory.put("amount",merchantCard.getInventory());
        Result<Inventory> inventoryResult = inventoryClientService.createProductionInventory(merchantCode, cardCode,inventory);
        Assert.notNull(inventoryResult,"创建库存出错");
        merchantCard.setBatchCode(inventoryResult.getData().getBatchCode());
        merchantCardClientService.saveSaleCard(merchantCode,cardCode,merchantCard);
        return "保存成功";
    }

    /**
     * 群发券--指定会员发券
     *
     * @param merchantCode
     * @param cardCode
     * @param userIds
     * @return
     */
    @PostMapping("/userCard/{merchantCode}/{cardCode}/{userIds}")
    @Transactional(rollbackFor = Exception.class)
    public String sendCardToUsers(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardCode") String cardCode,
            @PathVariable("userIds") List<Long> userIds,
            @RequestBody HashMap<String, String> admin) {
        Assert.isTrue(admin.containsKey("adminMerchantCode"), "请输入操作商户号");
        Merchants merchants = merchantsClientService.getMerchantByCode(admin.get("adminMerchantCode")).getData();
        UserUsers adminUsers = authClientService.getUserByIdTL(merchants.getUserId().toString()).getData();
        cardUserService.sendCardToUsers(merchantCode, cardCode, userIds, adminUsers);
        return "发券成功";
    }

    /**
     * 群发券--会员等级发卡
     * @param merchantCode
     * @param cardCode
     * @param adminMerchantCode
     * @param memberType
     * @return
     */
    @PostMapping("/memberCard/{merchantCode}/{cardCode}/{adminMerchantCode}")
    public String sendCardToMembers(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardCode") String cardCode,
            @PathVariable("adminMerchantCode") String adminMerchantCode,
            @RequestBody HashMap<String, List<String>> memberType) {
        Assert.isTrue(memberType.containsKey("memberType"), "参数有误");
        Merchants adminMerchants = merchantsClientService.getMerchantByCode(adminMerchantCode).getData();
        UserUsers adminUsers = authClientService.getUserByIdTL(adminMerchants.getUserId().toString()).getData();
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        List<String> memberTypes = memberType.get("memberType");
        String objectMerchantCode = merchants.getBusinessSubjects();
        memberTypes.forEach(e -> {
            cardUserService.sendCardToUsers(objectMerchantCode, cardCode, e, adminUsers, merchantCode);
        });
        return "发券成功";
    }

    /**
     * 群发券--导入文件发券
     * @param file
     * @param merchantCode
     * @param cardCode
     * @param adminMerchantCode
     * @return
     */
    @PostMapping("/userCard/{merchantCode}/{cardCode}/{adminMerchantCode}/import")
    public String sendCardToExcelUsers(
            @RequestParam("file") MultipartFile file,
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardCode") String cardCode,
            @PathVariable("adminMerchantCode") String adminMerchantCode) {
        Assert.isTrue(checkFile(file), "文件格式不正确！");
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(file.getInputStream(), UserCardImportVo.class,
                    new UserCardListener(cardUserService, merchantsClientService, authClientService, merchantCode, cardCode, adminMerchantCode)).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
            return "导入成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "导入失败";
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
    }

    /**
     * 电子券查询列表
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
        Page<MerchantUserCardVo> page = cardMapMerchantCardService.getUserCardList(merchantCode, pageNo, pageSize);
        return page;
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
        Map<String,Integer> map = new HashMap<>();
        map.put("amount",1);
        inventoryClientService.subtractInventory(merchantCode,cardCode,map);
        return cardMapUserClientService.posSendCard(merchantCode,cardCode,userId,batchCode).getData();
    }

    /**
     * 卡券管理--pos发券
     *
     * @param merchantCodes
     * @param posCardVo
     * @return
     */
//    @PostMapping("/posCards/{merchantCodes}")
//    public Result sendPosCard(
//            @PathVariable("merchantCodes") List<String> merchantCodes,
//            @RequestBody PosCardVo posCardVo) {
//        merchantCodes.forEach(e -> {
//            merchantCardClientService.savePosCard(e, posCardVo);
//        });
//        return ResultUtil.success("发卡成功");
//    }

    /**
     * pos发券--获取商户下的子商户（包括自己）
     *
     * @param merchantCode
     * @return
     */
    @GetMapping("/subMerchant/{merchantCode}")
    public List<Merchants> getSubMerchants(@PathVariable("merchantCode") String merchantCode) {
        Merchants merchant = merchantsClientService.getMerchantByCode(merchantCode).getData();
        Assert.notNull(merchant, "非法商户号");
        List<Merchants> merchantsList = new ArrayList<>();
        if ("OBJECT".equals(merchant.getType())) {
            merchantsList = merchantsClientService.getSubMerchants(merchantCode).getData();
            return merchantsList;
        }
        merchantsList.add(merchant);
        return merchantsList;
    }

    /**
     * 用户领券
     *
     * @param merchantCode
     * @param userFreeCard
     * @return
     */
    @PostMapping("/userCard/free/{merchantCode}")
    @Transactional(rollbackFor = RuntimeException.class)
    public Result sendCardToUserFree(
            @PathVariable("merchantCode") String merchantCode,
            @RequestBody UserFreeCard userFreeCard) {
        cardUserService.createUserFreeCards(userFreeCard, merchantCode);
        return Result.success("保存成功");
    }


    /**
     * 群发券列表
     *
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
//    @GetMapping("/cardDistributeList/send/{merchantCode}")
//    public Result getCardDistributeList(
//            @PathVariable("merchantCode") String merchantCode,
//            @RequestParam(required = false, defaultValue = "0") Long pageNo,
//            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
//        IPage<DistributeTrace> page = new Page<>(pageNo, pageSize);
//        List<DistributeTrace> list = distributeTraceService.getList(CardConstant.CARD_DISTRIBUTE_TYPE_SEND, merchantCode, page);
//        page.setRecords(list);
//        return ResultUtil.success(page);
//    }





    /**
     * pos发券列表
     */
//    @GetMapping("/cardDistributeList/pos/{merchantCode}")
//    public Result getCardPosSendDistributeList(
//            @PathVariable("merchantCode") String merchantCode,
//            @RequestParam(required = false, defaultValue = "0") Long pageNo,
//            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
//        IPage<DistributeTrace> page = new Page<>(pageNo, pageSize);
//        List<DistributeTrace> list = distributeTraceService.getList(CardConstant.CARD_DISTRIBUTE_TYPE_POS, merchantCode, page);
//        page.setRecords(list);
//        return ResultUtil.success(page);
//    }

//    @GetMapping("/cardCategory/{merchantCode}")
//    public Result getCardCategoryList( @PathVariable("merchantCode") String merchantCode){
//        List<String> allCategories = merchantCardClientService.getCategoryList(merchantCode);
//        return ResultUtil.success(allCategories);
//    }

//    @PostMapping("/cardCategory")
//    public Result saveCardCategory(@RequestBody CardCategorys cardCategorys){
//        cardCategorysService.save(cardCategorys);
//        return ResultUtil.success("保存成功");
//    }
    private boolean checkFile(MultipartFile file) {
        return true;
    }

    /**
     * pos端--获取商户下pos卡券
     *
     * @param merchantCode
     * @param cardName
     * @return
     */
    @PostMapping("/posCard/{merchantCode}")
    public Result<List<CardMapMerchantCards>> getPostCard(@PathVariable("merchantCode") String merchantCode, @RequestBody(required = false) HashMap<String, String> cardName) {
        Result<List<CardMapMerchantCards>> merchantCards = merchantCardClientService.getPostCard(merchantCode,cardName);
        Assert.notNull(merchantCards.getData(),"获取卡券列表出错");
        for (CardMapMerchantCards merchantCard:merchantCards.getData()) {
            Result<Integer> inventoryResult = inventoryClientService.getInventory(merchantCode,merchantCard.getCardCode());
            Assert.notNull(inventoryResult.getData(),"获取库存出错");
            merchantCard.setInventory(inventoryResult.getData());
        }
        return merchantCards;
    }

    /**
     * pos端--验卡接口
     *
     * @param merchantCode 商户号
     * @param cardNo       卡号
     * @return
     */
    @PostMapping("/checkCard/{merchantCode}/{cardNo}")
    public String checkCard(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardNo") String cardNo) {
        logger.info("**************验卡接口***********：卡号："+cardNo+"；商户号：" + merchantCode);
        Result<CardMapUserCards> cardsResult = cardMapUserClientService.getByCardNo(cardNo);
        logger.info("***************************cardResult",cardsResult);
        if (cardsResult!=null){
            CardMapUserCards cardMapUserCards = cardsResult.getData();
            System.out.println(cardMapUserCards);
            return getCheckCardResult(merchantCode, cardMapUserCards);
        }
        return "验卡失败";
    }

    /**
     * pos端--验卡接口
     *
     * @param merchantCode 商户号
     * @param cardNo       卡号
     * @return
     */
    @PostMapping("/checkQrCard/{merchantCode}/{cardNo}")
    public String checkQrCard(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("cardNo") String cardNo) {
        logger.info("**************验卡接口***********：卡号："+cardNo+"；商户号：" + merchantCode);
        Result<CardMapUserCards> cardsResult = cardMapUserClientService.getByCardNo(RequestQrCodeDataStrUtil.subStringQrCodeData(cardNo));
        if (cardsResult!=null){
            CardMapUserCards cardMapUserCards = cardsResult.getData();
            String qrAuthCode = RequestQrCodeDataStrUtil.subStringQrCodeAuthCode(cardNo);
            String redisAuthCode = stringRedisTemplate.opsForValue().get(cardMapUserCards.getCardNo());
            if (StringUtils.isEmpty(redisAuthCode) || !qrAuthCode.equals(redisAuthCode)){
                throw new CheckException(ResultTypeEnum.QR_CODE_INV);
            }
            return getCheckCardResult(merchantCode, cardMapUserCards);
        }
        return "验卡失败";
    }

    /**
     * 验卡使用
     * @param merchantCode
     * @param cardMapUserCards
     * @return
     */
    private String getCheckCardResult(String merchantCode, CardMapUserCards cardMapUserCards) {
        Assert.notNull(cardMapUserCards, "非法卡号");
        Assert.isTrue(merchantCode.equals(cardMapUserCards.getMerchantCode()), "请在指定门店核销");
        //商城验卡逻辑
        if (CardUserMallConstant.MALL_BUY_UN_USE_STATE.equals(cardMapUserCards.getState())
                ||CardUserMallConstant.MALL_FREE_UN_USE_STATE.equals(cardMapUserCards.getState())){
            return useMallCardStatement(cardMapUserCards,merchantCode,cardMapUserCards.getState());
        }
        CardCards card = cardsClientService.getCardByCardCode(cardMapUserCards.getCardCode()).getData();
        Assert.isTrue(cardLimitsService.checkCardUseLimit(cardMapUserCards.getCardCode(), merchantCode, cardMapUserCards.getUserId(), cardMapUserCards.getBatchCode()), "该卡不能使用");
        Integer faceValue = Integer.parseInt(cardMapUserCards.getFaceValue());
        if (("number").equals(card.getType())) {
            Assert.isTrue(faceValue > 0, "次数少于0次");
            cardMapUserCards.setFaceValue(String.valueOf(faceValue - 1));
            if (faceValue-1==0) {
               cardMapUserCards.setState(CardConstant.USER_CARD_STATE_USED);
            }
        }else {
           cardMapUserCards.setState(CardConstant.USER_CARD_STATE_USED);
        }
        cardMapUserClientService.saveOrUpdate(cardMapUserCards);

        //创建验券流水
        CardMapUserCardsTrace cardMapUserCardsTrace = new CardMapUserCardsTrace();
        cardMapUserCardsTrace.setUserId(cardMapUserCards.getUserId());
        cardMapUserCardsTrace.setState("normal");
        cardMapUserCardsTrace.setActionDate(new Date());
        cardMapUserCardsTrace.setCardNo(cardMapUserCards.getCardNo());
        cardMapUserCardsTrace.setCardCode(cardMapUserCards.getCardCode());
        cardMapUserCardsTrace.setMerchantCode(merchantCode);
        cardMapUserCardsTrace.setBatchCode(cardMapUserCards.getBatchCode());
        cardMapUserCardsTrace.setCreateAt(new Date());
        cardMapUserCardsTrace.setActionType("pos_use");
        cardMapUserTraceClientService.saveOrUpdateTrace(cardMapUserCardsTrace);
        return "验卡成功";
    }

    /**
     * 商城购买商品核销
     * @param cardMapUserCards
     * @param merchantCode
     * @return
     */
    private String useMallCardStatement(CardMapUserCards cardMapUserCards, String merchantCode,String state) {
        //校验卡券使用规则
//        boolean useLimit = cardLimitsService.checkCardUseLimit(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getUserId(), cardMapUserCards.getBatchCode());
        boolean useLimit = true;
        if (useLimit) {
            return cardMallCheckUseService.updateMallUsed(cardMapUserCards,state);
        }else {
            throw new CheckException(ResultTypeEnum.NOT_ALLOWED_USE);
        }
    }
}
