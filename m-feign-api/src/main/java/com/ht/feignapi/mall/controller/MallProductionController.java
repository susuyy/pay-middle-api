package com.ht.feignapi.mall.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.appconstant.CategoryConstant;
import com.ht.feignapi.mall.clientservice.*;
import com.ht.feignapi.mall.constant.MallConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.mall.service.CouponService;
import com.ht.feignapi.mall.service.InventoryService;
import com.ht.feignapi.mall.service.MallProductionService;
import com.ht.feignapi.mall.service.MerchantMallService;
import com.ht.feignapi.result.*;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.config.CardConstant;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.util.DateStrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: zheng weiguang
 * @Date: 2020/9/11 17:56
 */
@RequestMapping("/mall/production")
@RestController
public class MallProductionController {

    @Autowired
    private OrderProductionsClientService productionsClientService;

    private static final Logger logger = LoggerFactory.getLogger(MallProductionController.class);

    @Autowired
    private MallAppShowClientService mallAppShowClientService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderCategoriesClientService categoriesClientService;

    @Autowired
    private CardCardsClientService cardsClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MallProductionService mallProductionService;

    @Autowired
    private MerchantMallService merchantService;

    @Autowired
    private InventoryClientService inventoryClientService;

    @Autowired
    private CardMapMerchantCardClientService merchantCardClientService;

    @Autowired
    private MapMerchantPointsClientService pointsClientService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderCategoriesClientService orderCategoriesClientService;

    /**
     * 保存商品展示模板，商品从列表选择
     *
     * @param productions
     */
    @PutMapping("/onSaleState")
    public void changeOnSaleState(@RequestBody OrderProductions productions) {
        boolean cardProduction = mallProductionService.isCardProduction(productions.getCategoryCode(), productions.getMerchantCode());
        if (cardProduction) {
            Result<CardCards> cardsResult = cardsClientService.getCardByCardCode(productions.getProductionCode());
            if (cardsResult != null && cardsResult.getData() != null && cardsResult.getData() != null) {
                Result<CardMapMerchantCards> merchantCardsResult = merchantCardClientService.getMerchantCard(productions.getMerchantCode(), productions.getProductionCode());
                merchantCardsResult.getData().setOnSaleState(productions.getOnSaleState());
                merchantCardClientService.save(merchantCardsResult.getData());
            } else {
                throw new ProductionCodeNotExistException(ResultTypeEnum.PRODUCTION_CODE_NOT_EXIST, "卡券不存在");
            }
        } else {
            Result<OrderProductions> productionsResult = productionsClientService.getByCode(productions.getProductionCode(), productions.getMerchantCode());
            if (productionsResult != null && productionsResult.getData() != null && productionsResult.getData() != null) {
                productionsResult.getData().setOnSaleState(productions.getOnSaleState());
                productionsClientService.saveOrderProduction(productionsResult.getData());
            } else {
                throw new ProductionCodeNotExistException(ResultTypeEnum.PRODUCTION_CODE_NOT_EXIST, "产品不存在");
            }
        }
    }

