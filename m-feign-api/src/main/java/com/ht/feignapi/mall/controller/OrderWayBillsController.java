package com.ht.feignapi.mall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.mall.clientservice.OrderCategoriesClientService;
import com.ht.feignapi.mall.clientservice.OrderProductionsClientService;
import com.ht.feignapi.mall.clientservice.ShoppingCartClientService;
import com.ht.feignapi.mall.constant.OrderWayBillsTypeConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.mall.service.CouponService;
import com.ht.feignapi.mall.service.OrderCategorysServeice;
import com.ht.feignapi.mall.service.OrderWayBillsService;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-11
 */
@RestController
@RequestMapping("/mall/orderWayBills")
@CrossOrigin(allowCredentials = "true")
public class OrderWayBillsController {

    @Autowired
    private OrderWayBillsService orderWayBillsService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ShoppingCartClientService shoppingCartClientService;

    @Autowired
    private OrderCategoriesClientService orderCategoriesClientService;

    @Autowired
    private OrderProductionsClientService orderProductionsClientService;

    @Autowired
    private CardCardsClientService cardCardsClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private OrderCategorysServeice orderCategorysServeice;

    /**
     * 计算派送费 (暂时未用)
     * @param wayBillsAndCouponMoney
     * @return
     */
    @PostMapping("/wayBillsAndCouponMoney")
    public RetWayBillsAndCouponMoney statementWayBillsAndCouponMoney(@RequestBody WayBillsAndCouponMoney wayBillsAndCouponMoney){
        List<String> cardNoList = wayBillsAndCouponMoney.getCardNoList();
        if (cardNoList!=null && cardNoList.size()>0){
            //计算优惠
//            couponService.preStatementDiscount(cardNoList,wayBillsAndCouponMoney.getShoppingCartOrderCodeList());
        }

        List<String> shoppingCartOrderCodeList = wayBillsAndCouponMoney.getShoppingCartOrderCodeList();
        if (shoppingCartOrderCodeList!=null && shoppingCartOrderCodeList.size()>0) {
            //购物车计算
            List<ShowShoppingCartDate> shoppingCartDateList = shoppingCartClientService.queryByOrderCodeList(shoppingCartOrderCodeList).getData();
            Map<String, Integer> cartMoneyMap = new HashMap<>();
            for (ShowShoppingCartDate showShoppingCartDate : shoppingCartDateList) {
                OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(showShoppingCartDate.getProductionCategoryCode(), showShoppingCartDate.getMerchantCode()).getData();
                if (!ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                    Integer shoppingCartMoney = cartMoneyMap.get(showShoppingCartDate.getMerchantCode());
                    if (shoppingCartMoney==null){
                        cartMoneyMap.put(showShoppingCartDate.getMerchantCode(), shoppingCartMoney);
                    }else {
                        shoppingCartMoney =  shoppingCartMoney + showShoppingCartDate.getAmount();
                        cartMoneyMap.put(showShoppingCartDate.getMerchantCode(), shoppingCartMoney);
                    }
                }
            }
            double totalWayBillsMoney = 0;
            Map<String, Double> merchantWayBillMoney = new HashMap<>();
            Set<String> merchantCodeSet = cartMoneyMap.keySet();
            for (String merchantCode : merchantCodeSet) {
                //计算某个商家的运费
                double wayBillsMoney = orderWayBillsService.statementWayBillsMoney(merchantCode,cartMoneyMap.get(merchantCode),wayBillsAndCouponMoney.getOrderWayBillsType());
                totalWayBillsMoney = totalWayBillsMoney + wayBillsMoney;
                merchantWayBillMoney.put(merchantCode, wayBillsMoney);
            }
            RetWayBillsAndCouponMoney retWayBillsAndCouponMoney = new RetWayBillsAndCouponMoney();
            retWayBillsAndCouponMoney.setTotalWayBillsMoney(totalWayBillsMoney);
            retWayBillsAndCouponMoney.setMerchantWayBillMoneyMap(merchantWayBillMoney);
            return retWayBillsAndCouponMoney;
        }else {
            //直接购买计算
            OrderProductions orderProductions = orderProductionsClientService.getByCode(wayBillsAndCouponMoney.getProductionCode(),wayBillsAndCouponMoney.getStoreMerchantCode()).getData();
            //计算 商家的运费
            double wayBillsMoney = orderWayBillsService.statementWayBillsMoney(orderProductions.getMerchantCode(),
                    orderProductions.getPrice() * wayBillsAndCouponMoney.getQuantity(),
                    wayBillsAndCouponMoney.getOrderWayBillsType());
            Map<String, Double> merchantWayBillMoney = new HashMap<>();
            merchantWayBillMoney.put(orderProductions.getMerchantCode(), wayBillsMoney);
            RetWayBillsAndCouponMoney retWayBillsAndCouponMoney = new RetWayBillsAndCouponMoney();
            retWayBillsAndCouponMoney.setTotalWayBillsMoney(wayBillsMoney);
            retWayBillsAndCouponMoney.setMerchantWayBillMoneyMap(merchantWayBillMoney);
            return retWayBillsAndCouponMoney;
        }
    }

