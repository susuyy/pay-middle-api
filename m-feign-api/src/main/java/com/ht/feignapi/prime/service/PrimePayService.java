package com.ht.feignapi.prime.service;



import com.ht.feignapi.mall.entity.OrderRefundData;
import com.ht.feignapi.mall.entity.PaySuccess;
import com.ht.feignapi.mall.entity.RetRefundOrderData;
import com.ht.feignapi.pay.client.PayProjectClient;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PrimePayService {

    private Logger logger = LoggerFactory.getLogger(PrimePayService.class);

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private PayProjectClient payProjectClient;



    /**
     * 免税买电子卡支付成功业务逻辑
     * @param paySuccess
     */
    public void primeBuyCardSuccess(PaySuccess paySuccess) {
        //清空缓存订单
        redisTemplate.opsForHash().delete("orderInventory", paySuccess.getOrderCode());
        //修改订单状态 获取订单明细
        List<CardOrderDetails> cardOrderDetailsList = orderClientService.updatePrimeBuyCardState(paySuccess.getOrderCode(),paySuccess.getPayCode()).getData();
        //用户绑定卡
        List<CardElectronic> cardElectronicList = msPrimeClient.buyCardSuccessBindUser(cardOrderDetailsList, paySuccess.getUserId()).getData();
        UpdateCardNoData updateCardNoData = new UpdateCardNoData();
        updateCardNoData.setCardElectronicList(cardElectronicList);
        updateCardNoData.setCardOrderDetailsList(cardOrderDetailsList);
        //回写订单明细卡号
        orderClientService.primeBuyCardUpdateCardNo(updateCardNoData);
    }

    /**
     * 退款调用
     * @param merchantCode
     * @param amount
     * @param backOrderCode
     * @param oriOrderCode
     * @return
     */
    public RetRefundOrderData primeBuyCardOrderRefund(String merchantCode, Integer amount, String backOrderCode, String oriOrderCode){
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

    public void doBuyCardOrderRefund(String backOrderCode, CardOrdersVO cardOrdersVO, String operator, RefundCardDetail refundCardDetail) {
        DoRefundData doRefundData = new DoRefundData();
        doRefundData.setCardOrdersVO(cardOrdersVO);
        doRefundData.setRefundCardDetail(refundCardDetail);

        msPrimeClient.doBuyCardOrderRefund(backOrderCode,operator,doRefundData);
    }
}
