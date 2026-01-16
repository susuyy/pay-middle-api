package com.ht.feignapi.mall.service;

import com.alibaba.fastjson.JSON;
import com.ht.feignapi.mall.clientservice.*;
import com.ht.feignapi.mall.constant.CardUserMallConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.pay.client.PayProjectClient;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import com.ht.feignapi.tonglian.merchant.service.MerchantPrimeService;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderRefundService{

    private final static Logger logger = LoggerFactory.getLogger(OrderRefundService.class);

    @Autowired
    private PayProjectClient payProjectClient;

    @Autowired
    private CardMapMerchantCardClientService cardsClientService;

    @Autowired
    private OrderProductionsClientService productionsClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private MallProductionService mallProductionService;

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private MallOrderRefundClient refundClient;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private OrderCategoriesClientService orderCategoriesClientService;

    @Autowired
    private MallAppShowClientService mallAppShowClientService;

    /**
     * 退款调用
     * @param merchantCode
     * @param amount
     * @param backOrderCode
     * @param oriOrderCode
     * @return
     */
    public RetRefundOrderData mallOrderRefund(String merchantCode,Integer amount,String backOrderCode,String oriOrderCode){
        OrderRefundData orderRefundData = new OrderRefundData();
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        List<MerchantsConfigVO> configVOS = merchantsConfigClientService.getPayData(merchants.getBusinessSubjects()).getData();
        String cusId = "";
        String appId = "";
        for (MerchantsConfigVO configVO : configVOS) {
            if ("APPID".equals(configVO.getKey())){
                appId = configVO.getValue();
            }
            if ("MCHID".equals(configVO.getKey())){
                cusId = configVO.getValue();
            }
        }
        orderRefundData.setCusid(cusId);
        orderRefundData.setAppid(appId);
        orderRefundData.setMerchantCode(merchants.getBusinessSubjects());
        orderRefundData.setTrxamt(amount);
        orderRefundData.setReqsn(backOrderCode);
        orderRefundData.setOldreqsn(oriOrderCode);
        RetRefundOrderData retRefundOrderData = payProjectClient.mallOrderRefund(orderRefundData).getData();
        if ("0000".equals(retRefundOrderData.getTrxstatus()) && "SUCCESS".equals(retRefundOrderData.getRetcode())){
            retRefundOrderData.setServerFlag(true);
            return retRefundOrderData;

        }else {
            logger.info("退款失败数据为:======"+retRefundOrderData);
            retRefundOrderData.setServerFlag(false);
            return retRefundOrderData;
        }
    }


    /**
     * 执行退款相关的功能
     * @return
     */
    public boolean doRefund(OrderRefundDetails orderRefundDetails){
        RetRefundOrderData retRefundOrderData = mallOrderRefund(orderRefundDetails.getMerchantCode(), orderRefundDetails.getAmount(),
                orderRefundDetails.getBackOrderCode(), orderRefundDetails.getOriOrderCode());
        if(retRefundOrderData.isServerFlag()){
            String prodBatchCode;
            if (mallProductionService.isCardProduction(orderRefundDetails.getProductionCategoryCode(),orderRefundDetails.getMerchantCode())) {
                logger.info("*************进入卡券退款流程***************");
                logger.info("*******************orderRefundDetails******************:"+ JSON.toJSONString(orderRefundDetails));
                cardMapUserClientService.removeUserCard(orderRefundDetails.getUserId(), orderRefundDetails.getProductionCode(), orderRefundDetails.getOrderDetailId());
                Result<OrderOrderDetails> detail = mallOrderClientService.queryDetailById(orderRefundDetails.getOrderDetailId());
                prodBatchCode = detail.getData().getActivityCode();
                inventoryService.addInventory(orderRefundDetails.getMerchantCode(), orderRefundDetails.getProductionCode(),
                        orderRefundDetails.getQuantity(),prodBatchCode);
            }else {
                Result<OrderProductions> productionsResult = productionsClientService.getByCode(orderRefundDetails.getProductionCode(), orderRefundDetails.getMerchantCode());
                //todo 第二步,批次库存增加
            }
            //todo 第四步，积分退回

            //todo 最后一步，修改退款单状态,修改订单状态
            orderRefundDetails.setTrxId(retRefundOrderData.getTrxid());
            refundClient.doRefundOrders(orderRefundDetails);
            return true;
        }
        return false;
    }

    public List<OrderOrderDetails> queryRefundOrderDetailsList(String orderCode) {
        List<OrderOrderDetails> orderOrderDetailsList = mallOrderClientService.queryDetailByOrderCode(orderCode).getData();
        List<OrderOrderDetails> removeList = new ArrayList<>();
        for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
            Merchants merchants = merchantsClientService.getMerchantByCode(orderOrderDetails.getMerchantCode()).getData();
            OrderCategorys orderCategorys = orderCategoriesClientService.queryLevelOneCode(orderOrderDetails.getProductionCategoryCode(), merchants.getBusinessSubjects()).getData();
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                Integer quantity = cardMapUserClientService.queryCountByRefKeyAndState(orderOrderDetails.getId(), CardUserMallConstant.MALL_BUY_UN_USE_STATE).getData();
                //封装图片
                List<MallProductions> mallProductionsList = mallAppShowClientService.queryByCodeAndMerchant(orderOrderDetails.getProductionCode(), orderOrderDetails.getMerchantCode()).getData();
                if (mallProductionsList != null && mallProductionsList.size() > 0) {
                    orderOrderDetails.setProductionUrl(mallProductionsList.get(0).getProductionUrl());
                } else {
                    orderOrderDetails.setProductionUrl("https://hlta-allinpay.oss-cn-shenzhen.aliyuncs.com/%E4%BC%98%E6%83%A0%E5%88%B811.png?Expires=1914476742&OSSAccessKeyId=LTAI4GDESiBWHwcNHPYTHaDt&Signature=60vNmK406bWDLrSgRneQAzWN8yw%3D");
                }

                if (quantity <= 0) {
//                    removeList.add(orderOrderDetails);
                    Integer refundingCount = cardMapUserClientService.queryCountByRefKeyAndState(orderOrderDetails.getId(), CardUserMallConstant.REFUND_ING).getData();
                    if (refundingCount>0){
                        orderOrderDetails.setRefundState("refund");
                    }else {
                        orderOrderDetails.setRefundState("not_refund");
                    }
                } else {
                    Integer totalDiscount = orderOrderDetails.getDiscount();
                    int totalQuantity = orderOrderDetails.getQuantity().intValue();
                    Integer totalAmount = orderOrderDetails.getAmount();
                    int oneDiscount = totalDiscount / totalQuantity;
                    int oneAmount = totalAmount / totalQuantity;
                    int useDiscount = oneDiscount * quantity;
                    int useAmount = oneAmount * quantity;
                    orderOrderDetails.setQuantity(new BigDecimal(quantity));
                    orderOrderDetails.setAmount(useAmount - useDiscount);
                    orderOrderDetails.setRefundState("refund");
                }

                if (OrderConstant.REFUND.equals(orderOrderDetails.getState()) || OrderConstant.REFUND_ING.equals(orderOrderDetails.getState()) || OrderConstant.REFUND_NOT.equals(orderOrderDetails.getState())){
                    orderOrderDetails.setRefundState("refund");
                    orderOrderDetails.setProcessState("have_process");
                }else {
                    orderOrderDetails.setProcessState("not_process");
                }

            }
        }
