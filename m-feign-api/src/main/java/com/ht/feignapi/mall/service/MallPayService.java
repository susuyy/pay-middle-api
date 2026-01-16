package com.ht.feignapi.mall.service;

import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import com.ht.feignapi.tongshangyun.entity.ResponseAgentCollectApplyData;
import com.ht.feignapi.tongshangyun.service.AgentCollectApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MallPayService {

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AgentCollectApplyService agentCollectApplyService;

    @Autowired
    private MerchantsClientService merchantsClientService;


    public ResponseAgentCollectApplyData mallAgentCollectApplyBuy(PayOrderData payOrderData) throws Exception {
        List<String> cardNoList = payOrderData.getCardNoList();
        OrderOrders orderOrders = mallOrderClientService.queryOrderByOrderCode(payOrderData.getOrderCode()).getData();
        List<OrderOrderDetails> orderOrderDetailsList = mallOrderClientService.queryDetailByOrderCode(payOrderData.getOrderCode()).getData();

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

        BizMerchantUserData bizMerchantUserData = merchantsClientService.queryMerchantBizUserIdAndPayerBizUserId(orderOrders.getMerchantCode(), orderOrders.getUserId()).getData();

        int payAmount = orderOrders.getAmount() - discount - pointsCoupon;
        //通商云托管代收申请 同时确认支付
        return  agentCollectApplyService.agentCollectApplyAndCheckPay(Long.parseLong(payAmount + ""),
                bizMerchantUserData.getBizObjectMerchantUserId(), bizMerchantUserData.getBizUserId(),
                orderOrders.getComments(), orderOrders.getComments(),
                payOrderData.getOrderCode());
    }
}
