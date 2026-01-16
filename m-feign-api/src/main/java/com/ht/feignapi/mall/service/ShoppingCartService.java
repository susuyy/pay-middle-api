package com.ht.feignapi.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.feignapi.mall.clientservice.OrderCategoriesClientService;
import com.ht.feignapi.mall.clientservice.OrderProductionsClientService;
import com.ht.feignapi.mall.clientservice.ShoppingCartClientService;
import com.ht.feignapi.mall.constant.MerchantCardConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.OrderCategorys;
import com.ht.feignapi.mall.entity.OrderShoppingCart;
import com.ht.feignapi.mall.entity.ShowShoppingCartDate;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.CardElectronicSell;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartClientService shoppingCartClientService;

    @Autowired
    private OrderCategoriesClientService orderCategoriesClientService;

    @Autowired
    private CardMapMerchantCardClientService cardMapMerchantCardClientService;

    @Autowired
    private OrderProductionsClientService orderProductionsClientService;

    @Autowired
    private OrderCategorysServeice orderCategorysServeice;

    @Autowired
    private MSPrimeClient msPrimeClient;

    /**
     * 购物车 商品失效
     * @param userId
     */
    public void invalidShoppingCart(Long userId) {
        List<ShowShoppingCartDate> showShoppingCartDateList = shoppingCartClientService.queryMyShoppingCartUnpaid(userId, OrderConstant.UNPAID_STATE).getData();
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
            Boolean ifInvalid = checkProductionsInvalid(showShoppingCartDate.getProductionCode(),showShoppingCartDate.getProductionCategoryCode(),showShoppingCartDate.getMerchantCode());
            if (ifInvalid) {
                //更新购物车状态
                shoppingCartClientService.updateStateById(showShoppingCartDate.getId(), OrderConstant.INVALID_STATE);
            }
        }
    }

    /**
     * 校验商品是否失效
     *
     * @param productionCode
     * @param productionCategoryCode
     * @param merchantCode
     * @return
     */
    public Boolean checkProductionsInvalid(String productionCode, String productionCategoryCode, String merchantCode) {
        OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(productionCategoryCode, merchantCode).getData();
        if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
            CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.mallQueryCodeMerchantCodeType(productionCode, merchantCode, MerchantCardConstant.MALL_SELL_TYPE).getData();
            return checkMerchantCardOnSaleState(cardMapMerchantCards);
        }else {
            //todo 实体类的商品过期校验 暂未校验
            return false;
        }
    }

    /**
     * 校验 卡券类 商品 是否商家
     * @param cardMapMerchantCards
     * @return
     */
    public boolean checkMerchantCardOnSaleState(CardMapMerchantCards cardMapMerchantCards){
        String onSaleState = cardMapMerchantCards.getOnSaleState();
        if ("Y".equals(onSaleState)){
            return false;
        }else {
            return true;
        }
    }


    public void invalidShoppingCartHiGo(Long userId) {
        List<ShowShoppingCartDate> showShoppingCartDateList = shoppingCartClientService.queryMyShoppingCartUnpaid(userId, OrderConstant.UNPAID_STATE).getData();
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
            CardElectronicSell cardElectronicSell = msPrimeClient.queryBatchSellByCode(showShoppingCartDate.getProductionCode()).getData();
            if ("N".equals(cardElectronicSell.getState())) {
                //更新购物车状态
                shoppingCartClientService.updateStateById(showShoppingCartDate.getId(), OrderConstant.INVALID_STATE);
            }
        }
    }
}
