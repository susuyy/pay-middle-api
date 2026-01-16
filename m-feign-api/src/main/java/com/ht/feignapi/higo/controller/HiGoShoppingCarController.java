package com.ht.feignapi.higo.controller;

import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.higo.entity.HiGoAddShoppingCartData;
import com.ht.feignapi.higo.service.HiGoService;
import com.ht.feignapi.mall.clientservice.ShoppingCartClientService;
import com.ht.feignapi.mall.constant.MerchantCardConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.mall.service.ShoppingCartService;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/higo/shoppingCar")
@CrossOrigin(allowCredentials = "true")
public class HiGoShoppingCarController {

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private ShoppingCartClientService shoppingCartClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private HiGoService hiGoService;

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 用户添加购物车
     *
     * @param hiGoAddShoppingCartData
     */
    @PostMapping("/addShoppingCart")
    public void addShoppingCart(@RequestBody HiGoAddShoppingCartData hiGoAddShoppingCartData) {
        if (hiGoAddShoppingCartData.getCardElectronicSell()==null || hiGoAddShoppingCartData.getQuantity()<=0){
            throw new CheckException(ResultTypeEnum.NOT_PRODUCTION);
        }
        CardElectronicSell cardElectronicSell = hiGoAddShoppingCartData.getCardElectronicSell();
        if (cardElectronicSell.getQuantity()<hiGoAddShoppingCartData.getQuantity()){
            throw new CheckException(ResultTypeEnum.NOT_INVENTORY);
        }
        cardElectronicSell.setQuantity(hiGoAddShoppingCartData.getQuantity());
        shoppingCartClientService.addShoppingCartDateHiGo(hiGoAddShoppingCartData);
    }

    /**
     * 展示用户购物车数据 按店铺分页
     *
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/showShoppingCart")
    public RetPageCartData showShoppingCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData) {

        //修改购物车 商品失效
        shoppingCartService.invalidShoppingCartHiGo(requestShowShoppingCartData.getUserId());

        RetPageCartData retPageData = shoppingCartClientService.showShoppingCartHiGo(requestShowShoppingCartData).getData();
        List<ReturnShowShoppingCartData> records = retPageData.getRecords();
        for (ReturnShowShoppingCartData record : records) {
            Merchants merchants = merchantsClientService.getMerchantByCode(record.getMerchantCode()).getData();
            if (merchants != null) {
                record.setMerchantName(merchants.getMerchantName());
            }
            List<ShowShoppingCartDate> showShoppingCartDateList = record.getShowShoppingCartDateList();
            //封装图片
            for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {

                CardElectronicSell cardElectronicSell = msPrimeClient.queryBatchSellByCode(showShoppingCartDate.getProductionCode()).getData();

                showShoppingCartDate.setProductionUrl(cardElectronicSell.getBackGround());
                showShoppingCartDate.setUnitPrice(Integer.parseInt(cardElectronicSell.getSellAmount()+""));
                showShoppingCartDate.setInventoryCount(cardElectronicSell.getQuantity());
                showShoppingCartDate.setMerchantName(record.getMerchantName());
                boolean inventoryFlag = hiGoService.checkProductionInventory(cardElectronicSell.getQuantity(),showShoppingCartDate.getQuantity().intValue());
                showShoppingCartDate.setInventoryFlag(inventoryFlag);
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
        CardElectronicSell cardElectronicSell = msPrimeClient.queryBatchSellByCode(showShoppingCartDate.getProductionCode()).getData();
        RetUpdateQuantityData retUpdateQuantityData = new RetUpdateQuantityData();
        retUpdateQuantityData.setUnitPrice(Integer.parseInt(cardElectronicSell.getSellAmount()+""));
        //校验库存
        boolean inventoryFlag = hiGoService.checkProductionInventory(cardElectronicSell.getQuantity(),updateQuantityData.getQuantity());
        retUpdateQuantityData.setInventoryCount(cardElectronicSell.getQuantity());
        if (!inventoryFlag){
            throw new CheckException(ResultTypeEnum.NOT_INVENTORY);
        }
        updateQuantityData.setCardElectronicSell(cardElectronicSell);
        UpdateQuantityData quantityData = shoppingCartClientService.hiGoUpdateQuantityById(updateQuantityData).getData();
        retUpdateQuantityData.setId(quantityData.getId());
        retUpdateQuantityData.setQuantity(quantityData.getQuantity());
        retUpdateQuantityData.setAmount(quantityData.getAmount());
        retUpdateQuantityData.setInventoryFlag(true);
        return retUpdateQuantityData;
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
     * higo 获取购物车 商品数量
     *
     * @param openId
     * @return
     */
    @GetMapping("/productionsCount")
    public Integer productionsCount(@RequestParam("openId") String openId,@RequestParam("userId")String userId) {
        return shoppingCartClientService.productionsCountHiGo(Long.parseLong(userId)).getData();
    }

    /**
     * 购物车 结算 下单
     * @param shoppingCartUnionOrderData
     * @return
     */
    @PostMapping("/shoppingCartUnionOrder")
    public RetUnionOrderData shoppingCartUnionOrder(@RequestBody ShoppingCartUnionOrderData shoppingCartUnionOrderData) {
        List<ShowShoppingCartDate> showShoppingCartDates = shoppingCartClientService.queryByOrderCodeList(shoppingCartUnionOrderData.getShoppingCartOrderCodeList()).getData();
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDates) {
            //校验商品是否下架与库存是否充足
            CardElectronicSell cardElectronicSell = msPrimeClient.queryBatchSellByCode(showShoppingCartDate.getProductionCode()).getData();
            boolean inventory = hiGoService.checkProductionInventory(cardElectronicSell.getQuantity(), showShoppingCartDate.getQuantity().intValue());
            if (!inventory){
                throw new CheckException(ResultTypeEnum.PRODUCTION_OVERDUE);
            }
            if (!"Y".equals(cardElectronicSell.getState())){
                throw new CheckException(ResultTypeEnum.PRODUCTION_OVERDUE);
            }
        }
        shoppingCartUnionOrderData.setMerchantChargeType("charge_by_entity");
        RetUnionOrderData retUnionOrderData = hiGoService.shoppingCartUnionOrder(shoppingCartUnionOrderData);
        return retUnionOrderData;
    }
}
