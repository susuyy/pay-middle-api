package com.ht.feignapi.mall.controller;


import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.clientservice.MallPayClientService;
import com.ht.feignapi.mall.constant.MerchantChargeTypeConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.mall.service.CouponService;
import com.ht.feignapi.mall.service.MallOrderPayService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;

import com.ht.feignapi.tonglian.order.entity.UnionOrderData;
import com.ht.feignapi.tonglian.user.controller.UserUsersController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/mall/pay")
@CrossOrigin(allowCredentials = "true")
public class MallPayController {

    private Logger logger = LoggerFactory.getLogger(UserUsersController.class);

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private MallPayClientService mallPayClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private MallOrderPayService mallOrderPayService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private MapMerchantPrimesClientService mapMerchantPrimesClientService;

    @Autowired
    private AuthClientService authClientService;

    /**
     * 获取通联调取 H5 支付数据 (商城购物)
     *
     * @param payOrderData
     * @return
     * @throws Exception
     */
    @PostMapping("/mallUnionOrderBuy")
    public Map mallUnionOrderBuy(@RequestBody PayOrderData payOrderData) throws Exception {
        List<String> cardNoList = payOrderData.getCardNoList();
        OrderOrders orderOrders = mallOrderClientService.queryOrderByOrderCode(payOrderData.getOrderCode()).getData();
        List<OrderOrderDetails> orderOrderDetailsList = mallOrderClientService.queryDetailByOrderCode(payOrderData.getOrderCode()).getData();
        //获取配置的支付数据
        List<MerchantsConfigVO> merchantsConfigVOList = getMerchantsConfigListResult(payOrderData.getMerchantCode());
        String mchId;
        String appId;
        String md5Key;
        for (MerchantsConfigVO merchantsConfigVO : merchantsConfigVOList) {
            if ("MCHID".equals(merchantsConfigVO.getKey())) {
                mchId = merchantsConfigVO.getValue();
                payOrderData.setMchId(mchId);
            }
            if ("APPID".equals(merchantsConfigVO.getKey())) {
                appId = merchantsConfigVO.getValue();
                payOrderData.setAppId(appId);
            }
            if ("MD5KEY".equals(merchantsConfigVO.getKey())) {
                md5Key = merchantsConfigVO.getValue();
                payOrderData.setMD5Key(md5Key);
            }
        }
        Integer discount = 0;
        //计算折扣
        if (cardNoList != null && cardNoList.size() > 0) {
            //校验优惠券是否被使用过
            boolean ifUse = couponService.checkUsed(cardNoList);
            if (ifUse){
                Map retMap = new HashMap<>();
                retMap.put("toPay",false);
                retMap.put("message","存在使用过的优惠券,优惠券异常");
            }

            RetStatementDiscountData retStatementDiscountData = couponService.statementDiscount(cardNoList, orderOrders);
            if (retStatementDiscountData.isUseFlag()){
                discount = retStatementDiscountData.getDiscount();
            }
            List<UserCardDiscountData> userCardDiscountDataList = retStatementDiscountData.getUserCardDiscountDataList();
            for (UserCardDiscountData userCardDiscountData : userCardDiscountDataList) {
                //创建 优惠券 的 预使用流水
                mallOrderClientService.saveOrderPayTrace(orderOrders.getOrderCode(),
                        0L,
                        userCardDiscountData.getCardMapUserCards().getCardNo(),
                        OrderConstant.SHOP_TYPE,
                        OrderConstant.UNPAID_STATE,
                        "card_cards",
                        userCardDiscountData.getCardMapUserCards().getId().toString(),
                        userCardDiscountData.getDiscount(),
                        "");
            }
            mallOrderClientService.updateOrderDiscount(orderOrders.getOrderCode(),discount);
        }

        //积分抵扣 计算积分
        ReturnPointsData returnPointsData = couponService.statementPointsDiscount(orderOrders, orderOrderDetailsList);
        Integer pointsCoupon = 0;
        if (returnPointsData!=null && returnPointsData.getUsePoints()>0){
            //创建 用户积分的 预扣除 流水
            mallOrderClientService.saveOrderPayTrace(orderOrders.getOrderCode(),
                    0L,
                    "",
                    OrderConstant.SHOP_TYPE,
                    OrderConstant.UNPAID_STATE,
                    "prime_points",
                    returnPointsData.getPrimesId().toString(),
                    returnPointsData.getUsePoints(),
                    "");
            pointsCoupon = returnPointsData.getUsePoints();
            mallOrderClientService.updateOrderDiscount(orderOrders.getOrderCode(),pointsCoupon);
            Map<Long, Integer> productionPointsMap = returnPointsData.getProductionPointsMap();
            Set<Long> keySet = productionPointsMap.keySet();
            if (keySet.size()>0) {
                for (Long key : keySet) {
                    mallOrderClientService.updateOneDetailDiscount(key, productionPointsMap.get(key));
                }
            }
        }
        //创建预支付流水
        mallOrderClientService.saveOrderPayTrace(orderOrders.getOrderCode(),
                0L,
                "",
                OrderConstant.SHOP_TYPE,
                OrderConstant.UNPAID_STATE,
                "allinpay_H5",
                "",
                orderOrders.getAmount() - discount - pointsCoupon,
                "");


        String body = "购物";
        if (!StringUtils.isEmpty(orderOrders.getComments()) && !"null".equals(orderOrders.getComments())){
            if (orderOrders.getComments().length()>10){
                body=orderOrders.getComments().substring(0,10)+"...";
            }
        }

        payOrderData.setTrxamt(orderOrders.getAmount() - discount - pointsCoupon);
        payOrderData.setBody(body);
        logger.info("支付参数为" + payOrderData);
        //拉取支付数据
        Map unionOrderMapData = mallPayClientService.mallUnionOrderBuy(payOrderData).getData();
        unionOrderMapData.put("toPay",true);
        unionOrderMapData.put("message","创建支付流水成功,可支付");
        return unionOrderMapData;
    }

    /**
     * 获取 配置支付数据
     *
     * @param merchantCode
     * @return
     */
    public List<MerchantsConfigVO> getMerchantsConfigListResult(String merchantCode) {
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        String chargeType = merchants.getChargeType();
        List<MerchantsConfigVO> list;
        if (MerchantChargeTypeConstant.CHARGE_BY_ENTITY.equals(chargeType)) {
            if (StringUtils.isEmpty(merchants.getBusinessSubjects())) {
                list = merchantsConfigClientService.getPayData(merchants.getMerchantCode()).getData();
            } else {
                list = merchantsConfigClientService.getPayData(merchants.getBusinessSubjects()).getData();
            }
        } else if (MerchantChargeTypeConstant.CHARGE_BY_STORE.equals(chargeType)) {
            list = merchantsConfigClientService.getPayData(merchants.getMerchantCode()).getData();
        } else {
            throw new CheckException(ResultTypeEnum.CHARGE_TYPE_ERROR);
        }
        return list;
    }

    /**
     * 商城购物成功 处理商品 用户的 业务逻辑
     * @param paySuccess
     */
    @PostMapping("/mallBuySuccess")
    public void mallBuySuccess(@RequestBody PaySuccess paySuccess){
        mallOrderPayService.mallBuySuccess(paySuccess);
    }


}
