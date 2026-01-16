package com.ht.feignapi.prime.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.mall.entity.RetRefundOrderData;
import com.ht.feignapi.pay.client.PayProjectClient;
import com.ht.feignapi.prime.cardenum.CardElectronicEnum;
import com.ht.feignapi.prime.cardenum.PayTraceTypeSourceEnum;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.prime.excel.CardRefundOrderExcelData;
import com.ht.feignapi.prime.excel.ConsumeOrdersMasterExcelData;
import com.ht.feignapi.prime.service.PrimeOrderService;
import com.ht.feignapi.prime.service.PrimePayService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderPayTraceClientService;
import com.ht.feignapi.tonglian.card.entity.CardOrders;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceStateConfig;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceTypeConfig;
import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.config.CardOrdersTypeConfig;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.util.DateStrUtil;
import org.jacoco.agent.rt.internal_43f5073.core.internal.flow.IFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.smartcardio.Card;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.ht.feignapi.mall.constant.OrderConstant.REFUND;
import static com.ht.feignapi.tonglian.config.CardOrdersStateConfig.PAID;
import static com.ht.feignapi.tonglian.config.CardOrdersStateConfig.UNPAID;
import static com.ht.feignapi.tonglian.config.CardOrdersTypeConfig.CONSUME;

/**
 * @author: zheng weiguang
 * @Date: 2021/2/7 11:51
 */
@RestController
@RequestMapping("/ms/order")
@CrossOrigin(allowCredentials = "true")
public class PrimeOrderController {

    private Logger logger = LoggerFactory.getLogger(PrimeOrderController.class);

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private PrimePayService primePayService;

    @Autowired
    private PrimeOrderService primeOrderService;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;