    /**
     * 校验提交的商品 是否需要填写派送地址
     * @param productionsMsgList
     * @return
     */
    @PostMapping("/checkIfIntoWay")
    public RetCheckIfIntoWayData checkIfIntoWay(@RequestBody List<ProductionsMsg> productionsMsgList){
        RetCheckIfIntoWayData retCheckIfIntoWayData = new RetCheckIfIntoWayData();
        List<ReturnWayBillDetail> retDetailList = new ArrayList<>();
        Set<String> merchantCodeSet = new HashSet<>();
        for (ProductionsMsg productionsMsg : productionsMsgList) {
            OrderProductions orderProductions = orderProductionsClientService.getByCode(productionsMsg.getProductionCode(), productionsMsg.getSubMerchantCode()).getData();
            if (orderProductions!=null){
                retCheckIfIntoWayData.setIntoWayFlag(true);
                merchantCodeSet.add(productionsMsg.getSubMerchantCode());
            }
        }
        for (String merchantCode : merchantCodeSet) {
            Integer wayBillFee = orderWayBillsService.getOneMerchantWayBillFee(merchantCode, OrderWayBillsTypeConstant.COURIER_SEND);
            Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
            ReturnWayBillDetail returnWayBillDetail = new ReturnWayBillDetail();
            returnWayBillDetail.setMerchantName(merchants.getMerchantName());
            returnWayBillDetail.setWayBillFee(wayBillFee);
            retDetailList.add(returnWayBillDetail);
        }
        retCheckIfIntoWayData.setRetDetailList(retDetailList);
        Integer totalWayBillFee = 0;
        for (ReturnWayBillDetail returnWayBillDetail : retDetailList) {
            totalWayBillFee = totalWayBillFee + returnWayBillDetail.getWayBillFee();
        }
        retCheckIfIntoWayData.setTotalWayBillFee(totalWayBillFee);
        return retCheckIfIntoWayData;
    }

    /**
     * 计算派送费 (暂时未使用)
     * @param productionsMsgList
     * @param wayBillType
     * @return
     */
    @PostMapping("/showOrderWayBillFee")
    public List showOrderWayBillFee(@RequestBody List<ProductionsMsg> productionsMsgList,@RequestParam String wayBillType){
        List retDetailList = new ArrayList<>();
        Set<String> merchantCodeSet = new HashSet<>();
        for (ProductionsMsg productionsMsg : productionsMsgList) {
            List<OrderProductions> orderProductionsList = orderProductionsClientService.queryByProductionCodeNotMerchant(productionsMsg.getProductionCode()).getData();
            if (orderProductionsList!=null && orderProductionsList.size()>0){
                merchantCodeSet.add(productionsMsg.getSubMerchantCode());
            }
        }
        for (String merchantCode : merchantCodeSet) {
            Integer wayBillFee = orderWayBillsService.getOneMerchantWayBillFee(merchantCode, wayBillType);
            Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
            ReturnWayBillDetail returnWayBillDetail = new ReturnWayBillDetail();
            returnWayBillDetail.setMerchantName(merchants.getMerchantName());
            returnWayBillDetail.setWayBillFee(wayBillFee);
            retDetailList.add(returnWayBillDetail);
        }
        return retDetailList;
    }

    /**
     * 分页展示用户派送单
     * @param showMyWayBillsData
     * @return
     */
    @PostMapping("/showMyOrderWayBills")
    public Page<OrderWayBills> showMyOrderWayBills(@RequestBody ShowMyWayBillsData showMyWayBillsData){
        return orderWayBillsService.showMyOrderWayBills(showMyWayBillsData);
    }

    /**
     * 查询 派送单下的 商品明细
     * @param id
     * @param wayBillCode
     */
    @GetMapping("/queryWayBillsProduction")
    public WayBillPageData queryWayBillsProduction(@RequestParam("id") String id,@RequestParam("wayBillCode")String wayBillCode){
        WayBillPageData wayBillPageData = orderWayBillsService.queryWayBillsProduction(id, wayBillCode);
        return wayBillPageData;
    }
}