    /**
     * 修改上架
     * id必传
     *
     * @param mallProductions
     */
    @PutMapping("/showProduction")
    public void updateProduction(@RequestBody MallProductions mallProductions) throws Exception {
        logger.info("修改上架:mallProductionId:******" + mallProductions.getId());
        Assert.notNull(mallProductions.getId(),"非法请求参数");
        Result<MallProductions> mallProductionsResult = mallAppShowClientService.getMallProduction(mallProductions.getId());
        Assert.isTrue(mallProductionsResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode())&&mallProductionsResult.getData()!=null,"获取展示商品出错");
        boolean cardProduction = mallProductionService.isCardProduction(mallProductionsResult.getData().getCategoryCode(), mallProductionsResult.getData().getMerchantCode());
        productionsClientService.removeInstruments(mallProductionsResult.getData().getProductionCode(),mallProductionsResult.getData().getMerchantCode());
        mallProductions.getInstruments().forEach(e->{
            OrderProductionsInstruction productionsInstruction = new OrderProductionsInstruction();
            productionsInstruction.setInstruction(e);
            productionsInstruction.setMerchantCode(mallProductionsResult.getData().getMerchantCode());
            productionsInstruction.setProductionCode(mallProductionsResult.getData().getProductionCode());
            productionsInstruction.setType(cardProduction?ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE:ProductionsCategoryConstant.OTHER_PRODUCTION);
            productionsClientService.saveOrderInstruments(productionsInstruction);
        });
        mallAppShowClientService.updateMallProduction(mallProductions);
    }

    /**
     * 保存商品展示模板，商品从列表选择
     *
     * @param mallProductions
     */
    @PostMapping("/showProduction")
    public void saveProduction(@RequestBody MallProductions mallProductions) {
        //如果上架了多个
        boolean cardProduction = mallProductionService.isCardProduction(mallProductions.getCategoryCode(), mallProductions.getMerchantCode());
//        Result<MallProductions> mallProductionsResult = mallAppShowClientService.getMallProductionByCode(mallProductions.getProductionCode(), "");
//        MallProductions mallProductionsSave = new MallProductions();
//        if (ResultTypeEnum.SERVICE_ERROR.getCode().equals(mallProductionsResult.getCode())) {
//            throw new ProductionCodeNotExistException(ResultTypeEnum.PRODUCTION_CODE_NOT_EXIST, "产品不存在");
//        }else if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(mallProductionsResult.getCode()) && mallProductionsResult.getData()==null){
//            //根据productionCode，查到一个production
//            //新增一个showProduction
//            mallProductionsResult.setData(new MallProductions());
//        }
        Result<MallTemplateHeader> mallTemplateHeaderResult = mallAppShowClientService.getMallTemplateHeader(mallProductions.getTemplateCode());
//        mallProductions.setShowCategoryCode(mallProductions.getShowCategoryCode());
        mallProductions.setMallCode(mallTemplateHeaderResult.getData().getMallCode());
//        mallProductions.setPrice(mallProductions.getPrice());
//        mallProductions.setProductionName(mallProductions.getProductionName());
//        mallProductions.setProductionUrl(mallProductions.getProductionUrl());
//        mallProductions.setAvgSaleAmountPerMonth(mallProductions.getAvgSaleAmountPerMonth());
//        mallProductions.setTotalSalesAmount(mallProductions.getTotalSalesAmount());
//        mallProductions.setInstruments(mallProductions.getInstruments());
//        mallProductions.setSortNum(mallProductions.getSortNum());
//        mallProductions.setDetail(mallProductions.getDetail());
        mallProductions.setState("1");
        Result<Merchants> merchantsResult = merchantsClientService.getMerchantByCode(mallProductions.getMerchantCode());
        Assert.isTrue(ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(merchantsResult.getCode()) && merchantsResult.getData() != null,"非法商户号");
        mallProductions.setMerchantName(merchantsResult.getData().getMerchantName());
        mallProductions.setMerchantPhone(merchantsResult.getData().getMerchantContact());
        mallProductions.setMerchantAddress(merchantsResult.getData().getLocation());
        if (cardProduction) {
            Result<CardCards> cardsResult = cardsClientService.getCardByCardCode(mallProductions.getProductionCode());
            if (cardsResult != null && cardsResult.getData() != null && cardsResult.getData() != null) {
                mallProductions.setValidTime(DateStrUtil.dateToStr(cardsResult.getData().getValidFrom()) +
                        "-" + DateStrUtil.dateToStr(cardsResult.getData().getValidTo()));
                Result<CardMapMerchantCards> merchantCardsResult = merchantCardClientService.getMerchantCard(mallProductions.getMerchantCode(), mallProductions.getProductionCode());
                merchantCardsResult.getData().setCategoryCode(mallProductions.getCategoryCode());
                merchantCardsResult.getData().setOnSaleState(CardConstant.MERCHANT_CARD_ON_SALE_STATE_Y);
                merchantCardsResult.getData().setOnSaleDate(mallProductions.getOnSaleDate());
                merchantCardsResult.getData().setHaltSaleDate(mallProductions.getHaltSaleDate());
                merchantCardClientService.save(merchantCardsResult.getData());
                mallProductions.setDiscountPrice(merchantCardsResult.getData().getPrice());
                mallProductions.setProductionName(cardsResult.getData().getCardName());
                mallProductions.setProductionUrl(cardsResult.getData().getCardPicUrl());
            } else {
                throw new ProductionCodeNotExistException(ResultTypeEnum.PRODUCTION_CODE_NOT_EXIST, "卡券不存在");
            }
        } else {
            Result<OrderProductions> productionsResult = productionsClientService.getByCode(mallProductions.getProductionCode(), mallProductions.getMerchantCode());
            if (productionsResult != null && productionsResult.getData() != null && productionsResult.getData() != null) {
                mallProductions.setCategoryCode(productionsResult.getData().getCategoryCode());
                productionsResult.getData().setOnSaleState(CardConstant.MERCHANT_CARD_ON_SALE_STATE_Y);
                productionsResult.getData().setValidFrom(mallProductions.getOnSaleDate());
                productionsResult.getData().setValidTo(mallProductions.getHaltSaleDate());
                productionsClientService.saveOrderProduction(productionsResult.getData());
                mallProductions.setValidTime(DateStrUtil.dateToStr(productionsResult.getData().getValidFrom()) +
                        "-" + DateStrUtil.dateToStr(productionsResult.getData().getValidTo()));
                mallProductions.setDiscountPrice(productionsResult.getData().getPrice());
                mallProductions.setProductionName(productionsResult.getData().getProductionName());
                mallProductions.setProductionUrl(productionsResult.getData().getProductionPicUrl());
            } else {
                throw new ProductionCodeNotExistException(ResultTypeEnum.PRODUCTION_CODE_NOT_EXIST, "产品不存在");
            }
        }
        productionsClientService.removeInstruments(mallProductions.getProductionCode(),mallProductions.getMerchantCode());
        mallProductions.getInstruments().forEach(e->{
            OrderProductionsInstruction productionsInstruction = new OrderProductionsInstruction();
            productionsInstruction.setInstruction(e);
            productionsInstruction.setMerchantCode(mallProductions.getMerchantCode());
            productionsInstruction.setProductionCode(mallProductions.getProductionCode());
            productionsInstruction.setType(cardProduction?ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE:ProductionsCategoryConstant.OTHER_PRODUCTION);
            productionsClientService.saveOrderInstruments(productionsInstruction);
        });
        if  (!CollectionUtils.isEmpty(mallProductions.getImages())){
            mallProductions.getImages().forEach(e->{
                e.setMallCode(mallTemplateHeaderResult.getData().getMallCode());
                e.setMallProductionCode(mallProductions.getProductionCode());
            });
        }
        mallAppShowClientService.saveMallProduction(mallProductions);
    }

    /**
     * 从mallProduction下架
     *
     * @param productionCode
     * @param showCategoryCode
     */
    @PutMapping("/halt/showProduction/{productionCode}/{showCategoryCode}")
    public Result<Boolean> haltProduction(
            @PathVariable("productionCode") String productionCode,
            @PathVariable("showCategoryCode") String showCategoryCode
    ) {
        Result<Boolean> result = mallAppShowClientService.deleteMallProduction(productionCode,showCategoryCode);
        if (result.getData()!=null && result.getData()){
            return Result.success();
        }
        return Result.error(ResultTypeEnum.SAVE_OR_UPDATE_FAIL);
    }

    /**
     * 保存产品信息
     *
     * @param productions
     */
    @PostMapping("/orderProductions")
    public void save(@RequestBody OrderProductions productions) throws Exception {
        try {
            boolean cardProduction = mallProductionService.isCardProduction(productions.getCategoryCode(), productions.getMerchantCode());
            Assert.isTrue(!(productions.getPoints() != null && productions.getPoints() > productions.getPrice()),"积分抵扣金额不得高于售卖价格");
            if (cardProduction) {
                productions.setProductionCode(CategoryConstant.CARDS + "_" + IdWorker.getIdStr());
            } else {
                productions.setProductionCode(CategoryConstant.PRODUCTION + "_" + IdWorker.getIdStr());
            }
            Inventory inventory = inventoryService.createInventory(productions.getMerchantCode(), productions.getProductionCode(), productions.getInventory());
            if (cardProduction) {
                mallProductionService.saveCardProduction(productions, inventory);
            } else {
                productions.setOnSaleState(CardConstant.MERCHANT_CARD_ON_SALE_STATE_N);
                productionsClientService.saveOrderProduction(productions);
            }
            merchantService.createProductionPoints(productions);
//            saveMallProduction(productions);
        } catch (Exception e) {
            throw new Exception("产品保存出错");
        }
    }

    /**
     * 获取商品信息
     * @param merchantCode
     * @param productionCode
     * @param categoryCode
     * @return
     */
    @GetMapping("/orderProductions/{merchantCode}/{productionCode}/{categoryCode}")
    public OrderProductions getProductionInfo(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("productionCode") String productionCode,
            @PathVariable("categoryCode") String categoryCode) {
        boolean cardProduction = mallProductionService.isCardProduction(categoryCode, merchantCode);
        OrderProductions productions = new OrderProductions();
        if (cardProduction) {
            Result<CardCards> cardCardsResult = cardsClientService.getCardByCardCode(productionCode);
            Result<CardMapMerchantCards> cardMapMerchantCardsResult = merchantCardClientService.getMerchantCard(merchantCode, productionCode);
            if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(cardCardsResult.getCode())
                    && cardCardsResult.getData() != null && ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(cardMapMerchantCardsResult.getCode())
                    && cardMapMerchantCardsResult.getData() != null) {
                productions = convertToProduction(cardMapMerchantCardsResult.getData(), cardCardsResult.getData());
            } else {
                throw new ProductionCodeNotExistException(ResultTypeEnum.PRODUCTION_CODE_NOT_EXIST, "产品不存在");
            }
        } else {
            Result<OrderProductions> orderProductionsResult = productionsClientService.getByCode(productionCode, productionCode);
            if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(orderProductionsResult.getCode()) && orderProductionsResult.getData() != null) {
                productions = orderProductionsResult.getData();
            }
        }
        productions.setInventory(inventoryClientService.getInventory(merchantCode, productionCode).getData());
        decorateProductionPoints(productions);
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        Result<OrderCategorys> categoriesResult = orderCategoriesClientService.queryLevelOneCode(categoryCode,merchants.getBusinessSubjects());
        Assert.notNull(categoriesResult,"获取商品分类出错!");
        Assert.notNull(categoriesResult.getData(),"获取商品分类出错!");
        productions.setCategoryLevel02Code(categoriesResult.getData().getCategoryLevel02Code());
        productions.setCategoryLevel02Name(categoriesResult.getData().getCategoryLevel02Name());
        productions.setCategoryLevel03Code(categoriesResult.getData().getCategoryLevel03Code());
        productions.setCategoryLevel03Name(categoriesResult.getData().getCategoryLevel03Name());
        return productions;
    }

    /**
     * 修改
     *
     * @param productions
     */
    @PutMapping("/orderProductions")
    public void update(@RequestBody OrderProductions productions) throws Exception {
        try {
            logger.info("修改orderProduction：" + JSON.toJSONString(productions));
            boolean cardProduction = mallProductionService.isCardProduction(productions.getCategoryCode(), productions.getMerchantCode());
            Assert.isTrue(!(productions.getPoints() != null && productions.getPoints() > productions.getPrice()),"积分抵扣金额不得高于售卖价格");
            Result<Integer> inventoryResult = inventoryClientService.getInventory(productions.getMerchantCode(), productions.getProductionCode());
            if (!(ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(inventoryResult.getCode())&&inventoryResult.getData()!=null)){
                throw new InventoryEmptyException("库存不存在，请检查商户号！");
            }
            if (productions.getInventory() > inventoryResult.getData()) {
                String batchCode = IdWorker.getIdStr();
                inventoryService.addInventory(productions.getMerchantCode(), productions.getProductionCode(), productions.getInventory() - inventoryResult.getData(),batchCode);
            } else {
                inventoryService.subtractInventory(productions.getMerchantCode(), productions.getProductionCode(), inventoryResult.getData() - productions.getInventory());
            }
            if (cardProduction) {
                mallProductionService.updateCardProduction(productions);
            } else {
                productions.setOnSaleState(CardConstant.MERCHANT_CARD_ON_SALE_STATE_N);
                productionsClientService.saveOrderProduction(productions);
            }
            merchantService.updatePoints(productions);
            saveMallProduction(productions);
        } catch (Exception e) {
            throw new Exception("产品保存出错");
        }
    }

    private void saveMallProduction(OrderProductions productions) {
        MallProductions mallProductions = new MallProductions();
        Result<List<MallProductions>> mallProductionsResult = mallAppShowClientService.getMallProductionList(productions.getProductionCode(), productions.getMerchantCode());
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(mallProductionsResult.getCode()) && !CollectionUtils.isEmpty(mallProductionsResult.getData())) {
            for (MallProductions productionsExist : mallProductionsResult.getData()) {
                copyValue(productions, productionsExist);
            }
            mallAppShowClientService.saveMallProductionsBatch(mallProductionsResult.getData());
        } else {
            copyValue(productions, mallProductions);
            mallAppShowClientService.saveMallProduction(mallProductions);
        }
    }

    private void copyValue(OrderProductions productions, MallProductions mallProductions) {
        mallProductions.setProductionCode(productions.getProductionCode());
        mallProductions.setProductionName(productions.getProductionName());
        mallProductions.setProductionUrl(productions.getProductionPicUrl());
        mallProductions.setMerchantCode(productions.getMerchantCode());
        mallProductions.setDetail(productions.getDetail());
        Result<Merchants> merchantsResult = merchantsClientService.getMerchantByCode(productions.getMerchantCode());
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(merchantsResult.getCode()) && merchantsResult.getData() != null) {
            mallProductions.setMerchantPhone(merchantsResult.getData().getMerchantContact());
            mallProductions.setMerchantName(merchantsResult.getData().getMerchantName());
            mallProductions.setMerchantAddress(merchantsResult.getData().getLocation());
        }
        mallProductions.setCategoryCode(productions.getCategoryCode());
    }

    /**
     * 展示主体下的商品
     *
     * @param objMerchantCode
     * @param pageNo
     * @param pageSize
     * @param productionName
     * @param categoryCode
     * @return
     */
    @GetMapping("/showProduction/{objMerchantCode}")
    public Result<Page<MallProductions>> getAllShowProductions(
            @PathVariable("objMerchantCode") String objMerchantCode,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") Long pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize,
            @RequestParam(value = "productionName", required = false, defaultValue = "") String productionName,
            @RequestParam(value = "categoryCode", required = false, defaultValue = "") String categoryCode) {
        Result<List<Merchants>> merchantsResult = merchantsClientService.getSubMerchants(objMerchantCode);
        Assert.isTrue(ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(merchantsResult.getCode()), "获取子商户数据错误");
        List<String> merchantCodes = merchantsResult.getData().stream().map(Merchants::getMerchantCode).collect(Collectors.toList());
        return mallAppShowClientService.getMerchantsAllProduction(merchantCodes, pageNo, pageSize, productionName, categoryCode);
    }

    /**
     * 获取商户所有产品信息
     *
     * @param adminMerchantCode
     * @param productionSearch
     * @return
     */
    @PostMapping("/orderProduction/{adminMerchantCode}")
    public IPage<OrderProductions> getOrderProductionPage(
            @PathVariable("adminMerchantCode") String adminMerchantCode,
            @RequestBody ProductionSearch productionSearch) {
        Result<List<Merchants>> subMerchantsResult = merchantsClientService.getSubMerchants(adminMerchantCode);
        if (subMerchantsResult != null && !CollectionUtils.isEmpty(subMerchantsResult.getData())) {
            List<Merchants> subMerchantList = subMerchantsResult.getData();
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(productionSearch.getType())) {
                Result<Page<CardMapMerchantCards>> cardsResult = merchantCardClientService.getMallSellCardObject(
                        subMerchantList.stream().map(Merchants::getMerchantCode).collect(Collectors.toList()),
                        productionSearch.getPageNo()==null?0:productionSearch.getPageNo(), productionSearch.getPageSize()==null?0:productionSearch.getPageSize(), productionSearch.getProductionName(), productionSearch.getProductionCode(),
                        OrderConstant.ALL_STATE);
                Page<OrderProductions> pageResult = new Page<>();
                pageResult.setCurrent(cardsResult.getData().getCurrent());
                pageResult.setSize(cardsResult.getData().getSize());
                pageResult.setTotal(cardsResult.getData().getTotal());
                pageResult.setPages(cardsResult.getData().getPages());
                pageResult.setRecords(getProductionVo(cardsResult.getData().getRecords()));
                return pageResult;
            } else {
                Result<Page<OrderProductions>> productionResult = productionsClientService.selectOrderProductionPage(
                        subMerchantList, productionSearch.getPageNo()==null?0:productionSearch.getPageNo(), productionSearch.getPageSize()==null?0:productionSearch.getPageSize(), productionSearch.getProductionName(),
                        productionSearch.getProductionCode(), OrderConstant.ALL_STATE);
                productionResult.getData().getRecords().forEach(e -> {
                    decorateProductionPoints(e);
                    decorateInventory(e);
                    mallProductionService.decorateProductionInstrument(e);
                });
                return productionResult.getData();
            }
        } else {
            return new Page<>();
        }
    }

    private List<OrderProductions> getProductionVo(List<CardMapMerchantCards> records) {
        List<OrderProductions> list = new ArrayList<>();
        records.forEach(e -> {
            Result<CardCards> cardCards = cardsClientService.getCardByCardCode(e.getCardCode());
            if (!(ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(cardCards.getCode())&&cardCards.getData()!=null)){
                throw new ProductionCodeNotExistException(ResultTypeEnum.PRODUCTION_CODE_NOT_EXIST,e.getCardCode()+":产品号不存在");
            }
            OrderProductions productions = convertToProduction(e, cardCards.getData());
            decorateProductionPoints(productions);
            decorateInventory(productions);
            mallProductionService.decorateProductionInstrument(productions);
            list.add(productions);
        });
        return list;
    }

    private void decorateInventory(OrderProductions productions) {
        Result<Integer> inventoryResult = inventoryClientService.getInventory(productions.getMerchantCode(), productions.getProductionCode());
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(inventoryResult.getCode()) && inventoryResult.getData() != null) {
            productions.setInventory(inventoryResult.getData());
        } else {
            productions.setInventory(0);
        }
    }

    private OrderProductions convertToProduction(CardMapMerchantCards e, CardCards cardCards) {
        OrderProductions productions = new OrderProductions();
        BeanUtils.copyProperties(cardCards, productions);
        productions.setOnSaleState(e.getOnSaleState());
        productions.setMerchantCode(e.getMerchantCode());
        productions.setPrice(e.getPrice());
        productions.setProductionCode(e.getCardCode());
        productions.setProductionName(e.getCardName());
        productions.setProductionPicUrl(cardCards.getCardPicUrl());
        return productions;
    }

    /**
     * 获取商品库存
     *
     * @param productionCode
     * @param storeMerchantCode
     * @return
     */
    @GetMapping("/queryInventory")
    public Map queryInventory(@RequestParam("productionCode") String productionCode, @RequestParam("storeMerchantCode") String storeMerchantCode) {
        Map map = new HashMap<>();
        try {
            Integer integer = inventoryClientService.getInventory(storeMerchantCode, productionCode).getData();
            if (integer > 0) {
                map.put("flag", true);
                map.put("count", integer);
            } else {
                map.put("flag", false);
                map.put("count", integer);
            }
        } catch (Exception e) {
            map.put("flag", false);
            map.put("count", 0);
        }
        return map;
    }

    /**
     * 获取主体下，所有商户出售的卡券
     *
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/{merchantCode}/type/mallSell")
    public Result<Page<CardMapMerchantCards>> getMallSellCard(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") Long pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        Result<List<Merchants>> listResult = merchantsClientService.getSubMerchants(merchantCode);
        Assert.notNull(listResult, "获取子商户出错");
        Assert.isTrue(!CollectionUtils.isEmpty(listResult.getData()), "获取子商户列表不能为空");
        List<String> codeList = listResult.getData().stream().map(Merchants::getMerchantCode).collect(Collectors.toList());
        Result<Page<CardMapMerchantCards>> result = merchantCardClientService.getMallSellCard(codeList, pageNo, pageSize);
        Assert.notNull(result, "获取子商户卡券出错");
        result.getData().getRecords().forEach(e -> {
            e.setInventory(inventoryClientService.getInventory(e.getMerchantCode(), e.getCardCode()).getData());
        });
        return result;
    }

    /**
     * 分页查询 mallProduction
     *
     * @param param
     * @param mallCode
     * @return
     * @throws ResultException
     */
    @PostMapping("selectByPage")
    public Page<MallProductions> selectByPage(@RequestBody Map<String, String> param, @RequestHeader("mallCode") String mallCode) throws ResultException {
        Result<Page<MallProductions>> productionPageResult = mallAppShowClientService.selectByPage(param, mallCode);
        Assert.notNull(productionPageResult, "获取产品列表信息出错!");
        Assert.notNull(productionPageResult.getData(), "获取产品列表信息出错!");
        List<MallProductions> productionList = productionPageResult.getData().getRecords();
        if (CollectionUtils.isEmpty(productionList)) {
            return new Page<>();
        }
        for (MallProductions production : productionList) {
            mallProductionService.parseEndDate(production);
            if (production.getSortNum().equals(MallConstant.SOLD_OUT)) {
                production.setState(MallConstant.SOLD_OUT_STR);
            }
            decorateProductionPoints(production);
        }
        return productionPageResult.getData();
    }

    private void decorateProductionPoints(OrderProductions production) {
        Result<MrcPrimeDiscountPoints> discountPointsResult = pointsClientService.queryPrimeDiscountPoints(production.getMerchantCode(), production.getProductionCode());
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(discountPointsResult.getCode()) &&
                discountPointsResult.getData() != null) {
            production.setPoints(discountPointsResult.getData().getPoints());
            production.setLimitAmountPerOrder(discountPointsResult.getData().getLimitAmountPerOrder());
            production.setLimitAmountTotal(discountPointsResult.getData().getLimitAmountTotal());
        } else {
            production.setPoints(0);
            production.setLimitAmountPerOrder(0);
            production.setLimitAmountTotal(0);
        }
    }

    private void decorateProductionPoints(MallProductions production) {
        Result<MrcPrimeDiscountPoints> discountPointsResult = pointsClientService.queryPrimeDiscountPoints(production.getMerchantCode(), production.getProductionCode());
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(discountPointsResult.getCode()) &&
                discountPointsResult.getData() != null) {
            production.setDiscountPoints(discountPointsResult.getData().getPoints());
        } else {
            production.setDiscountPoints(0);
        }
    }

    /**
     * 根据id 获取商城商品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public MallProductions getMallProduction(@PathVariable("id") Long id) {
        Result<MallProductions> productionResult = mallAppShowClientService.getMallProduction(id);
        Assert.notNull(productionResult.getData(), "无对应产品信息");
        mallProductionService.decorateProductionInstrument(productionResult.getData());
        mallProductionService.parseEndDate(productionResult.getData());
        decorateProductionPoints(productionResult.getData());
        return productionResult.getData();
    }

    /**
     * 支付页展示 商品积分明细
     *
     * @param requestProductPointsData
     * @return
     */
    @PostMapping("/queryPointDetail")
    public ReturnProductPointsData queryPointDetail(@RequestBody RequestProductPointsData requestProductPointsData) {
        List<ProductPointsData> productPointsDataList = requestProductPointsData.getProductPointsDataList();
        ShowProductPointsData showProductPointsData = couponService.showPayPointsData(productPointsDataList, requestProductPointsData.getObjectMerchantCode(),
                requestProductPointsData.getOpenId());
        if (showProductPointsData.getPrimesId() == -1L) {
            ReturnProductPointsData returnProductPointsData = new ReturnProductPointsData();
            returnProductPointsData.setTotalUsePoints(0);
            returnProductPointsData.setTotalReduceMoney(0);
            returnProductPointsData.setProductPointsMoneyDetailList(new ArrayList<>());
            returnProductPointsData.setUserPoints(0);
            returnProductPointsData.setAfterUserPoints(0);
            return returnProductPointsData;
        }

        ReturnProductPointsData returnProductPointsData = new ReturnProductPointsData();
        Map<String, Integer> productionPointsMap = showProductPointsData.getProductionPointsMap();
        List<ProductPointsMoneyDetail> productPointsMoneyDetailList = new ArrayList<>();
        Set<String> set = productionPointsMap.keySet();
        for (String productionCode : set) {
            List<ProductPointsData> collect = productPointsDataList.stream().filter(productPointsData -> productPointsData.getProductionCode().equals(productionCode)).collect(Collectors.toList());
            if (collect.size() > 0) {
                ProductPointsData productPointsData = collect.get(0);
                ProductPointsMoneyDetail productPointsMoneyDetail = new ProductPointsMoneyDetail();
                productPointsMoneyDetail.setProductionCode(productPointsData.getProductionCode());
                productPointsMoneyDetail.setProductionName(productPointsData.getProductionName());
                productPointsMoneyDetail.setUsePoints(productionPointsMap.get(productionCode));
                // 目前积分和金额比例 1:1(分)不需要额外扣除金额计算
                productPointsMoneyDetail.setReduceMoney(productionPointsMap.get(productionCode));
                productPointsMoneyDetailList.add(productPointsMoneyDetail);
            }
        }
        returnProductPointsData.setTotalUsePoints(showProductPointsData.getUsePoints());
        // 目前积分和金额比例 1:1(分)不需要额外扣除金额计算
        returnProductPointsData.setTotalReduceMoney(showProductPointsData.getUsePoints());
        returnProductPointsData.setProductPointsMoneyDetailList(productPointsMoneyDetailList);
        returnProductPointsData.setUserPoints(showProductPointsData.getUserPoints());
        returnProductPointsData.setAfterUserPoints(showProductPointsData.getUserPoints() - showProductPointsData.getUsePoints());
        return returnProductPointsData;
    }
}