    /**
     * 获取购卡订单列表
     *
     * @param pageNo
     * @param pageSize
     * @param phone 手机号
     * @param state 状态
     * @param cardNo 卡号
     * @param traceNo 交易流水号
     * @param orderNo 订单号
     * @param startTime  开始时间
     * @param endTime  结束时间
     * @return
     */
    @GetMapping
    public Page<CardOrders> getPrimeBuyCardOrders(
            @RequestParam(value = "phone",defaultValue = "",required = false) String phone,
            @RequestParam(value = "state",defaultValue = "",required = false) String state,
            @RequestParam(value = "cardNo",defaultValue = "",required = false) String cardNo,
            @RequestParam(value = "traceNo",defaultValue = "",required = false) String traceNo,
            @RequestParam(value = "pageNo",defaultValue = "0",required = false) Long pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) Long pageSize,
            @RequestParam(value = "orderNo",defaultValue = "",required = false) String orderNo,
            @RequestParam(value = "startTime", defaultValue = "1970-01-01", required = false) String startTime,
            @RequestParam(value = "endTime", defaultValue = "2200-01-01", required = false)  String endTime
    ){
        endTime = endTime + " 23:59:59";
        Long userId = getUserId(phone);
        Page<CardOrders> primeBuyCardOrders = primeOrderService.getPrimeBuyCardOrders(phone, state, cardNo, traceNo, pageNo, pageSize, orderNo, startTime, endTime, userId);
        return primeBuyCardOrders;
    }

    private Long getUserId(String phone) {
        if (!StringUtils.isEmpty(phone)) {
            Result<VipUser> userSearchResult = msPrimeClient.queryUserByPhone(phone);
            if (!userSearchResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode()) || ObjectUtils.isEmpty(userSearchResult.getData())) {
                return null;
            }
            return userSearchResult.getData().getId();
        }
        return null;
    }

    private String getUserOpenId(String phone){
        if (!StringUtils.isEmpty(phone)){
            Result<VipUser> userSearchResult = msPrimeClient.queryUserByPhone(phone);
            if (!userSearchResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode())||ObjectUtils.isEmpty(userSearchResult.getData())){
                return null;
            }
            return userSearchResult.getData().getOpenid();
        }
        return null;
    }

    /**
     * 获取核销订单列表
     *
     * @param pageNo
     * @param pageSize
     * @param phone     手机号
     * @param state     状态
     * @param cardNo    卡号
     * @param traceNo   交易流水号
     * @param orderNo   订单号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @GetMapping("/consumeOrder")
    public Page<CardOrders> getConsumeOrders(
            @RequestParam(value = "phone", defaultValue = "", required = false) String phone,
            @RequestParam(value = "state", defaultValue = "", required = false) String state,
            @RequestParam(value = "cardNo", defaultValue = "", required = false) String cardNo,
            @RequestParam(value = "traceNo", defaultValue = "", required = false) String traceNo,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Long pageSize,
            @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
            @RequestParam(value = "startTime", defaultValue = "1970-01-01", required = false) String startTime,
            @RequestParam(value = "endTime", defaultValue = "2200-01-01", required = false)  String endTime
    ) {
        endTime = endTime + " 23:59:59";
        Long userId = getUserId(phone);
        String userOpenId = getUserOpenId(phone);
        Page<CardOrders> consumeOrders = primeOrderService.getConsumeOrders(userOpenId, state, cardNo, traceNo, pageNo, pageSize, orderNo, startTime, endTime, userId);
        return consumeOrders;
    }

    /**
     * 获取用户订单列表
     *
     * @param pageNo
     * @param pageSize
     * @param openId
     * @return
     */
    @GetMapping("/queryUserOrder")
    public Page<CardOrders> getOrders(
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) Long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Long pageSize,
            @RequestParam("openId") String openId
    ) {
        VipUser vipUser = msPrimeClient.queryByOpenId(openId).getData();
        if (vipUser == null || StringUtils.isEmpty(vipUser.getPhoneNum())){
            return new Page<CardOrders>();
        }
        Result<Page<CardOrders>> ordersResult = orderClientService.getOrderList(pageNo, pageSize, vipUser.getPhoneNum(),vipUser.getId());
        if (!ordersResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode()) || ObjectUtils.isEmpty(ordersResult.getData())) {
            return new Page<CardOrders>();
        }
        return ordersResult.getData();
    }

    /**
     * 获取用户订单列表
     *
     * @param pageNo
     * @param pageSize
     * @param userFlag
     * @return
     */
    @GetMapping("/queryUserConsumeOrder")
    public Page<CardOrderPayTrace> queryUserConsumeOrder(
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) Long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Long pageSize,
            @RequestParam("userFlag") String userFlag
    ) {
        VipUser vipUser = msPrimeClient.queryByOpenId(userFlag).getData();
        if (vipUser == null || StringUtils.isEmpty(vipUser.getPhoneNum())){
            return new Page<CardOrderPayTrace>();
        }
        Result<Page<CardOrderPayTrace>> ordersResult = cardOrderPayTraceClientService.queryUserConsumeOrder(pageNo, pageSize, vipUser.getPhoneNum(),userFlag);
        if (!ordersResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode()) || ObjectUtils.isEmpty(ordersResult.getData())) {
            return new Page<CardOrderPayTrace>();
        }
        return ordersResult.getData();
    }

    /**
     * 海旅购卡订单退款
     *
     * @param buyCardOrderRefundData
     */
    @PostMapping("/buyCardOrderRefund")
    public void buyCardOrderRefund(@RequestBody BuyCardOrderRefundData buyCardOrderRefundData) {
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(buyCardOrderRefundData.getOrderCode()).getData();
        if (CardOrdersStateConfig.UNPAID.equals(cardOrdersVO.getState())) {
            throw new CheckException(ResultTypeEnum.NOT_PAID);
        }
        RetCheckRefund retCheckRefund = msPrimeClient.checkRefundCardAndMoney(buyCardOrderRefundData.getCardNoList()).getData();
        List<RefundCardDetail> refundCardDetails = retCheckRefund.getRefundCardDetails();
        for (RefundCardDetail refundCardDetail : refundCardDetails) {
            String backOrderCode = IdWorker.getIdStr();
            RetRefundOrderData retRefundOrderData = primePayService.primeBuyCardOrderRefund(cardOrdersVO.getMerchantCode(),
                    Integer.parseInt(refundCardDetail.getRefundAmount() + ""),
                    backOrderCode, buyCardOrderRefundData.getOrderCode());
            if (!retRefundOrderData.isServerFlag()) {
                throw new CheckException(ResultTypeEnum.REFUND_ERROR.getCode(), retRefundOrderData.getErrmsg());
            }
            //修改退款订单状态
            orderClientService.updateBuyCardOrderRefundState(buyCardOrderRefundData.getOrderCode());
            //撤销用户购买电子卡 同时 新增退款订单 对应批次库存增加
            primePayService.doBuyCardOrderRefund(backOrderCode, cardOrdersVO, buyCardOrderRefundData.getOperator(), refundCardDetail);
        }
    }

    /**
     * 导出购卡订单excel
     * @param response
     * @param phone
     * @param state
     * @param cardNo
     * @param traceNo
     * @param orderNo
     * @param startTime
     * @param endTime
     */
    @GetMapping("/download")
    public void downloadOrdersWithCardInfo(
            HttpServletResponse response,
            @RequestParam(value = "phone", defaultValue = "", required = false) String phone,
            @RequestParam(value = "state", defaultValue = "", required = false) String state,
            @RequestParam(value = "cardNo", defaultValue = "", required = false) String cardNo,
            @RequestParam(value = "traceNo", defaultValue = "", required = false) String traceNo,
            @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime
    ) {
        if (StringUtils.isEmpty(phone)
                && StringUtils.isEmpty(state)
                && StringUtils.isEmpty(cardNo)
                &&StringUtils.isEmpty(traceNo)
                &&StringUtils.isEmpty(orderNo)){
            if (StringUtils.isEmpty(startTime)){
                startTime = DateStrUtil.nowDateStrYearMoonDay();
            }
            if (StringUtils.isEmpty(endTime)){
                endTime = DateStrUtil.nowDateStrYearMoonDay();
            }
            endTime = endTime + " 23:59:59";
        }else {
            if (!StringUtils.isEmpty(endTime)){
                endTime = endTime + " 23:59:59";
            }
        }
        Long userId = getUserId(phone);
        Result<List<PrimeBuyCardOrderExcelVo>> result;
        if (userId!=null) {
            result = orderClientService.getOrderExcelList(orderNo, startTime, endTime, state, userId,phone, CardOrdersTypeConfig.PRIME_BUY_CARD, cardNo, traceNo);
        }else {
            result = orderClientService.getOrderExcelListPhone(orderNo, startTime, endTime, state, phone, CardOrdersTypeConfig.PRIME_BUY_CARD, cardNo, traceNo);
        }

        List<PrimeBuyCardOrderExcelVo> list = primeOrderService.packageBuyCardExcelOrder(result);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName=new String(("购卡订单导出"+date + ".xlsx").getBytes("gb2312"),  "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            EasyExcel.write(response.getOutputStream(), PrimeBuyCardOrderExcelVo.class).sheet("Sheet1").doWrite(list);
        } catch (Exception e) {
            System.out.println("告警信息下载失败" + e.getMessage());
        }
    }

    /**
     * 下载消费订单excel
     * @param response
     * @param phone
     * @param state
     * @param cardNo
     * @param traceNo
     * @param orderNo
     * @param startTime
     * @param endTime
     */
    @GetMapping("/downloadConsumeOrders")
    public void downloadConsumeOrders(
            HttpServletResponse response,
            @RequestParam(value = "phone",defaultValue = "",required = false) String phone,
            @RequestParam(value = "state",defaultValue = "",required = false) String state,
            @RequestParam(value = "cardNo",defaultValue = "",required = false) String cardNo,
            @RequestParam(value = "traceNo",defaultValue = "",required = false) String traceNo,
            @RequestParam(value = "orderNo",defaultValue = "",required = false) String orderNo,
            @RequestParam(value = "startTime",required = false)String startTime,
            @RequestParam(value = "endTime",required = false) String endTime
    ){
        if (StringUtils.isEmpty(phone)
                && StringUtils.isEmpty(state)
                && StringUtils.isEmpty(cardNo)
                &&StringUtils.isEmpty(traceNo)
                &&StringUtils.isEmpty(orderNo)){
            if (StringUtils.isEmpty(startTime)){
                startTime = DateStrUtil.nowDateStrYearMoonDay();
            }
            if (StringUtils.isEmpty(endTime)){
                endTime = DateStrUtil.nowDateStrYearMoonDay();
            }
            endTime = endTime + " 23:59:59";
        }else {
            if (!StringUtils.isEmpty(endTime)){
                endTime = endTime + " 23:59:59";
            }
        }
        String userOpenId = getUserOpenId(phone);
        if (StringUtils.isEmpty(userOpenId) || "null".equals(userOpenId)){
            userOpenId = "";
        }
        List<CardOrderPayTrace> cardOrderPayTraceList= orderClientService.getConsumeOrdersExcelData(orderNo, startTime, endTime, state, userOpenId, "", cardNo, traceNo).getData();
        List<ConsumeCardOrderExcelVo> consumeCardOrderExcelVos = primeOrderService.packageConsumeExcelListData(cardOrderPayTraceList);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName=new String(("消费订单导出"+date + ".xlsx").getBytes("gb2312"),  "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            EasyExcel.write(response.getOutputStream(), ConsumeCardOrderExcelVo.class).sheet("Sheet1").doWrite(consumeCardOrderExcelVos);
        } catch (Exception e) {
            System.out.println("告警信息下载失败" + e.getMessage());
        }
    }

    /**
     * pos查询交易明细
     * @param searchPosOrderListData
     * @return
     */
    @PostMapping("/posOrderList")
    public Page<CardOrderPayTrace> posOrderList(@RequestBody SearchPosOrderListData searchPosOrderListData){
        return cardOrderPayTraceClientService.posOrderList(searchPosOrderListData).getData();
    }


    /**
     * 修复订单
     * @param fixCardOrderData
     */
    @PostMapping("/fixCardOrder")
    public void fixCardOrder(@RequestBody FixCardOrderData fixCardOrderData){
        List<CardActualMapUser> data = msPrimeClient.queryFixCardOrderDataId(fixCardOrderData.getAcmuId()).getData();
        for (CardActualMapUser datum : data) {
            CardElectronic cardElectronic = msPrimeClient.queryCardElectronicByCardNo(datum.getCardNo()).getData();
            orderClientService.createAdminSetUserCardOrder(cardElectronic,"tl_pos",cardElectronic.getSellAmount());
        }
    }

    /**
     * 修复流水记录
     * @param listOrderCode
     */
    @PostMapping("/fixCardOrderTraceUser")
    public void fixCardOrder(@RequestBody List<String> listOrderCode){
        for (String orderCode : listOrderCode) {
            CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
            List<CardOrderDetails> cardOrderDetailsList = cardOrdersVO.getCardOrderDetailsList();

            for (CardOrderDetails cardOrderDetails : cardOrderDetailsList) {
                String productionCode = cardOrderDetails.getProductionCode();
                List list = JSONObject.parseObject(productionCode, List.class);
                for (Object o : list) {
                    CardActualMapUser cardActualMapUser = msPrimeClient.queryByCardNo(o.toString());

                    cardOrderPayTraceClientService.fixCardOrderTraceUser(orderCode,cardActualMapUser.getUserPhone());
                }
            }
        }
    }

    /**
     * 退款,撤销订单列表
     * @param cardNo  卡号
     * @param oriOrderId  原交易单号
     * @param orderId 富基退款单号
     * @param startTime  区间开始时间
     * @param endTime    区间结束时间
     * @return
     */
    @GetMapping("/adminRefundOrderList")
    public Page<CardRefundOrder> adminRefundOrderList(
            @RequestParam(value = "pageNo") Integer pageNo,
            @RequestParam(value = "pageSize") Integer pageSize,
            @RequestParam(value = "cardNo", defaultValue = "", required = false) String cardNo,
            @RequestParam(value = "oriOrderId", defaultValue = "", required = false) String oriOrderId,
            @RequestParam(value = "orderId", defaultValue = "", required = false) String orderId,
            @RequestParam(value = "userPhone", defaultValue = "", required = false) String userPhone,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime
    ){
        if (!StringUtils.isEmpty(startTime)){
            startTime = startTime + " 00:00:00";
        }
        if (!StringUtils.isEmpty(endTime)){
            endTime = endTime + " 23:59:59";
        }
        Page<CardRefundOrder> data = msPrimeClient.adminRefundOrderList(pageNo, pageSize, cardNo, oriOrderId, orderId,userPhone, startTime, endTime).getData();
        return data;
    }

    /**
     * 退款,撤销订单列表
     * @param cardNo  卡号
     * @param oriOrderId  原交易单号
     * @param orderId 富基退款单号
     * @param startTime  区间开始时间
     * @param endTime    区间结束时间
     * @return
     */
    @GetMapping("/adminRefundOrderListExcel")
    public void adminRefundOrderListExcel(
            HttpServletResponse response,
            @RequestParam(value = "cardNo", defaultValue = "", required = false) String cardNo,
            @RequestParam(value = "oriOrderId", defaultValue = "", required = false) String oriOrderId,
            @RequestParam(value = "orderId", defaultValue = "", required = false) String orderId,
            @RequestParam(value = "userPhone", defaultValue = "", required = false) String userPhone,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime
    ){
        if (StringUtils.isEmpty(cardNo)
                && StringUtils.isEmpty(oriOrderId)
                && StringUtils.isEmpty(orderId)
                && StringUtils.isEmpty(userPhone)){
            if (StringUtils.isEmpty(startTime)){
                startTime = DateStrUtil.nowDateStrYearMoonDay();
            }
            if (StringUtils.isEmpty(endTime)){
                endTime = DateStrUtil.nowDateStrYearMoonDay();
            }
            endTime = endTime + " 23:59:59";
        }else {
            if (!StringUtils.isEmpty(endTime)){
                endTime = endTime + " 23:59:59";
            }
        }

        List<CardRefundOrder> cardRefundOrderList = msPrimeClient.adminRefundOrderListNoPage(cardNo, oriOrderId, orderId,userPhone, startTime, endTime,"").getData();
        List<CardRefundOrderExcelData> cardRefundOrderExcelData = primeOrderService.packageRefundOrderExcelListData(cardRefundOrderList);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName=new String(("退款数据导出"+date + ".xlsx").getBytes("gb2312"),  "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            EasyExcel.write(response.getOutputStream(), CardRefundOrderExcelData.class).sheet("Sheet1").doWrite(cardRefundOrderExcelData);
        } catch (Exception e) {
            System.out.println("告警信息下载失败" + e.getMessage());
        }
    }

    /**
     * 下载消费订单excel
     * @param response
     * @param orderNo
     * @param startTime
     * @param endTime
     */
    @GetMapping("/downloadConsumeOrdersMaster")
    public void downloadConsumeOrdersMaster(
            HttpServletResponse response,
            @RequestParam(value = "orderNo",defaultValue = "",required = false) String orderNo,
            @RequestParam(value = "startTime",required = false)String startTime,
            @RequestParam(value = "endTime",required = false) String endTime
    ){
        if (StringUtils.isEmpty(startTime)){
            startTime = DateStrUtil.nowDateStrYearMoonDay();
        }
        if (StringUtils.isEmpty(endTime)){
            endTime = DateStrUtil.nowDateStrYearMoonDay();
        }
        endTime = endTime + " 23:59:59";

        List<CardOrders> cardOrders = orderClientService.getConsumeOrdersMasterExcelData(orderNo, startTime, endTime).getData();
        List<ConsumeOrdersMasterExcelData> consumeOrdersMasterExcelData = primeOrderService.packageConsumeOrdersMasterExcelListData(cardOrders);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName=new String(("消费订单主订单导出"+date + ".xlsx").getBytes("gb2312"),  "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            EasyExcel.write(response.getOutputStream(), ConsumeOrdersMasterExcelData.class).sheet("Sheet1").doWrite(consumeOrdersMasterExcelData);
        } catch (Exception e) {
            System.out.println("告警信息下载失败" + e.getMessage());
        }
    }

    /**
     * 查询可退金额
     * @param oriOrderId
     * @return
     */
    @GetMapping("/adminQueryCanRefundAmount")
    public int adminQueryCanRefundAmount(@RequestParam("oriOrderId")String oriOrderId){
        List<CardRefundOrder> cardRefundOrders = msPrimeClient.queryRefundOrder(oriOrderId, "HLSC", "").getData();

        //已退款金额
        int totalRefundAmount = 0;
        for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
            totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
        }

        //已支付金额
        int totalPayMoney = 0;
        List<CardOrderPayTrace> cardOrderPayTraceList = cardOrderPayTraceClientService.queryPayTrace(oriOrderId).getData();
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
            if (CardOrderPayTraceStateConfig.PAID.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.REFUND.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.CANCEL.equals(cardOrderPayTrace.getState())){

                if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())
                        || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType())){
                    totalPayMoney = totalPayMoney + cardOrderPayTrace.getAmount();
                }
            }
        }
        return totalPayMoney-totalRefundAmount;
    }

    /**
     * 查询 已退金额
     * @param oriOrderId
     * @return
     */
    @GetMapping("/adminRefundSuccessQuery")
    public int adminRefundSuccessQuery(@RequestParam("oriOrderId")String oriOrderId,@RequestParam("refundCode")String refundCode){
        List<CardRefundOrder> cardRefundOrders = msPrimeClient.queryRefundOrder(oriOrderId, "HLSC", refundCode).getData();
        //退款成功金额
        int totalRefundAmount = 0;
        for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
            if ("success".equals(cardRefundOrder.getState())) {
                totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
            }
        }
        return totalRefundAmount;
    }



    /**
     * 查询 已退金额
     *
     * @return
     */
    @GetMapping("/fixOrderDetails")
    public void fixOrderDetails(){
        List<CardOrderDetails> data = orderClientService.allDetailsBuyCard().getData();
        for (CardOrderDetails cardOrderDetails : data) {
            if (!StringUtils.isEmpty(cardOrderDetails.getProductionCode())){
                ArrayList<String> cardList = JSON.parseObject(cardOrderDetails.getProductionCode(), ArrayList.class);
                if (cardList!=null && cardList.size()>0) {
                    CardElectronic cardElectronic = msPrimeClient.queryCardElectronicByCardNo(cardList.get(0)).getData();
                    if (cardElectronic != null) {
                        cardOrderDetails.setUserPhone(cardElectronic.getUserPhone());
                        cardOrderDetails.setCardType(cardElectronic.getCardType());
                    }
                }
            }
        }
        orderClientService.fixOrderDetails(data);
    }

    /**
     * 查询 已退金额
     *
     * @return
     */
    @GetMapping("/fixOrderTraceRefUser")
    public void fixOrderTraceRefUser(){
        List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceClientService.queryConsumeAll().getData();
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
            if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())) {
                CardElectronic data = msPrimeClient.queryCardElectronicByCardNo(cardOrderPayTrace.getSourceId()).getData();
                if (data!=null) {
                    Integer totalRefundAmount = 0;
                    if (!StringUtils.isEmpty(cardOrderPayTrace.getTraceNo())) {
                        List<CardRefundOrder> refundByTraceNoList = msPrimeClient.queryRefundByTraceNo(cardOrderPayTrace.getTraceNo()).getData();
                        for (CardRefundOrder cardRefundOrder : refundByTraceNoList) {
                            totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
                        }
                    }
                    cardOrderPayTrace.setRefundAmount(totalRefundAmount);
                    cardOrderPayTrace.setUserPhone(data.getUserPhone());
                    cardOrderPayTrace.setRefCardType(data.getCardType());
                    cardOrderPayTrace.setRefCardName(data.getCardName());
                    cardOrderPayTrace.setRefBatchCode(data.getBatchCode());
                    cardOrderPayTrace.setRefRemainFaceValue(data.getFaceValue());
                }
            }else if (CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType())){
                CardPhysical data = msPrimeClient.queryByCardCode(cardOrderPayTrace.getSourceId()).getData();
                if (data!=null) {
                    Integer totalRefundAmount = 0;
                    if (!StringUtils.isEmpty(cardOrderPayTrace.getTraceNo())) {
                        List<CardRefundOrder> refundByTraceNoList = msPrimeClient.queryRefundByTraceNo(cardOrderPayTrace.getTraceNo()).getData();
                        for (CardRefundOrder cardRefundOrder : refundByTraceNoList) {
                            totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
                        }
                    }
                    cardOrderPayTrace.setRefundAmount(totalRefundAmount);
                    cardOrderPayTrace.setRefCardType(data.getType());
                    cardOrderPayTrace.setRefCardName(data.getCardName());
                    cardOrderPayTrace.setRefBatchCode(data.getBatchCode());
                    cardOrderPayTrace.setRefRemainFaceValue(data.getFaceValue());
                }
            }
        }
        cardOrderPayTraceClientService.fixOrderTraceRefUser(cardOrderPayTraces);
    }
}
