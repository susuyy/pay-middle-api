package com.ht.feignapi.higo.service;

import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.clientservice.ShoppingCartClientService;
import com.ht.feignapi.mall.constant.MerchantCardConstant;
import com.ht.feignapi.mall.constant.MerchantChargeTypeConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.CardElectronicSell;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.util.OrderCodeFactory;
import com.ht.feignapi.util.OrderEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HiGoService {


    @Autowired
    private ShoppingCartClientService shoppingCartClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    /**
     * 校验库存
     *
     * @param inventoryQuantity
     * @param shoppingCarQuantity
     * @return
     */
    public boolean checkProductionInventory(Integer inventoryQuantity, int shoppingCarQuantity) {
        return inventoryQuantity >= shoppingCarQuantity;
    }

    /**
     * 购物车结算下单
     *
     * @param shoppingCartUnionOrderData
     * @return
     */
    public RetUnionOrderData shoppingCartUnionOrder(ShoppingCartUnionOrderData shoppingCartUnionOrderData) {
        List<String> shoppingCartOrderCodeList = shoppingCartUnionOrderData.getShoppingCartOrderCodeList();
        List<String> merchantCodeList = new ArrayList<>();
        List<OrderShoppingCart> orderShoppingCartList = new ArrayList<>();
        for (String shoppingCartOrderCode : shoppingCartOrderCodeList) {
            OrderShoppingCart orderShoppingCart = shoppingCartClientService.queryByOrderCode(shoppingCartOrderCode).getData();
            merchantCodeList.add(orderShoppingCart.getMerchantCode());
            orderShoppingCartList.add(orderShoppingCart);
        }

        List<String> entityChargeTypeMerchantCodeList = new ArrayList<>();

        for (String merchantCode : merchantCodeList) {
            Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
            entityChargeTypeMerchantCodeList.add(merchants.getMerchantCode());
        }

        //主体 收银分类
        List<String> entityChargeTypeShoppingCartCode = new ArrayList<>();
        for (String merchantCode : entityChargeTypeMerchantCodeList) {
            for (OrderShoppingCart orderShoppingCart : orderShoppingCartList) {
                if (orderShoppingCart.getMerchantCode().equals(merchantCode)) {
                    entityChargeTypeShoppingCartCode.add(orderShoppingCart.getOrderCode());
                }
            }
        }

        RetUnionOrderData retUnionOrderData = new RetUnionOrderData();
        //主体收银类型处理
        if (entityChargeTypeMerchantCodeList.size() > 0) {
            // 主体收银 直接创建订单
            List<ShowShoppingCartDate> showShoppingCartDateList = shoppingCartClientService.queryByOrderCodeList(entityChargeTypeShoppingCartCode).getData();
            //创建订单
            retUnionOrderData = createNoMoreCategoryOrder(showShoppingCartDateList, shoppingCartUnionOrderData.getObjectMerchantCode(), shoppingCartUnionOrderData.getOrderWayBills());
        }

        return retUnionOrderData;
    }

    /**
     * 不根据商品品类拆单 不根据商户编码拆单 直接下单
     *
     * @param showShoppingCartDateList
     * @param orderWayBills
     * @return
     */
    private RetUnionOrderData createNoMoreCategoryOrder(List<ShowShoppingCartDate> showShoppingCartDateList, String merchantCode, OrderWayBills orderWayBills) {
        String orderCode = OrderCodeFactory.getOrderCode(OrderEnum.ShopOrder);
        //创建订单明细
        Integer amount = 0;
        String orderComment = "";
//        Set<String> merchantCodeSet = new HashSet<>();
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
            //商品逻辑处理
            CardElectronicSell cardElectronicSell = msPrimeClient.queryBatchSellByCode(showShoppingCartDate.getProductionCode()).getData();
            Integer price = Integer.parseInt(cardElectronicSell.getSellAmount()+"") * showShoppingCartDate.getQuantity().intValue();
            OrderOrderDetails orderOrderDetails = mallOrderClientService.saveOrderOrderDetails(orderCode,
                    cardElectronicSell.getRefBrhId(),
                    showShoppingCartDate.getUserId(),
                    showShoppingCartDate.getQuantity().intValue(),
                    price,
                    showShoppingCartDate.getProductionCode(),
                    showShoppingCartDate.getProductionName(),
                    showShoppingCartDate.getProductionCategoryCode(),
                    showShoppingCartDate.getProductionCategoryName(),
                    showShoppingCartDate.getActivityCode(),
                    showShoppingCartDate.getOrderCode(),
                    OrderConstant.UNPAID_STATE,
                    OrderConstant.SHOP_TYPE,
                    showShoppingCartDate.getDiscount()).getData();
            amount = amount + price;
            orderComment = orderComment + cardElectronicSell.getCardName() + " ";
//            merchantCodeSet.add(orderProductions.getMerchantCode());
        }
        //保存订单主表
        mallOrderClientService.saveOrderOrders(orderCode,
                OrderConstant.SHOP_TYPE,
                OrderConstant.UNPAID_STATE,
                merchantCode,
                showShoppingCartDateList.get(0).getUserId(),
                "-1",
                showShoppingCartDateList.size(),
                amount,
                orderComment,
                0);

        RetUnionOrderData retUnionOrderData = new RetUnionOrderData();
        retUnionOrderData.setOrderCode(orderCode);
        retUnionOrderData.setIsToPay(true);
        retUnionOrderData.setUnionOrderMessage("下单成功,直接跳转支付,无需拆单");
        retUnionOrderData.setMerchantCode(merchantCode);
        return retUnionOrderData;
    }
}
