package com.ht.feignapi.mall.controller;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.appconstant.CategoryConstant;
import com.ht.feignapi.mall.clientservice.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.constant.CardUserMallConstant;
import com.ht.feignapi.mall.constant.MerchantCardConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.mall.entity.vo.ObjectIncomeSearch;
import com.ht.feignapi.mall.entity.vo.SalesVolume;
import com.ht.feignapi.mall.service.*;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card. clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mall/order")
@CrossOrigin(allowCredentials = "true")
public class MallOrderController {

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private OrderProductionsClientService orderProductionsClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MallOrderPayService mallOrderPayService;

    @Autowired
    private CardMapMerchantCardClientService cardMapMerchantCardClientService;

    @Autowired
    private OrderCategoriesClientService orderCategoriesClientService;

    @Autowired
    private CardCardsClientService cardCardsClientService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private MallAppShowClientService mallAppShowClientService;

    @Autowired
    private OrderWayBillsService orderWayBillsService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private MallProductionService mallProductionService;

    @Autowired
    private ShoppingCartClientService shoppingCartClientService;

    @Autowired
    private OrderCategorysServeice orderCategorysServeice;



    /**
     * 商品详情页 直接下单购买
     *
     * @param mallUnionOrderData
     * @return
     */
    @PostMapping("/mallUnionOrder")
    public RetUnionOrderData mallUnionOrder(@RequestBody MallUnionOrderData mallUnionOrderData) {

        //校验绑定手机
        UserUsers userUsers = authClientService.queryByOpenid(mallUnionOrderData.getOpenId()).getData();
        mallUnionOrderData.setUserId(userUsers.getId());

        //校验商品是否过期
        MallProductions mallProductions = new MallProductions();
        mallProductions.setProductionCode(mallUnionOrderData.getProductionCode());
        mallProductions.setMerchantCode(mallUnionOrderData.getStoreMerchantCode());
        mallProductions.setCategoryCode(mallUnionOrderData.getCategoryCode());
        mallProductionService.parseEndDate(mallProductions);
        boolean before = mallProductions.getEndDate().before(new Date());
        if (before){
            throw new CheckException(ResultTypeEnum.PRODUCTION_OVERDUE);
        }

        //校验库存
        boolean have = inventoryService.checkProductionInventory(mallUnionOrderData.getProductionCode(), mallUnionOrderData.getStoreMerchantCode(), mallUnionOrderData.getQuantity());
        if (have) {
            OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(mallUnionOrderData.getCategoryCode(), mallUnionOrderData.getStoreMerchantCode()).getData();
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                //卡券类商品
                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.mallQueryCodeMerchantCodeType(mallUnionOrderData.getProductionCode(),
                        mallUnionOrderData.getStoreMerchantCode(),
                        MerchantCardConstant.MALL_SELL_TYPE).getData();
                mallUnionOrderData.setCardMapMerchantCards(cardMapMerchantCards);
                mallUnionOrderData.setCategoryLevel01Code(orderCategorys.getCategoryLevel01Code());
                mallUnionOrderData.setDiscount(0);
                RetUnionOrderData retUnionOrderData = mallOrderClientService.saveOrderAllData(mallUnionOrderData).getData();
                retUnionOrderData.setToBindTel(false);
                return retUnionOrderData;
            } else {
                //实体类商品
                mallUnionOrderData.setCategoryLevel01Code(orderCategorys.getCategoryLevel01Code());
                mallUnionOrderData.setDiscount(0);
                RetUnionOrderData retUnionOrderData = mallOrderClientService.saveOrderAllData(mallUnionOrderData).getData();
                retUnionOrderData.setToBindTel(false);
                return retUnionOrderData;
            }
        } else {
            throw new CheckException(ResultTypeEnum.NOT_INVENTORY);
        }
    }

    /**
     * 购物车 结算
     * @param shoppingCartUnionOrderData
     * @return
     */
    @PostMapping("/shoppingCartUnionOrder")
    public RetUnionOrderData shoppingCartUnionOrder(@RequestBody ShoppingCartUnionOrderData shoppingCartUnionOrderData) {
        List<ShowShoppingCartDate> showShoppingCartDates = shoppingCartClientService.queryByOrderCodeList(shoppingCartUnionOrderData.getShoppingCartOrderCodeList()).getData();
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDates) {
            //校验商品是否过期
            MallProductions mallProductions = new MallProductions();
            mallProductions.setProductionCode(showShoppingCartDate.getProductionCode());
            mallProductions.setMerchantCode(showShoppingCartDate.getMerchantCode());
            mallProductions.setCategoryCode(showShoppingCartDate.getProductionCategoryCode());
            mallProductionService.parseEndDate(mallProductions);
            boolean before = mallProductions.getEndDate().before(new Date());
            if (before){
                throw new CheckException(ResultTypeEnum.PRODUCTION_OVERDUE);
            }
        }
        UserUsers userUsers = authClientService.queryByOpenid(shoppingCartUnionOrderData.getOpenId()).getData();
        shoppingCartUnionOrderData.setUserId(userUsers.getId().toString());
        Merchants merchants = merchantsClientService.getMerchantByCode(shoppingCartUnionOrderData.getObjectMerchantCode()).getData();
        shoppingCartUnionOrderData.setMerchantChargeType(merchants.getChargeType());
        RetUnionOrderData retUnionOrderData = mallOrderPayService.shoppingCartUnionOrder(shoppingCartUnionOrderData);
        return retUnionOrderData;
    }


    /**
     * 分页展示用户订单 按商户分组 分页  (展示订单明细)  订单页 待使用状态 接口 (订单维度,暂不用)
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/showMyOrder")
    public Page<OrderOrderDetails> showMyOrder(@RequestBody ShowMyOrderData showMyOrderData) {
        UserUsers userUsers = authClientService.queryByOpenid(showMyOrderData.getOpenId()).getData();
        showMyOrderData.setUserId(userUsers.getId());
        Page<OrderOrderDetails> orderDetailsPage = mallOrderClientService.queryMyOrderDetail(showMyOrderData).getData();
        List<OrderOrderDetails> records = orderDetailsPage.getRecords();
        ArrayList<OrderOrderDetails> list = new ArrayList<>();
        for (OrderOrderDetails orderOrderDetails : records) {
            Merchants merchants = merchantsClientService.getMerchantByCode(orderOrderDetails.getMerchantCode()).getData();
            if (merchants != null) {
                orderOrderDetails.setMerchantName(merchants.getMerchantName());
            }
            OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(orderOrderDetails.getProductionCategoryCode(),
                    orderOrderDetails.getMerchantCode()).getData();
            orderOrderDetails.setOneLevelCategoryCode(orderCategorys.getCategoryLevel01Code());
            //封装商品图片
            List<String> picUrlList = new ArrayList<>();
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                CardCards cardCards = cardCardsClientService.getCardByCardCode(orderOrderDetails.getProductionCode()).getData();
                orderOrderDetails.setProductionUrl(cardCards.getCardPicUrl());
                picUrlList.add(cardCards.getCardPicUrl());
                //获取展示的 商品id
                List<MallProductions> mallProductionsList = mallAppShowClientService.queryByCodeAndMerchant(cardCards.getCardCode(), orderOrderDetails.getMerchantCode()).getData();
                if (mallProductionsList!=null && mallProductionsList.size()>0) {
                    orderOrderDetails.setMallProductionsId(mallProductionsList.get(0).getId().toString());
                }
                orderOrderDetails.setComments(orderOrderDetails.getProductionName());
            } else {
                list.add(orderOrderDetails);
            }
            orderOrderDetails.setProductionPicUrlList(picUrlList);
        }
        //移除实体类商品
        for (OrderOrderDetails orderOrderDetails : list) {
            records.remove(orderOrderDetails);
        }
        return orderDetailsPage;
    }

    /**
     * 获取更多 某个商家下的 订单 数据(展示订单明细) 订单页 待使用状态 接口 (暂不用)
     *
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/moreMyOrder")
    public Page<OrderOrderDetails> moreMyOrder(@RequestBody ShowMyOrderData showMyOrderData) {
        UserUsers userUsers = authClientService.queryByOpenid(showMyOrderData.getOpenId()).getData();
        showMyOrderData.setUserId(userUsers.getId());
        Page<OrderOrderDetails> orderOrderDetailsPage = mallOrderClientService.moreMyOrder(showMyOrderData).getData();
        List<OrderOrderDetails> records = orderOrderDetailsPage.getRecords();
        for (OrderOrderDetails orderOrderDetails : records) {
            OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(orderOrderDetails.getProductionCategoryCode(),
                    orderOrderDetails.getMerchantCode()).getData();
            orderOrderDetails.setOneLevelCategoryCode(orderCategorys.getCategoryLevel01Code());
            //封装商品图片
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                CardCards cardCards = cardCardsClientService.getCardByCardCode(orderOrderDetails.getProductionCode()).getData();
                orderOrderDetails.setProductionUrl(cardCards.getCardPicUrl());
            } else {
//                records.remove(orderOrderDetails);
                OrderProductions orderProductions = orderProductionsClientService.getByCode(orderOrderDetails.getProductionCode(), orderOrderDetails.getMerchantCode()).getData();
                orderOrderDetails.setProductionUrl(orderProductions.getProductionPicUrl());
            }
        }
        return orderOrderDetailsPage;
    }

    /**
     * 根据订单号 查询商城订单 (服务间调用 前端无使用)
     *
     * @param orderCode
     * @return
     */
    @GetMapping("/getByOrderCode")
    public OrderOrders getByOrderCode(@RequestParam("orderCode") String orderCode) {
        return mallOrderClientService.queryOrderByOrderCode(orderCode).getData();
    }

    /**
     * 根据orderCode 和 id 查询明细表 查看商品券码功能 (订单维度 暂不用)
     *
     * @param orderCode
     * @param id
     * @return
     */
    @GetMapping("/orderQrCodeDetail")
    public RetOrderQrCodeDetailData orderQrCodeDetail(@RequestParam("orderCode") String orderCode, @RequestParam("id") String id) {
        RetOrderQrCodeDetailData retOrderQrCodeDetailData = mallOrderPayService.orderQrCodeDetail(orderCode, id);
        return retOrderQrCodeDetailData;
    }

    /**
     * 用户商城购买卡券 展示商品券码
     * @param orderCode
     * @param id
     * @param mapUserCardNo
     * @return
     */
    @GetMapping("/buyCardQrCodeDetail")
    public RetOrderQrCodeDetailData buyQrCodeDetail(@RequestParam("orderCode") String orderCode,
                                                    @RequestParam("id") String id,
                                                    @RequestParam("mapUserCardNo")String mapUserCardNo) {
        RetOrderQrCodeDetailData retOrderQrCodeDetailData = mallOrderPayService.buyQrCodeDetail(orderCode, id,mapUserCardNo);
        return retOrderQrCodeDetailData;
    }


    /**
     * 分页展示用户订单 分页
     *
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/showMyOrderMaster")
    public Page<OrderOrders> showMyOrderMaster(@RequestBody ShowMyOrderData showMyOrderData) {
        UserUsers userUsers = authClientService.queryByOpenid(showMyOrderData.getOpenId()).getData();
        showMyOrderData.setUserId(userUsers.getId());
        Page<OrderOrders> orderOrdersPage = mallOrderClientService.queryMyOrderMaster(showMyOrderData).getData();
        List<OrderOrders> records = orderOrdersPage.getRecords();
        for (OrderOrders record : records) {
            Merchants merchants = merchantsClientService.getMerchantByCode(record.getMerchantCode()).getData();
            if (merchants != null) {
                record.setMerchantName(merchants.getMerchantName());
            }
            List<OrderOrderDetails> orderOrderDetailsList = mallOrderClientService.queryDetailByOrderCode(record.getOrderCode()).getData();
            List<String> picUrlList = new ArrayList<>();
            for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
                OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(orderOrderDetails.getProductionCategoryCode(),
                        orderOrderDetails.getMerchantCode()).getData();
                orderOrderDetails.setOneLevelCategoryCode(orderCategorys.getCategoryLevel01Code());
                //封装商品图片
                if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                    CardCards cardCards = cardCardsClientService.getCardByCardCode(orderOrderDetails.getProductionCode()).getData();
                    if (cardCards==null){
                        picUrlList.add("");
                    }else {
                        picUrlList.add(cardCards.getCardPicUrl());
                    }
                } else {
                    OrderProductions orderProductions = orderProductionsClientService.getByCode(orderOrderDetails.getProductionCode(), orderOrderDetails.getMerchantCode()).getData();
                    if (orderProductions==null){
                        picUrlList.add("");
                    }else {
                        picUrlList.add(orderProductions.getProductionPicUrl());
                    }
                }
            }
            record.setProductionPicUrlList(picUrlList);
        }
        return orderOrdersPage;
    }

    /**
     * 获取更多 某个商家下的 订单 数据 (暂不用)
     *
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/moreMyOrderMaster")
    public Page<OrderOrders> moreMyOrderMaster(@RequestBody ShowMyOrderData showMyOrderData) {
        UserUsers userUsers = authClientService.queryByOpenid(showMyOrderData.getOpenId()).getData();
        showMyOrderData.setUserId(userUsers.getId());
        Page<OrderOrders> orderOrdersPage = mallOrderClientService.moreMyOrderMaster(showMyOrderData).getData();
        List<OrderOrders> records = orderOrdersPage.getRecords();
        for (OrderOrders orderOrders : records) {
            List<OrderOrderDetails> orderOrderDetailsList = mallOrderClientService.queryDetailByOrderCode(orderOrders.getOrderCode()).getData();
            List<String> picUrlList = new ArrayList<>();
            for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
                OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(orderOrderDetails.getProductionCategoryCode(),
                        orderOrderDetails.getMerchantCode()).getData();
                orderOrderDetails.setOneLevelCategoryCode(orderCategorys.getCategoryLevel01Code());
                //封装商品图片
                if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                    CardCards cardCards = cardCardsClientService.getCardByCardCode(orderOrderDetails.getProductionCode()).getData();
                    picUrlList.add(cardCards.getCardPicUrl());
                } else {
                    OrderProductions orderProductions = orderProductionsClientService.getByCode(orderOrderDetails.getProductionCode(), orderOrderDetails.getMerchantCode()).getData();
                    picUrlList.add(orderProductions.getProductionPicUrl());
                }
            }
            orderOrders.setProductionPicUrlList(picUrlList);
        }
        return orderOrdersPage;
    }

    /**
     * 根据订单编码 获取订单明细集合
     *
     * @param orderCode
     * @return
     */
    @GetMapping("/queryDetailByOrderCode")
    public List<OrderOrderDetails> queryDetailByOrderCode(@RequestParam("orderCode") String orderCode) {
        List<OrderOrderDetails> orderDetailsList = mallOrderClientService.queryDetailByOrderCode(orderCode).getData();
        for (OrderOrderDetails orderOrderDetails : orderDetailsList) {
            OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(orderOrderDetails.getProductionCategoryCode(),
                    orderOrderDetails.getMerchantCode()).getData();
            orderOrderDetails.setOneLevelCategoryCode(orderCategorys.getCategoryLevel01Code());
            //封装商品图片
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                CardCards cardCards = cardCardsClientService.getCardByCardCode(orderOrderDetails.getProductionCode()).getData();
                orderOrderDetails.setProductionUrl(cardCards.getCardPicUrl());
            } else {
                OrderProductions orderProductions = orderProductionsClientService.getByCode(orderOrderDetails.getProductionCode(), orderOrderDetails.getMerchantCode()).getData();
                orderOrderDetails.setProductionUrl(orderProductions.getProductionPicUrl());
            }
        }
        return orderDetailsList;
    }

    /**
     * 分页展示用户购买卡券
     *
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/showMyBuyCard")
    public Page<OrderOrderDetails> showMyCard(@RequestBody ShowMyOrderData showMyOrderData) {
        UserUsers userUsers = authClientService.queryByOpenid(showMyOrderData.getOpenId()).getData();
        String state = showMyOrderData.getState();
        if (OrderConstant.PAID_UN_USE_STATE.equals(state)){
            state = CardUserMallConstant.MALL_BUY_UN_USE_STATE;
        }else {
            state = CardUserMallConstant.USED;
        }
        Page<CardMapUserCards> mapUserCardsPage = cardMapUserClientService.queryUserBuyCardList(userUsers.getId(), state, CardUserMallConstant.MALL_BUY_TYPE, showMyOrderData.getPageNo(), showMyOrderData.getPageSize()).getData();
        //封装分页数据
        Page<OrderOrderDetails> orderDetailsPage = new Page<>();
        orderDetailsPage.setCurrent(mapUserCardsPage.getCurrent());
        orderDetailsPage.setSize(mapUserCardsPage.getSize());
        orderDetailsPage.setTotal(mapUserCardsPage.getTotal());
        orderDetailsPage.setPages(mapUserCardsPage.getPages());

        //封装内容集合数据
        List<CardMapUserCards> mapUserCardRecords = mapUserCardsPage.getRecords();
        List<OrderOrderDetails> orderDetailsList =new ArrayList<>();

        for (CardMapUserCards mapUserCardRecord : mapUserCardRecords) {
            OrderOrderDetails orderOrderDetails = mallOrderClientService.queryDetailById(mapUserCardRecord.getRefSourceKey()).getData();
            Merchants merchants = merchantsClientService.getMerchantByCode(mapUserCardRecord.getMerchantCode()).getData();
            if (merchants != null) {
                orderOrderDetails.setMerchantName(merchants.getMerchantName());
            }else {
                orderOrderDetails.setMerchantName("商户数据丢失");
            }
            orderOrderDetails.setAmount(orderOrderDetails.getAmount()/orderOrderDetails.getQuantity().intValue());
            orderOrderDetails.setProductionCode(mapUserCardRecord.getCardCode());
            orderOrderDetails.setProductionName(mapUserCardRecord.getCardName());
            orderOrderDetails.setProductionCategoryCode(mapUserCardRecord.getCategoryCode());
            orderOrderDetails.setProductionCategoryName(mapUserCardRecord.getCategoryName());
            orderOrderDetails.setDiscount(orderOrderDetails.getDiscount()/orderOrderDetails.getQuantity().intValue());
            orderOrderDetails.setOneLevelCategoryCode(CategoryConstant.CARDS);
            orderOrderDetails.setComments(mapUserCardRecord.getCardName());

            //获取展示的 商品id
            List<MallProductions> mallProductionsList = mallAppShowClientService.queryByCodeAndMerchant(mapUserCardRecord.getCardCode(), orderOrderDetails.getMerchantCode()).getData();
            List<String> picUrlList = new ArrayList<>();
            if (mallProductionsList!=null && mallProductionsList.size()>0) {
                orderOrderDetails.setMallProductionsId(mallProductionsList.get(0).getId().toString());
                picUrlList.add(mallProductionsList.get(0).getProductionUrl());
                orderOrderDetails.setProductionPicUrlList(picUrlList);
                orderOrderDetails.setProductionUrl(mallProductionsList.get(0).getProductionUrl());
            }else {
                CardCards cardCards = cardCardsClientService.getCardByCardCode(mapUserCardRecord.getCardCode()).getData();
                picUrlList.add(cardCards.getCardPicUrl());
                orderOrderDetails.setProductionPicUrlList(picUrlList);
                orderOrderDetails.setProductionUrl(cardCards.getCardPicUrl());
            }
            orderOrderDetails.setMapUserCardId(mapUserCardRecord.getId());
            orderOrderDetails.setMapUserCardNo(mapUserCardRecord.getCardNo());
            orderOrderDetails.setQuantity(new BigDecimal("1"));
            orderDetailsList.add(orderOrderDetails);
        }
        orderDetailsPage.setRecords(orderDetailsList);
        return orderDetailsPage;
    }

    /**
     * 获取所有主体的收入统计
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/objectIncome")
    public Result<List<SalesVolume>> getObjectIncome(
            @RequestParam(value = "beginDate",defaultValue = "2020-01-01")  @DateTimeFormat(pattern="yyyy-MM-dd") Date beginDate,
            @RequestParam(value = "endDate",defaultValue = "2099-12-31")  @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate){
        ObjectIncomeSearch objectIncomeSearch = new ObjectIncomeSearch();
        objectIncomeSearch.setBeginDate(beginDate);
        objectIncomeSearch.setEndDate(endDate);
        objectIncomeSearch.setMerchantCodes(merchantsClientService.getObjMerchantCodes().getData());
        Result<List<SalesVolume>> result = mallOrderClientService.getMerchantSalesVolume(objectIncomeSearch);
        decorateMerchantName(result);
        return result;
    }


    /**
     * 获取主体的子商户收入统计
     * @param objMerchantCode
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/{objMerchantCode}/subjectIncome")
    public Result<List<SalesVolume>> getObjectIncome(
            @PathVariable("objMerchantCode") String objMerchantCode,
            @RequestParam(value = "beginDate",defaultValue = "2020-01-01")  @DateTimeFormat(pattern="yyyy-MM-dd") Date beginDate,
            @RequestParam(value = "endDate",defaultValue = "2099-12-31")  @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate){
        ObjectIncomeSearch objectIncomeSearch = new ObjectIncomeSearch();
        objectIncomeSearch.setBeginDate(beginDate);
        objectIncomeSearch.setEndDate(endDate);
        List<Merchants> subMerchants = merchantsClientService.getSubMerchants(objMerchantCode).getData();
        if (!CollectionUtils.isEmpty(subMerchants)){
            objectIncomeSearch.setMerchantCodes(subMerchants.stream().map(Merchants::getMerchantCode).collect(Collectors.toList()));
        }
        Result<List<SalesVolume>> result = mallOrderClientService.getSubMerchantSalesVolume(objectIncomeSearch);
        decorateMerchantName(result);
        return result;
    }

    private void decorateMerchantName(Result<List<SalesVolume>> result) {
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(result.getCode()) && !CollectionUtils.isEmpty(result.getData())) {
            result.getData().forEach(e -> {
                Result<Merchants> merchantsResult = merchantsClientService.getMerchantByCode(e.getMerchantCode());
                e.setMerchantName(merchantsResult.getData().getMerchantName());
            });
        }
    }

    @GetMapping("/test")
    public List<SalesVolume> test() {
        SalesVolume salesVolume = new SalesVolume();
        salesVolume.setDate(new Date());
        salesVolume.setMerchantName("测试测试测试");
        ArrayList<SalesVolume> list = new ArrayList<>();
        list.add(salesVolume);
        return list;
//        return IdWorker.getIdStr();
    }



}
