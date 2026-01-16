package com.ht.feignapi.pay.controller;


import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.entity.OrderOrders;
import com.ht.feignapi.mall.entity.PaySuccess;
import com.ht.feignapi.mall.service.MallOrderPayService;
import com.ht.feignapi.pay.client.PayProjectClient;
import com.ht.feignapi.pay.service.MallAllinpayService;
import com.ht.feignapi.pay.service.PrimeAllinpayService;
import com.ht.feignapi.pay.service.SybPayService;
import com.ht.feignapi.prime.service.PrimePayService;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderClientService;
import com.ht.feignapi.tonglian.card.entity.CardOrders;
import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.util.DateStrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/ms/primeAllinpay")
@CrossOrigin(allowCredentials = "true")
public class PrimeAllinpayController {

    private Logger logger = LoggerFactory.getLogger(PrimeAllinpayController.class);

    @Autowired
    private PrimeAllinpayService primeAllinpayService;

    @Autowired
    private SybPayService sybPayService;

    @Autowired
    private PayProjectClient payProjectClient;

    @Autowired
    private OrderClientService orderClientService;


    @Value("${payRedirect}")
    private String payRedirectUrl;

    @Autowired
    private PrimePayService primePayService;


    /**
     * 同步回调接口(商城购物支付使用)
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping("/returlMallBuy")
    public void returlMallBuy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String objectMerchantCode = "";
        try {
            logger.info("同步回调==============进入接口/returlMallBuy方法returlMallBuy");
            request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
            response.setCharacterEncoding("UTF-8");
            TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
            logger.info("同步回调接收到的数据 : "+params);
            objectMerchantCode = primeAllinpayService.getObjectMerchantCode(params);
            logger.info("同步回调路径为 : "+payRedirectUrl+"?merchantCode="+objectMerchantCode);

            String orderCode = "";
            if (!StringUtils.isEmpty(params.get("cusorderid"))) {
                orderCode = params.get("cusorderid");
            } else if (!StringUtils.isEmpty(params.get("reqsn")) && StringUtils.isEmpty(orderCode)) {
                orderCode = params.get("reqsn");
            }
            //反查订单
            Map map = payProjectClient.queryAllInPayOrder(orderCode, params.get("trxid"), params.get("cusid"), params.get("appid"), objectMerchantCode).getData();
            CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
            if (cardOrdersVO != null && "0000".equals(map.get("trxstatus"))) {
                if (!StringUtils.isEmpty(cardOrdersVO.getOrderCode())) {
                    if (CardOrdersStateConfig.PAID.equals(cardOrdersVO.getState())) {
                        logger.info("该订单已支付,直接跳转");
                        response.sendRedirect(payRedirectUrl+"?merchantCode="+objectMerchantCode);
                        response.flushBuffer();
                        return;
                    }
                    PaySuccess paySuccess = new PaySuccess();
                    paySuccess.setMerchantCode(cardOrdersVO.getMerchantCode());
                    paySuccess.setUserId(cardOrdersVO.getUserId().toString());
                    paySuccess.setOrderCode(cardOrdersVO.getOrderCode());
                    paySuccess.setPayCode(params.get("trxid"));
                    String nowDateStr = DateStrUtil.dateToStr(new Date());
                    paySuccess.setPayDate(nowDateStr);
                    //成功处理业务逻辑
                    primePayService.primeBuyCardSuccess(paySuccess);
                }
            }
            logger.info("异步回调,未及时更新订单,同步回调已处理");
            response.sendRedirect(payRedirectUrl+"?merchantCode="+objectMerchantCode);
            response.flushBuffer();
            return;
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            response.sendRedirect(payRedirectUrl+"?merchantCode="+objectMerchantCode);
            response.flushBuffer();
        }
    }

    /**
     * 支付成功异步回调接口,商城购物支付使用
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/notifyMallBuy")
    public void notifyMallBuy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("/notifyMallBuy/接口方法notifyMallBuy===接收到通知");
        request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
        response.setCharacterEncoding("UTF-8");
        TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容

        //获取交易标识码判断交易是否成功
        String trxstatus = params.get("trxstatus");
        if (!"0000".equals(trxstatus)){
            logger.info("系统订单号为:"+params.get("cusorderid")+",流水号trxid为:"+params.get("trxid")+",交易失败返回码trxstatus="+trxstatus);
            response.getOutputStream().write("fail".getBytes());
            response.flushBuffer();
        }

        primeAllinpayService.notifyMallBuy(params);
        response.getOutputStream().write("success".getBytes());
        response.flushBuffer();
    }


}
