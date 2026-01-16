package com.ht.feignapi.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.mall.clientservice.MallOrderClientService;

import com.ht.feignapi.mall.entity.OrderOrders;
import com.ht.feignapi.mall.service.MallOrderPayService;
import com.ht.feignapi.pay.constants.SybConstants;
import com.ht.feignapi.pay.controller.MallAllinpayController;
import com.ht.feignapi.pay.entity.PayConfig;
//import com.ht.feignapi.pay.utils.HttpConnectionUtil;
import com.ht.feignapi.pay.utils.SybUtil;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.tonglian.order.entity.PaySuccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class AllinpayService {

    private Logger logger = LoggerFactory.getLogger(AllinpayService.class);

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private InventoryClientService inventoryClientService;

    @Autowired
    private AuthClientService authClientService;

    /**
     * 当面付 异步 接口回调 处理业务逻辑
     *
     * @param params
     */
    public void notifyCusPay(TreeMap<String, String> params) {
        try {
            String orderCode = params.get("bizseq");
            //获取公钥
            logger.info("获取到的订单编号(bizseq)===============" + params.get("bizseq"));
            CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
            if (cardOrdersVO != null) {
                if (!StringUtils.isEmpty(cardOrdersVO.getOrderCode())) {
                    if ("paid".equals(cardOrdersVO.getState())) {
                        return;
                    }
                    PaySuccess paySuccess = new PaySuccess();
                    paySuccess.setMerchantCode(cardOrdersVO.getMerchantCode());
                    paySuccess.setUserId(cardOrdersVO.getUserId().toString());
                    paySuccess.setOrderCode(cardOrdersVO.getOrderCode());
                    paySuccess.setPayCode(params.get("trxid"));
                    paySuccess.setPayDate(params.get("paytime"));
                    orderClientService.updateCashCardOrder(paySuccess);
                }
            }
        } catch (Exception e) {//处理异常
            e.printStackTrace();
            logger.error(e.getMessage() + "业务代码异常");
        }
    }

    /**
     * 通联 优惠券 购买卡券 异步接口回调 处理业务逻辑
     *
     * @param params
     */
    public void notifyBuyCard(TreeMap<String, String> params) throws Exception {
        String orderCode = "";
        if (!StringUtils.isEmpty(params.get("cusorderid"))) {
            orderCode = params.get("cusorderid");
        } else if (!StringUtils.isEmpty(params.get("reqsn")) && StringUtils.isEmpty(orderCode)) {
            orderCode = params.get("reqsn");
        }
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
        if (cardOrdersVO != null) {
            if (!StringUtils.isEmpty(cardOrdersVO.getOrderCode())) {
                logger.info("查询的订单数据==========");
                logger.info(cardOrdersVO.toString());
                logger.info("==============");
                if ("paid".equals(cardOrdersVO.getState())) {
                    return;
                }
                PaySuccess paySuccess = new PaySuccess();
                paySuccess.setMerchantCode(cardOrdersVO.getMerchantCode());
                paySuccess.setUserId(cardOrdersVO.getUserId().toString());
                paySuccess.setOrderCode(cardOrdersVO.getOrderCode());
                paySuccess.setPayCode(params.get("trxid"));
                paySuccess.setPayDate(params.get("paytime"));
                paySuccess.setUserId(cardOrdersVO.getUserId().toString());

                orderClientService.buyCardPaySuccess(paySuccess);
                Map<String, Integer> map = new HashMap<>();
                map.put("amount", 1);
                for (CardOrderDetails details: cardOrdersVO.getCardOrderDetailsList()) {
                    inventoryClientService.subtractInventory(cardOrdersVO.getMerchantCode(),details.getProductionCode(), map);
                }
            }
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
        CardOrdersVO ordersVO = orderClientService.queryByOrderCode(orderCode).getData();
        String merchantCode = ordersVO.getMerchantCode();
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        return merchants.getBusinessSubjects();
    }

    /**
     * C扫B  H5收银模式 异步回调 处理业务逻辑
     * @param params
     */
    public void notifyH5Pay(TreeMap<String, String> params) {
        try {
            String orderCode = "";
            if (!StringUtils.isEmpty(params.get("cusorderid"))) {
                orderCode = params.get("cusorderid");
            } else if (!StringUtils.isEmpty(params.get("reqsn")) && StringUtils.isEmpty(orderCode)) {
                orderCode = params.get("reqsn");
            }
            logger.info("获取到的订单编号(orderCode)===============" + orderCode);
            CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
            if (cardOrdersVO != null) {
                if (!StringUtils.isEmpty(cardOrdersVO.getOrderCode())) {
                    if ("paid".equals(cardOrdersVO.getState())) {
                        return;
                    }
                    PaySuccess paySuccess = new PaySuccess();
                    paySuccess.setMerchantCode(cardOrdersVO.getMerchantCode());
                    paySuccess.setUserId(cardOrdersVO.getUserId().toString());
                    paySuccess.setOrderCode(cardOrdersVO.getOrderCode());
                    paySuccess.setPayCode(params.get("trxid"));
                    paySuccess.setPayDate(params.get("paytime"));
                    //todo 修改方法
                    orderClientService.updateCashCardOrder(paySuccess);
                }
            }
        } catch (Exception e) {//处理异常
            e.printStackTrace();
            logger.error(e.getMessage() + "业务代码异常");
        }
    }

    public void notifyTopUp(TreeMap<String, String> params) {
        String orderCode = "";
        if (!StringUtils.isEmpty(params.get("cusorderid"))) {
            orderCode = params.get("cusorderid");
        } else if (!StringUtils.isEmpty(params.get("reqsn")) && StringUtils.isEmpty(orderCode)) {
            orderCode = params.get("reqsn");
        }
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
        if (cardOrdersVO != null) {
            if (!StringUtils.isEmpty(cardOrdersVO.getOrderCode())) {
                logger.info("查询的订单数据==========");
                logger.info(cardOrdersVO.toString());
                logger.info("==============");
                if ("paid".equals(cardOrdersVO.getState())) {
                    return;
                }
                PaySuccess paySuccess = new PaySuccess();
                paySuccess.setMerchantCode(cardOrdersVO.getMerchantCode());
                paySuccess.setUserId(cardOrdersVO.getUserId().toString());
                paySuccess.setOrderCode(cardOrdersVO.getOrderCode());
                paySuccess.setPayCode(params.get("trxid"));
                paySuccess.setPayDate(params.get("paytime"));
                paySuccess.setUserId(cardOrdersVO.getUserId().toString());
                UserUsers userUsers = authClientService.getUserByIdTL(cardOrdersVO.getUserId().toString()).getData();
                paySuccess.setUserTel(userUsers.getTel());
                orderClientService.topUpPaySuccess(paySuccess);
            }
        }
    }
}
