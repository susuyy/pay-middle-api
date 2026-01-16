package com.ht.feignapi.pay.service;

import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.entity.OrderOrders;
import com.ht.feignapi.mall.entity.PaySuccess;
import com.ht.feignapi.mall.service.MallOrderPayService;
import com.ht.feignapi.pay.constants.SybConstants;
import com.ht.feignapi.pay.utils.SybUtil;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.TreeMap;

@Service
public class MallAllinpayService {
    private Logger logger = LoggerFactory.getLogger(MallAllinpayService.class);

    @Autowired
    private MallOrderPayService mallOrderPayService;

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

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
            OrderOrders orderOrders = mallOrderClientService.queryOrderByOrderCode(orderCode).getData();
            if (orderOrders != null) {
                if (!StringUtils.isEmpty(orderOrders.getOrderCode())) {
                    if (OrderConstant.PAID_UN_USE_STATE.equals(orderOrders.getState())) {
                        return;
                    }
                    PaySuccess paySuccess = new PaySuccess();
                    paySuccess.setMerchantCode(orderOrders.getMerchantCode());
                    paySuccess.setUserId(orderOrders.getUserId().toString());
                    paySuccess.setOrderCode(orderOrders.getOrderCode());
                    paySuccess.setPayCode(params.get("trxid"));
                    paySuccess.setPayDate(params.get("paytime"));
                    //商城购物成功处理业务逻辑
                    mallOrderPayService.mallBuySuccess(paySuccess);
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
        OrderOrders orderOrders = mallOrderClientService.queryOrderByOrderCode(orderCode).getData();
        String merchantCode = orderOrders.getMerchantCode();
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        return merchants.getBusinessSubjects();
    }

}
