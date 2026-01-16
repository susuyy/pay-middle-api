package com.ht.feignapi.pay.service;

import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.entity.OrderOrders;
import com.ht.feignapi.mall.entity.PaySuccess;
import com.ht.feignapi.mall.service.MallOrderPayService;
import com.ht.feignapi.prime.service.PrimePayService;
import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.TreeMap;

@Service
public class PrimeAllinpayService {

    private Logger logger = LoggerFactory.getLogger(PrimeAllinpayService.class);


    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private PrimePayService primePayService;

    /**
     * 异步回调处理 (商城购买商品)
     *
     * @param params
     */
    public void notifyMallBuy(TreeMap<String, String> params) {
        try {
            String orderCode = "";
            if (!StringUtils.isEmpty(params.get("cusorderid"))) {
                orderCode = params.get("cusorderid");
            } else if (!StringUtils.isEmpty(params.get("reqsn")) && StringUtils.isEmpty(orderCode)) {
                orderCode = params.get("reqsn");
            }
            CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
            if (cardOrdersVO != null) {
                if (!StringUtils.isEmpty(cardOrdersVO.getOrderCode())) {
                    if (CardOrdersStateConfig.PAID.equals(cardOrdersVO.getState())) {
                        return;
                    }
                    PaySuccess paySuccess = new PaySuccess();
                    paySuccess.setMerchantCode(cardOrdersVO.getMerchantCode());
                    paySuccess.setUserId(cardOrdersVO.getUserId().toString());
                    paySuccess.setOrderCode(cardOrdersVO.getOrderCode());
                    paySuccess.setPayCode(params.get("trxid"));
                    paySuccess.setPayDate(params.get("paytime"));
                    //商城购物成功处理业务逻辑
                    primePayService.primeBuyCardSuccess(paySuccess);
                }
            }
        } catch (Exception e) {//处理异常
            logger.info(e.getMessage());
            logger.error(e.getMessage() + "业务代码异常");
        }
    }

    /**
     * 获取 主体 商户编码
     * @param params
     * @return
     */
    public String getObjectMerchantCode(TreeMap<String, String> params) {
        String orderCode = "";
        if (!StringUtils.isEmpty(params.get("cusorderid"))) {
            orderCode = params.get("cusorderid");
        } else if (!StringUtils.isEmpty(params.get("reqsn")) && StringUtils.isEmpty(orderCode)) {
            orderCode = params.get("reqsn");
        }
        logger.info("同步回调的订单号为:"+orderCode);
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
        String merchantCode = cardOrdersVO.getMerchantCode();
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        return merchants.getBusinessSubjects();
    }
}
