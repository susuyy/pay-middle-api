package com.ht.feignapi.tonglian.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.entity.OrderOrders;
import com.ht.feignapi.mall.entity.OrderWayBills;
import com.ht.feignapi.prime.controller.PrimePayController;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderPayTraceClientService;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.card.service.CardUserService;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceStateConfig;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceTypeConfig;
import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.config.CardOrdersTypeConfig;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.*;
import com.ht.feignapi.tonglian.order.service.MisOrderService;
import com.ht.feignapi.tonglian.user.entity.PosSelectCardNo;
import com.ht.feignapi.tonglian.user.entity.RetCalculationData;
import com.ht.feignapi.util.DESUtil;
import com.ht.feignapi.util.DateStrUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tonglian/orders")
@CrossOrigin(allowCredentials = "true")
public class OrderApiController {

    private Logger logger = LoggerFactory.getLogger(OrderApiController.class);

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private InventoryClientService inventoryClientService;

    @Autowired
    private MisOrderService misOrderService;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private DESUtil desUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据订单号查询订单
     *
     * @param orderCode
     * @return
     */
    @GetMapping("/queryByOrderCode/{orderCode}")
    public CardOrdersVO queryByOrderCode(@PathVariable("orderCode") String orderCode) {
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
        return cardOrdersVO;
    }

    /**
     * C端公众号,支付成功修改订单数据(组合支付)
     *
     * @param paySuccess
     * @return
     */
    @PostMapping("/updateCashCardOrder")
    public Boolean updateCashCardOrder(@RequestBody PaySuccess paySuccess) {
        Boolean flag = orderClientService.updateCashCardOrder(paySuccess).getData();
        return flag;
    }

    /**
     * C端公众号,支付成功修改订单数据(购买卡券)
     *
     * @param paySuccess
     * @return
     */
    @PostMapping("/paySuccess")
    public Boolean buyCardPaySuccess(@RequestBody PaySuccess paySuccess) {
        List<CardOrderDetails> list = orderClientService.buyCardPaySuccess(paySuccess).getData();
        Map<String, Integer> map = new HashMap<>();
        map.put("amount", 1);
        for (CardOrderDetails cardOrderDetails : list) {
            inventoryClientService.subtractInventory(paySuccess.getMerchantCode(), cardOrderDetails.getProductionCode(), map);
        }
        return !CollectionUtils.isEmpty(list);
    }

    /**
     * 分页获取主体订单
     *
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @param orderSearch
     * @return
     */
    @GetMapping("/objectAllOrders/{merchantCode}")
    public IPage<OrderOrders> getObjectAllOrders(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Long pageSize,
            @ModelAttribute OrderSearch orderSearch) {
        Result<List<Merchants>> merchantsResult = merchantsClientService.getSubMerchants(merchantCode);
        if (merchantsResult != null && !CollectionUtils.isEmpty(merchantsResult.getData())) {
            List<Merchants> merchantsList = merchantsResult.getData();
            List<String> merchantCodes = merchantsList.stream().map(Merchants::getMerchantCode).collect(Collectors.toList());
            IPage<OrderOrders> ordersPage = mallOrderClientService.selectOrderPage(merchantCodes, pageNo, pageSize, orderSearch).getData();
            ordersPage.getRecords().forEach(e -> {
                Merchants merchants = merchantsClientService.getMerchantByCode(e.getMerchantCode()).getData();
                e.setMerchantName(merchants.getMerchantName());

                UserUsers userUsers = authClientService.getUserByIdTL(e.getUserId().toString()).getData();
                e.setUser(userUsers);

                e.getOrderDetails().forEach(o -> {
                    Result<OrderWayBills> wayBillsResult = mallOrderClientService.getOrderDetailWayBillState(o.getOrderCode(), o.getMerchantCode());
                    if (wayBillsResult != null && wayBillsResult.getData() != null) {
                        o.setOrderWayBill(wayBillsResult.getData());
                    } else {
                        o.setOrderWayBill(new OrderWayBills());
                    }
                });
            });
            return ordersPage;
        }
        return new Page<>();
    }

//    /**
//     * 上送云mis
//     *
//     * @param misOrder
//     * @return
//     */
//    @PostMapping("/cloudMisSendOrder")
//    public ReturnMisOrder cloudMisSendOrder(@RequestBody MisOrder misOrder) throws Exception {
//        String appId = misOrder.getAPP_ID();
//        String businessId = misOrder.getBUSINESS_ID();
//        String cashId = misOrder.getCASH_ID();
//        String custDataStr = misOrder.getCUST_DATA();
//        String signData = misOrder.getSIGN_DATA();
//        CustData custData = JSONObject.parseObject(custDataStr, CustData.class);
//
//        boolean checkFlag = misOrderService.checkMD5Sign(misOrder);
//        if (!checkFlag){
//            throw new CheckException(ResultTypeEnum.MD5_SIGN_ERROR);
//        }
//
//        //发送数据 到消息队列
//        boolean flag = misOrderService.sendMisOrderMessage("POS-" + cashId, misOrder);
////        boolean flag =true;
//        //响应调用端
//        RspData rspData = misOrderService.packageRspData(misOrder, custData);
//        ReturnMisOrder returnMisOrder = new ReturnMisOrder();
//        returnMisOrder.setAPP_ID(appId);
//        returnMisOrder.setBUSINESS_ID(businessId);
//        returnMisOrder.setRSP_DATA(JSONObject.toJSONString(rspData));
//
//        //创建mis订单数据
//        String cloudMisTrxSsn = cardOrderPayTraceClientService.createMisOrderPayTrace(misOrder).getData();
//
//        returnMisOrder.setClOUD_MIS_TRX_SSN(cloudMisTrxSsn);
//
//        if (flag) {
//            returnMisOrder.setRSP_CODE("Y0000");
//            returnMisOrder.setRSP_DESC("云MIS服务调用成功");
//        } else {
//            returnMisOrder.setRSP_CODE("E0007");
//            returnMisOrder.setRSP_DESC("系统故障");
//        }
//
//        String resMD5Sign = misOrderService.createResponseMD5Sign(returnMisOrder);
//        returnMisOrder.setSIGN_DATA(resMD5Sign);
//        return returnMisOrder;
//    }


