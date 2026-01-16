package com.ht.feignapi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.mall.clientservice.OrderCategoriesClientService;
import com.ht.feignapi.mall.clientservice.OrderProductionsClientService;
import com.ht.feignapi.mall.clientservice.ShoppingCartClientService;
import com.ht.feignapi.mall.constant.MerchantCardConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.mall.service.InventoryService;
import com.ht.feignapi.mall.service.MallProductionService;
import com.ht.feignapi.mall.service.OrderCategorysServeice;
import com.ht.feignapi.mall.service.ShoppingCartService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mall/shoppingCart")
@CrossOrigin(allowCredentials = "true")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartClientService shoppingCartClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private OrderCategoriesClientService orderCategorysClientService;

    @Autowired
    private CardMapMerchantCardClientService cardMapMerchantCardClientService;

    @Autowired
    private OrderProductionsClientService orderProductionsClientService;

    @Autowired
    private CardCardsClientService cardCardsClientService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryClientService mallInventoryClientService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private MallProductionService mallProductionService;

    @Autowired
    private OrderCategorysServeice orderCategorysServeice;

    /**
     * 用户添加购物车
     *
     * @param addShoppingCartDate
     */
    @PostMapping("/addShoppingCart")
    public void addShoppingCart(@RequestBody AddShoppingCartDate addShoppingCartDate) {
        MallProductions mallProductions = new MallProductions();
        mallProductions.setProductionCode(addShoppingCartDate.getOrderProductions().getProductionCode());
        mallProductions.setMerchantCode(addShoppingCartDate.getStoreMerchantCode());
        mallProductions.setCategoryCode(addShoppingCartDate.getOrderProductions().getCategoryCode());
        mallProductionService.parseEndDate(mallProductions);
        boolean before = mallProductions.getEndDate().before(new Date());
        if (before){
            throw new CheckException(ResultTypeEnum.PRODUCTION_OVERDUE);
        }


        boolean inventory = inventoryService.checkProductionInventory(addShoppingCartDate.getOrderProductions().getProductionCode(), addShoppingCartDate.getStoreMerchantCode(), addShoppingCartDate.getQuantity().intValue());
        if (!inventory){
            throw new CheckException(ResultTypeEnum.NOT_INVENTORY);
        }
        UserUsers users = authClientService.queryByOpenid(addShoppingCartDate.getOpenId()).getData();
        addShoppingCartDate.setUserId(users.getId());
        OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(addShoppingCartDate.getOrderProductions().getCategoryCode(), addShoppingCartDate.getStoreMerchantCode()).getData();
        if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
            //卡券类商品
            CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.mallQueryCodeMerchantCodeType(addShoppingCartDate.getOrderProductions().getProductionCode(),
                    addShoppingCartDate.getStoreMerchantCode(),
                    MerchantCardConstant.MALL_SELL_TYPE).getData();
            addShoppingCartDate.setCategoryLevel01Code(orderCategorys.getCategoryLevel01Code());
            addShoppingCartDate.setCardMapMerchantCards(cardMapMerchantCards);
            CardCards cardCards = cardCardsClientService.getCardByCardCode(cardMapMerchantCards.getCardCode()).getData();
            addShoppingCartDate.setNotice(cardCards.getNotice());
            shoppingCartClientService.addShoppingCartDate(addShoppingCartDate);
        } else {
            //实体类商品
            addShoppingCartDate.setCategoryLevel01Code(orderCategorys.getCategoryLevel01Code());
            shoppingCartClientService.addShoppingCartDate(addShoppingCartDate);
        }
    }

    /**
     * 展示用户购物车数据 按店铺分页
     *
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/showShoppingCart")
    public RetPageCartData showShoppingCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData) {
        UserUsers users = authClientService.queryByOpenid(requestShowShoppingCartData.getOpenId()).getData();
        requestShowShoppingCartData.setUserId(users.getId());

        //修改购物车 商品失效
        shoppingCartService.invalidShoppingCart(users.getId());

        RetPageCartData retPageData = shoppingCartClientService.showShoppingCart(requestShowShoppingCartData).getData();
        List<ReturnShowShoppingCartData> records = retPageData.getRecords();
        for (ReturnShowShoppingCartData record : records) {
            Merchants merchants = merchantsClientService.getMerchantByCode(record.getMerchantCode()).getData();
            if (merchants != null) {
                record.setMerchantName(merchants.getMerchantName());
            }
            List<ShowShoppingCartDate> showShoppingCartDateList = record.getShowShoppingCartDateList();
            //封装图片
            for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
                OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(showShoppingCartDate.getProductionCategoryCode(), showShoppingCartDate.getMerchantCode()).getData();
                if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                    CardCards cardCards = cardCardsClientService.getCardByCardCode(showShoppingCartDate.getProductionCode()).getData();
                    CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.mallQueryCodeMerchantCodeType(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode(), MerchantCardConstant.MALL_SELL_TYPE).getData();
                    showShoppingCartDate.setProductionUrl(cardCards.getCardPicUrl());
                    showShoppingCartDate.setUnitPrice(cardMapMerchantCards.getPrice());
                } else {
                    OrderProductions orderProductions = orderProductionsClientService.getByCode(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode()).getData();
                    showShoppingCartDate.setProductionUrl(orderProductions.getProductionPicUrl());
                    showShoppingCartDate.setUnitPrice(orderProductions.getPrice());
                }
                showShoppingCartDate.setTopCategory(orderCategorys.getCategoryLevel01Code());
                showShoppingCartDate.setMerchantName(record.getMerchantName());
                boolean inventoryFlag = inventoryService.checkProductionInventory(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode(), showShoppingCartDate.getQuantity().intValue());
                showShoppingCartDate.setInventoryFlag(inventoryFlag);
                if (inventoryFlag) {
                    MallProductions mallProductions = new MallProductions();
                    mallProductions.setProductionCode(showShoppingCartDate.getProductionCode());
                    mallProductions.setMerchantCode(showShoppingCartDate.getMerchantCode());
                    mallProductions.setCategoryCode(showShoppingCartDate.getProductionCategoryCode());
                    mallProductionService.parseEndDate(mallProductions);
                    boolean before = mallProductions.getEndDate().before(new Date());
                    if (before) {
                        showShoppingCartDate.setInventoryFlag(false);
                    }
                }
            }
        }
        return retPageData;
    }

    /**
     * 修改购物车 数量
     *
     * @param updateQuantityData
     * @return
     */
    @PostMapping("/updateQuantityById")
    public RetUpdateQuantityData updateQuantityById(@RequestBody UpdateQuantityData updateQuantityData) {
        ShowShoppingCartDate showShoppingCartDate = shoppingCartClientService.queryById(updateQuantityData.getId()).getData();
        OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(showShoppingCartDate.getProductionCategoryCode(), showShoppingCartDate.getMerchantCode()).getData();
        RetUpdateQuantityData retUpdateQuantityData = new RetUpdateQuantityData();
        if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
            CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.mallQueryCodeMerchantCodeType(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode(), MerchantCardConstant.MALL_SELL_TYPE).getData();
            updateQuantityData.setCardMapMerchantCards(cardMapMerchantCards);
            retUpdateQuantityData.setUnitPrice(cardMapMerchantCards.getPrice());
        } else {
            OrderProductions productions = orderProductionsClientService.getByCode(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode()).getData();
            updateQuantityData.setOrderProductions(productions);
            retUpdateQuantityData.setUnitPrice(productions.getPrice());
        }
        //校验库存
        boolean inventoryFlag = inventoryService.checkProductionInventory(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode(), updateQuantityData.getQuantity());
        Integer integer = 0;
        try {
            integer = mallInventoryClientService.getInventory(showShoppingCartDate.getMerchantCode(), showShoppingCartDate.getProductionCode()).getData();
            retUpdateQuantityData.setInventoryCount(integer);
        } catch (Exception e) {
            retUpdateQuantityData.setInventoryCount(0);
        }
        if (!inventoryFlag){
            throw new CheckException(ResultTypeEnum.NOT_INVENTORY);
        }
        updateQuantityData.setLevelThreeCode(orderCategorys.getCategoryLevel01Code());
        UpdateQuantityData quantityData = shoppingCartClientService.updateQuantityById(updateQuantityData).getData();
        retUpdateQuantityData.setId(quantityData.getId());
        retUpdateQuantityData.setQuantity(quantityData.getQuantity());
        retUpdateQuantityData.setAmount(quantityData.getAmount());
        retUpdateQuantityData.setLevelThreeCode(quantityData.getLevelThreeCode());
        retUpdateQuantityData.setInventoryFlag(true);
        return retUpdateQuantityData;
    }

    /**
     * 获取失效宝贝
     *
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/invalidCart")
    public Page<ShowShoppingCartDate> invalidCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData) {
        UserUsers users = authClientService.queryByOpenid(requestShowShoppingCartData.getOpenId()).getData();
        requestShowShoppingCartData.setUserId(users.getId());
        return shoppingCartClientService.invalidCart(requestShowShoppingCartData).getData();
    }

    /**
     * 获取购物车 商品数量
     *
     * @param openId
     * @return
     */
    @GetMapping("/productionsCount")
    public Integer productionsCount(@RequestParam("openId") String openId) {
        UserUsers userUsers = authClientService.queryByOpenid(openId).getData();
        return shoppingCartClientService.productionsCount(userUsers.getId()).getData();
    }

    /**
     * 清空失效宝贝
     *
     * @param openId
     */
    @PostMapping("/delInvalidCart")
    public void delInvalidCart(@RequestParam("openId") String openId) {
        UserUsers users = authClientService.queryByOpenid(openId).getData();
        shoppingCartClientService.delInvalidCart(users.getId());
    }

    /**
     * 获取更多 某个商家下的 购物车 数据
     *
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/moreShoppingCart")
    public Page<ShowShoppingCartDate> moreShoppingCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData) {
        UserUsers users = authClientService.queryByOpenid(requestShowShoppingCartData.getOpenId()).getData();
        requestShowShoppingCartData.setUserId(users.getId());
        Page<ShowShoppingCartDate> page = shoppingCartClientService.moreShoppingCart(requestShowShoppingCartData).getData();
        List<ShowShoppingCartDate> showShoppingCartDateList = page.getRecords();
        //封装图片
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
            OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(showShoppingCartDate.getProductionCategoryCode(), showShoppingCartDate.getMerchantCode()).getData();
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                CardCards cardCards = cardCardsClientService.getCardByCardCode(showShoppingCartDate.getProductionCode()).getData();
                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.mallQueryCodeMerchantCodeType(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode(), MerchantCardConstant.MALL_SELL_TYPE).getData();
                showShoppingCartDate.setProductionUrl(cardCards.getCardPicUrl());
                showShoppingCartDate.setUnitPrice(cardMapMerchantCards.getPrice());
            } else {
                OrderProductions orderProductions = orderProductionsClientService.getByCode(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode()).getData();
                showShoppingCartDate.setProductionUrl(orderProductions.getProductionPicUrl());
                showShoppingCartDate.setUnitPrice(orderProductions.getPrice());
            }
            showShoppingCartDate.setTopCategory(orderCategorys.getCategoryLevel01Code());
            Merchants merchants = merchantsClientService.getMerchantByCode(showShoppingCartDate.getMerchantCode()).getData();
            if (merchants!=null) {
                showShoppingCartDate.setMerchantName(merchants.getMerchantName());
            }
        }
        return page;
    }


    /**
     * 用户 删除购物车
     * @param orderCode
     */
    @DeleteMapping("/delShoppingCart")
    public void delShoppingCart(@RequestParam("orderCode")String orderCode){
        shoppingCartClientService.delShoppingCart(orderCode);
    }

    /**
     * 记录购物车选中状态
     * @param map
     */
    @PostMapping("/recordSelected")
    public void recordSelected(@RequestBody Map<String,String> map){
        String orderCode = map.get("orderCode");
        String selected = map.get("selected");
        shoppingCartClientService.updateSelectedByOrderCode(orderCode,selected);
    }
}