//        for (OrderOrderDetails orderOrderDetails : removeList) {
//            orderOrderDetailsList.remove(orderOrderDetails);
//        }
        return orderOrderDetailsList;
    }

    /**
     * check 一笔订单是否还有可退明细
     * @param orderCode
     * @return
     */
    public boolean checkRefundOrderDetails(String orderCode) {
        List<OrderOrderDetails> orderOrderDetailsList = mallOrderClientService.queryDetailByOrderCode(orderCode).getData();
        List<OrderOrderDetails> removeList = new ArrayList<>();
        for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
            OrderCategorys orderCategorys = orderCategoriesClientService.queryLevelOneCode(orderOrderDetails.getProductionCategoryCode(), orderOrderDetails.getMerchantCode()).getData();
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                Integer quantity = cardMapUserClientService.queryCountByRefKeyAndState(orderOrderDetails.getId(), CardUserMallConstant.MALL_BUY_UN_USE_STATE).getData();
                if (quantity <= 0) {
                    removeList.add(orderOrderDetails);
                }
            }
        }
        for (OrderOrderDetails orderOrderDetails : removeList) {
            orderOrderDetailsList.remove(orderOrderDetails);
        }
        if (orderOrderDetailsList.size()>0){
            return true;
        }else {
            return false;
        }
    }
}