    /**
     * 拉取 云mis 订单数据
     *
     * @param cashId
     * @return
     */
    @GetMapping("/pullMisOrder")
    public ResponseMisOrderData pullMisOrder(@RequestParam("cashId") String cashId) {
        if ("null".equals(cashId) || "undefined".equals(cashId) || StringUtils.isEmpty(cashId)) {
            throw new CheckException(ResultTypeEnum.CASH_ID_ERROR.getCode(), "收银机款台号为:" + cashId);
        }

        List<CardOrderPayTrace> orderPayTraceList = orderClientService.checkHaveOrder(cashId).getData();

        if (orderPayTraceList==null || orderPayTraceList.size()<1){
            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
        }
        CardOrderPayTrace cardOrderPayTraceOne = orderPayTraceList.get(0);
//        List<CardOrderPayTrace> orderPayTraces = cardOrderPayTraceClientService.queryPayTrace(cardOrderPayTraceOne.getOrderCode()).getData();
//        int totalTracePayMoney = 0;
//        for (CardOrderPayTrace orderPayTrace : orderPayTraces) {
//            if ((CardOrdersStateConfig.PAID.equals(orderPayTrace.getState())|| CardOrdersStateConfig.REFUND.equals(orderPayTrace.getState())) && !CardOrdersTypeConfig.POS_MIS_ORDER.equals(orderPayTrace.getType())){
//                totalTracePayMoney = totalTracePayMoney + orderPayTrace.getAmount();
//            }
//        }
        CardOrders data = orderClientService.queryByOrderCodeNotDetail(cardOrderPayTraceOne.getOrderCode()).getData();
        if (data != null) {
            if (!CardOrdersStateConfig.UNPAID.equals(data.getState())) {
                throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
            }
        }else {
            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
        }
//        if (totalTracePayMoney >= data.getAmount()){
//            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
//        }

//        MisOrderData misOrderData  = misOrderService.pullMisOrder(cashId);

//        if (misOrderData != null) {
//            ResponseMisOrderData responseMisOrderData = new ResponseMisOrderData();
//            responseMisOrderData.setBusinessId(misOrderData.getBusinessId());
////            responseMisOrderData.setAmount(data.getAmount() - totalTracePayMoney);
//            responseMisOrderData.setAmount(data.getAmount());
//            responseMisOrderData.setOrderNo(misOrderData.getOrderCode());
//            return responseMisOrderData;
//        } else {
//            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
//        }
//        if (data != null) {
            ResponseMisOrderData responseMisOrderData = new ResponseMisOrderData();
            responseMisOrderData.setBusinessId("000050");
//            responseMisOrderData.setAmount(data.getAmount() - totalTracePayMoney);
            responseMisOrderData.setAmount(data.getAmount());
            responseMisOrderData.setOrderNo(data.getOrderCode());
            return responseMisOrderData;
//        } else {
//            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
//        }
    }

    /**
     * 上送云mis
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/misOrder")
    public ResponMisOrder misOrder(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        try {
            logger.info("上送的mis订单加密数据为:"+desDataStr.getDesDataStr());
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            logger.info("上送的mis订单解密字符串数据为:"+decryptDataStr);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        MisOrderData misOrderData = JSONObject.parseObject(decryptDataStr, MisOrderData.class);
        logger.info("上送的mis订单数据为:"+misOrderData);
        if (misOrderData==null){
            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL_ERROR);
        }

        //发送数据 到消息队列
//        boolean flag = misOrderService.sendMisOrderMessage("POS-" + misOrderData.getCashId(), misOrderData);

//        if (!flag){
//            throw new CheckException(ResultTypeEnum.SERVICE_ERROR);
//        }

        //响应调用端
        ResponMisOrder responMisOrder = new ResponMisOrder();
        responMisOrder.setOrderCode(misOrderData.getOrderCode());

        //创建mis订单数据
        CardOrderPayTrace cardOrderPayTrace = cardOrderPayTraceClientService.createMisOrderPayTrace(misOrderData).getData();

        responMisOrder.setCloudMisTrxSsn(StringUtils.isEmpty(misOrderData.getPayCode())? cardOrderPayTrace.getPayCode() : misOrderData.getPayCode());

        return responMisOrder;
    }

    /**
     * 上送云mis 测试页面
     *
     * @param
     * @return
     */
    @PostMapping("/misOrderTest")
    public ResponMisOrder misOrderTest(MisOrderData misOrderData) throws Exception {
        //发送数据 到消息队列
//        boolean flag = misOrderService.sendMisOrderMessage("POS-" + misOrderData.getCashId(), misOrderData);
//        if (!flag){
//            throw new CheckException(ResultTypeEnum.SERVICE_ERROR);
//        }
        //响应调用端
        ResponMisOrder responMisOrder = new ResponMisOrder();
        responMisOrder.setOrderCode(misOrderData.getOrderCode());

        //创建mis订单数据
        CardOrderPayTrace cardOrderPayTrace = cardOrderPayTraceClientService.createMisOrderPayTrace(misOrderData).getData();

        responMisOrder.setCloudMisTrxSsn(StringUtils.isEmpty(misOrderData.getPayCode())? cardOrderPayTrace.getPayCode() : misOrderData.getPayCode());

        return responMisOrder;
    }

}
