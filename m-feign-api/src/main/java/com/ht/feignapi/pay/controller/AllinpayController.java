package com.ht.feignapi.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.entity.OrderOrders;
import com.ht.feignapi.pay.client.PayProjectClient;
import com.ht.feignapi.pay.service.AllinpayService;
import com.ht.feignapi.pay.service.SybPayService;

import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.tonglian.order.entity.PaySuccess;
import com.ht.feignapi.util.DateStrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/tonglian/allinpay")
@CrossOrigin(allowCredentials = "true")
public class AllinpayController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SybPayService sybPayService;

    @Autowired
    private AllinpayService allinpayService;

    @Autowired
    private PayProjectClient payProjectClient;

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private AuthClientService authClientService;

    /**
     * 支付成功同步回调接口,C端通联 购买优惠券同步回调
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/retUrlBuyCard")
    public void retUrlBuyCard(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("同步回调==============进入接口/retUrlBuyCard");
        request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
        response.setCharacterEncoding("UTF-8");
        TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
        String objectMerchantCode = allinpayService.getObjectMerchantCode(params);
        response.sendRedirect("https://allinpay-web-v2.hualta.com/couponRedirect.html?merchantCode="+objectMerchantCode);
        response.flushBuffer();
    }

    /**
     * 购买卡券支付回调
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/notifyBuyCard")
    public void notifyBuyCard(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("/notifyBuyCard/接口方法,接收到通知");
        request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
        response.setCharacterEncoding("UTF-8");
        TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
        logger.info("异步回调接收到的参数:=====");
        logger.info(params.toString());
        logger.info("====================");
        try {
            allinpayService.notifyBuyCard(params);
        } catch (Exception e) {//处理异常
            e.printStackTrace();
            logger.error(e.getMessage()+"业务代码异常");
        } finally {//收到通知,返回success
            response.getOutputStream().write("success".getBytes());
            response.flushBuffer();
        }
    }


    /**
     * 支付成功同步回调接口,C端组合支付回调,当面付
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/retCuspay")
    public void retCuspay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("/retCuspay/接口方法===接收到通知");
        request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
        response.setCharacterEncoding("UTF-8");
        TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
        logger.info("异步回调接收到的参数:=====");
        logger.info(params.toString());
        logger.info("====================");
        response.getOutputStream().write("success".getBytes());
        response.flushBuffer();
    }

    /**
     * 支付成功异步回调接口,C端组合支付回调,当面付
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/notifyCuspay")
    public void notifyCuspay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("/notifyCuspay/接口方法notify===接收到通知");
        request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
        response.setCharacterEncoding("UTF-8");
        TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
        logger.info("异步回调接收到的参数:=====");
        logger.info(params.toString());
        logger.info("====================");
        allinpayService.notifyCusPay(params);
        response.getOutputStream().write("success".getBytes());
        response.flushBuffer();
    }


    /**
     * c扫B组合支付同步回调 H5收银用
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/retH5Pay")
    public void retH5Pay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("同步回调==============进入接口/retH5Pay");
        request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
        response.setCharacterEncoding("UTF-8");
        TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
        String objectMerchantCode = allinpayService.getObjectMerchantCode(params);
        //todo封装 openid 等等
        response.sendRedirect("https://allinpay-web-v2.hualta.com/couponRedirect.html?merchantCode="+objectMerchantCode);
        response.flushBuffer();
    }

    /**
     * c扫B组合支付异步回调 H5收银用
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/notifyH5Pay")
    public void notifyH5Pay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("/notifyH5Pay/接口方法,接收到通知");
        request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
        response.setCharacterEncoding("UTF-8");
        TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
        logger.info("异步回调接收到的参数:=====");
        logger.info(params.toString());
        logger.info("====================");
        try {
            allinpayService.notifyH5Pay(params);
        } catch (Exception e) {//处理异常
            e.printStackTrace();
            logger.error(e.getMessage()+"业务代码异常");
        } finally {//收到通知,返回success
            response.getOutputStream().write("success".getBytes());
            response.flushBuffer();
        }
    }

    /**
     * 测试通商云异步回调
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/testTSYNotify")
    public void testTSYNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("测试通商云异步回调==================");
        Map reqMap = request.getParameterMap();
        String s = JSONObject.toJSONString(reqMap);
        logger.info(reqMap.toString());
        logger.info("异步回调数据json串"+s);
    }

    /**
     * 充值支付 同步回调
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/retUrlTopUp")
    public void retUrlTopUp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("同步回调==============进入接口/retUrlBuyCard");
        request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
        response.setCharacterEncoding("UTF-8");
        TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
        String objectMerchantCode = allinpayService.getObjectMerchantCode(params);
        //反查订单
        String orderCode = "";
        if (!StringUtils.isEmpty(params.get("cusorderid"))) {
            orderCode = params.get("cusorderid");
        } else if (!StringUtils.isEmpty(params.get("reqsn")) && StringUtils.isEmpty(orderCode)) {
            orderCode = params.get("reqsn");
        }
        Map map = payProjectClient.queryAllInPayOrder(orderCode, params.get("trxid"), params.get("cusid"), params.get("appid"), objectMerchantCode).getData();
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
        if (cardOrdersVO != null && "0000".equals(map.get("trxstatus"))) {
            if (!StringUtils.isEmpty(cardOrdersVO.getOrderCode())) {
                if (CardOrdersStateConfig.PAID.equals(cardOrdersVO.getState())) {
                    logger.info("该订单已支付,直接跳转");
                    response.sendRedirect("https://allinpay-web-v2.hualta.com/couponRedirect.html?merchantCode="+objectMerchantCode);
                    response.flushBuffer();
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
        logger.info("异步回调,未及时更新订单,同步回调已处理");
        response.sendRedirect("https://allinpay-web-v2.hualta.com/couponRedirect.html?merchantCode="+objectMerchantCode);
        response.flushBuffer();
    }

    /**
     * 充值支付异步回调
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/notifyTopUp")
    public void notifyTopUp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("/notifyBuyCard/接口方法,接收到通知");
        request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
        response.setCharacterEncoding("UTF-8");
        TreeMap<String, String> params = sybPayService.getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
        logger.info("异步回调接收到的参数:=====");
        logger.info(params.toString());
        logger.info("====================");
        try {
            allinpayService.notifyTopUp(params);
        } catch (Exception e) {//处理异常
            e.printStackTrace();
            logger.error(e.getMessage()+"业务代码异常");
        } finally {//收到通知,返回success
            response.getOutputStream().write("success".getBytes());
            response.flushBuffer();
        }
    }
}
