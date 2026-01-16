package com.ht.user.outlets.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.MisOrderData;
import com.ht.user.config.CardOrderPayTraceStateConfig;
import com.ht.user.config.CardOrdersStateConfig;
import com.ht.user.mall.entity.OrderOrders;
import com.ht.user.ordergoods.entity.UploadOrderDetails;
import com.ht.user.outlets.allinpay.SybPayService;
import com.ht.user.outlets.config.MerchantCashMapConstant;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.paystrategy.PayCompanyStrategy;
import com.ht.user.outlets.paystrategy.PayCompanyStrategyFactory;
import com.ht.user.outlets.paystrategy.PayCompanyTypeEnum;
import com.ht.user.outlets.service.IOutletsOrderPayTraceService;
import com.ht.user.outlets.service.IOutletsOrderRefRefundCancelService;
import com.ht.user.outlets.service.IOutletsOrdersService;
import com.ht.user.outlets.util.*;
import com.ht.user.outlets.vo.OutletsOrdersVO;
import com.ht.user.outlets.vo.QueryOutletsOrdersVO;
import com.ht.user.outlets.vo.UpdateRefundPasswordVO;
//import com.ht.user.outlets.websocket.OrderWebSocketServer;
import com.ht.user.result.ResultTypeEnum;
import com.ht.user.sysconstant.entity.DicConstant;
import com.ht.user.sysconstant.service.DicConstantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 订单主表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@RestController
@RequestMapping("/tonglian/outlets/orders")
public class OutletsOrdersController {

    private Logger logger = LoggerFactory.getLogger(OutletsOrdersController.class);

    @Autowired
    private DESUtil desUtil;

    @Autowired
    private IOutletsOrdersService outletsOrdersService;

    @Autowired
    private IOutletsOrderPayTraceService outletsOrderPayTraceService;

    @Autowired
    private SybPayService sybPayService;

//    @Autowired
//    private OrderWebSocketServer orderWebSocketServer;

    @Autowired
    private DicConstantService dicConstantService;

    @Autowired
    private IOutletsOrderRefRefundCancelService outletsOrderRefRefundCancelService;

    @Autowired
    public StraitDESUtil straitDESUtil;


    /**
     * 奥特莱斯 上送云mis订单
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/misOrder")
    public ResponMisOrder misOrder(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        try {
            logger.info("上送的mis订单加密数据为:" + desDataStr.getDesDataStr());
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            logger.info("上送的mis订单解密字符串数据为:" + decryptDataStr);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        MisOrderData misOrderData = JSONObject.parseObject(decryptDataStr, MisOrderData.class);
        logger.info("上送的mis订单数据为:" + misOrderData);
        if (misOrderData == null) {
            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL_ERROR);
        }
        if (!StringGeneralUtil.checkNotNull(misOrderData.getOrderCode())) {
            throw new CheckException(ResultTypeEnum.ORDER_CODE_NULL);
        }

        if (!StringGeneralUtil.checkNotNull(misOrderData.getAmount()) ||
                Long.parseLong(misOrderData.getAmount()) < 1) {
            throw new CheckException(ResultTypeEnum.MONEY_ERROR);
        }
        misOrderData.setLimitPayType(StringGeneralUtil.checkNotNull(misOrderData.getLimitPayType()) ? misOrderData.getLimitPayType() : "credit_card");
        //响应调用端
        ResponMisOrder responMisOrder = new ResponMisOrder();
        responMisOrder.setOrderCode(misOrderData.getOrderCode());

        //创建mis订单数据
        OutletsOrderPayTrace outletsOrderPayTrace = outletsOrdersService.createMisOrderPayTrace(misOrderData);

//        封装数据推送pos
//        PushWebSocketOrderData pushWebSocketOrderData = new PushWebSocketOrderData();
//        pushWebSocketOrderData.setType("pay");
//        ResponseOutletsPosOrderData responseMisOrderData = new ResponseOutletsPosOrderData();
//        responseMisOrderData.setAmount(Integer.parseInt(misOrderData.getAmount()));
//        responseMisOrderData.setOrderNo(misOrderData.getOrderCode());
//        responseMisOrderData.setLimitPayType(misOrderData.getLimitPayType());
//        pushWebSocketOrderData.setResponseOutletsPosOrderData(responseMisOrderData);
//        orderWebSocketServer.sendInfo(misOrderData.getCashId(),JSONObject.toJSONString(pushWebSocketOrderData));

        return responMisOrder;
    }

    /**
     * 拉取 云mis 订单数据
     *
     * @param cashId
     * @return
     */
    @GetMapping("/pullMisOrder")
    public ResponseOutletsPosOrderData pullMisOrder(@RequestParam("cashId") String cashId, @RequestParam(value = "version", required = false) String version) {
        if ("null".equals(cashId) || "undefined".equals(cashId) || StringUtils.isEmpty(cashId)) {
            throw new CheckException(ResultTypeEnum.CASH_ID_ERROR.getCode(), "收银机款台号为:" + cashId);
        }
        List<OutletsOrderPayTrace> orderPayTraceList = outletsOrdersService.checkHaveOrder(cashId);
        if (orderPayTraceList == null || orderPayTraceList.size() < 1) {
            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
        }
        OutletsOrderPayTrace outletsOrderPayTrace = orderPayTraceList.get(0);
        OutletsOrders data = outletsOrdersService.queryByOrderCode(outletsOrderPayTrace.getOrderCode());
        if (data != null) {
            if (!CardOrdersStateConfig.UNPAID.equals(data.getState())) {
                throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
            }
        } else {
            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
        }

        //mis订单支付时间相应状态处理
        boolean checkPayTimeFlag = CheckPayTimeUtil.checkPayTime(outletsOrderPayTrace.getCreateAt());
        if (!checkPayTimeFlag) {
            outletsOrdersService.updateAllState(outletsOrderPayTrace.getOrderCode(), CardOrdersStateConfig.CLOSE);
            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL);
        }

        ResponseOutletsPosOrderData responseMisOrderData = new ResponseOutletsPosOrderData();
        responseMisOrderData.setAmount(data.getAmount());
        responseMisOrderData.setOrderNo(outletsOrderPayTrace.getRefBatchCode());
        responseMisOrderData.setLimitPayType(data.getLimitPayType());
        responseMisOrderData.setCreateAt(outletsOrderPayTrace.getCreateAt());
        responseMisOrderData.setCheckTrace(outletsOrderPayTrace.getRefBatchCode());
        responseMisOrderData.setLimitPayTime(CheckPayTimeUtil.getLimitPayTime(outletsOrderPayTrace.getCreateAt()));

        if ("yes".equals(CompanyPayWeight.ifOpenPos)){
            return responseMisOrderData;
        }else {
            throw new CheckException(ResultTypeEnum.SERVICE_ERROR.getCode(),"pos故障异常");
        }

    }

    /**
     * 奥特莱斯 pos端收银 银行卡,微信,支付宝支付成功  数据记录接口
     *
     * @param posPayTraceSuccessData
     * @return
     */
    @PostMapping("/settlementSuccess")
    public String settlementSuccess(@RequestBody PosPayTraceSuccessData posPayTraceSuccessData) throws Exception {
        logger.info("pos支付数据回调:" + posPayTraceSuccessData);
        if (!"00".equals(posPayTraceSuccessData.getRejCode())) {
            throw new CheckException(ResultTypeEnum.POS_PAY_ERROR);
        }
//        String orderCodeOri = posPayTraceSuccessData.getOrderCode();
        String refBatchCode = posPayTraceSuccessData.getOrderCode();
        if (!StringUtils.isEmpty(refBatchCode)) {
//            OutletsOrders outletsOrders = outletsOrdersService.queryByOrderCodeNotMany(orderCodeOri);
            OutletsOrderPayTrace outletsOrderPayTrace = outletsOrderPayTraceService.queryByRefBatchCode(refBatchCode);
            if (outletsOrderPayTrace != null) {
                posPayTraceSuccessData.setOrderCode(outletsOrderPayTrace.getOrderCode());
                outletsOrderPayTraceService.updateMisOrderState(posPayTraceSuccessData);
                return refBatchCode;
            }
        }
        String orderCode = outletsOrderPayTraceService.createPosPayTraceFromCashier(posPayTraceSuccessData);
        return orderCode;
    }

    /**
     * 奥特莱斯 pos端收银 银行卡,微信,支付宝支付成功  数据记录接口
     *
     * @param posPayTraceSuccessData
     * @return
     */
    @PostMapping("/fixOrderState")
    public String fixOrderState(@RequestBody PosPayTraceSuccessData posPayTraceSuccessData) throws Exception {
        return "test";
    }


    /**
     * 奥特莱斯 扫码支付
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/paymentQrCode")
    public String paymentQrCode(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        QrPaymentData qrPaymentData;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            qrPaymentData = JSONObject.parseObject(decryptDataStr, QrPaymentData.class);
            logger.info("上送的核销数据为:" + qrPaymentData);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        if (qrPaymentData == null) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        String paymentQrCode = qrPaymentData.getPaymentQrCode();

        logger.info("获取的付款码为:" + paymentQrCode);

        if (!StringGeneralUtil.checkNotNull(paymentQrCode)) {
            throw new CheckException(ResultTypeEnum.PAY_QR_CODE_NULL_ERROR);
        }

        if (!StringGeneralUtil.checkNotNull(qrPaymentData.getOrderCode())) {
            throw new CheckException(ResultTypeEnum.ORDER_CODE_NULL);
        }

        if (qrPaymentData.getAmount() == null ||
                qrPaymentData.getAmount() < 1) {
            throw new CheckException(ResultTypeEnum.MONEY_ERROR);
        }

        OutletsOrders outletsOrders = outletsOrdersService.queryByOrderCode(qrPaymentData.getOrderCode());
        if (outletsOrders != null) {
            throw new CheckException(ResultTypeEnum.ORDER_CODE_REPEAT);
        }



        String channelApi = CompanyPayWeight.getPayCompanyFlag(CompanyPayWeight.qrPayCompany);

        String transferOrderCode;
        //不同支付公司传递的订单号不一样
        if (PayCompanyTypeEnum.JLPAY.getPayCompany().equals(channelApi)){
            //订单长度调整
            transferOrderCode = OrderCodeIntercept.reserveThirtyOne(qrPaymentData.getOrderCode());
        }else {
            transferOrderCode = qrPaymentData.getOrderCode();
        }

        PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(channelApi);
        //调用 支付接口
        Map<String, String> map = payCompanyStrategy.scanCodePay(qrPaymentData.getAmount(),
                transferOrderCode,
                "扫码消费订单",
                "备注",
                qrPaymentData.getPaymentQrCode(),
                "",
                "",
                "",
                "",
                qrPaymentData.getCashId());
        //调用通联支付接口
//        Map<String, String> map = sybPayService.scanPay(qrPaymentData.getAmount(),
//                qrPaymentData.getOrderCode(),
//                "扫码消费订单",
//                "备注",
//                qrPaymentData.getPaymentQrCode(),
//                "",
//                "",
//                "",
//                "");
        logger.info("扫码支付响应数据为" + map);

        OutletsOrderRefTrace outletsOrderRefTrace = new OutletsOrderRefTrace();
        BeanMap beanMap = BeanMap.create(outletsOrderRefTrace);
        beanMap.putAll(map);
        outletsOrderRefTrace.setReqsn(qrPaymentData.getOrderCode());
        outletsOrderRefTrace.setRefReqsn(transferOrderCode);

        //创建支付订单
        outletsOrdersService.createPaymentQrCodeOrder(outletsOrderRefTrace,
                qrPaymentData,
                channelApi);

        //响应数据
        RetPaymentQrCodeData retPaymentQrCodeData = new RetPaymentQrCodeData();
        retPaymentQrCodeData.setOrderCode(qrPaymentData.getOrderCode());

        String retcode = outletsOrderRefTrace.getRetcode();
        String trxstatus = outletsOrderRefTrace.getTrxstatus();

        //金额响应
        retPaymentQrCodeData.setPaidAmount(0);
        retPaymentQrCodeData.setNeedPaidAmount(qrPaymentData.getAmount());

        if ("SUCCESS".equals(retcode)) {
            if ("0000".equals(trxstatus)) {
                retPaymentQrCodeData.setPaidAmount(Integer.parseInt(outletsOrderRefTrace.getTrxamt()));
                retPaymentQrCodeData.setNeedPaidAmount(qrPaymentData.getAmount() - Integer.parseInt(outletsOrderRefTrace.getTrxamt()));
            }
        }

        String jsonString = JSONObject.toJSONString(retPaymentQrCodeData);
        String encrypt = desUtil.encrypt(jsonString);
        logger.info(qrPaymentData.getOrderCode() + "订单,响应收银台数据为:" + retPaymentQrCodeData);
        logger.info(qrPaymentData.getOrderCode() + "订单,响应收银台数据为:" + encrypt);
        return encrypt;
    }


    /**
     * 奥特莱斯 订单关闭
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/orderClose")
    public void orderClose(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        OutletsOrderCloseData orderCloseData = JSONObject.parseObject(decryptDataStr, OutletsOrderCloseData.class);
        OutletsOrders data = outletsOrdersService.queryByOrderCodeNotMany(orderCloseData.getOrderCode());
        if (!CardOrdersStateConfig.UNPAID.equals(data.getState())) {
            throw new CheckException(ResultTypeEnum.ORDER_CLOSE_ERROR);
        }
        outletsOrdersService.updateAllState(orderCloseData.getOrderCode(), CardOrdersStateConfig.CLOSE);
//        List<OutletsOrderPayTrace> outletsOrderPayTraces = outletsOrderPayTraceService.queryTraceByOrderCode(orderCloseData.getOrderCode());
//
//        if (outletsOrderPayTraces!=null && outletsOrderPayTraces.size()>0) {
//            //封装数据推送pos
//            PushWebSocketOrderData pushWebSocketOrderData = new PushWebSocketOrderData();
//            pushWebSocketOrderData.setType("close");
//            PushOutletsPosOrderCloseData pushOutletsPosOrderCloseData = new PushOutletsPosOrderCloseData();
//            pushOutletsPosOrderCloseData.setOrderNo(data.getOrderCode());
//            pushOutletsPosOrderCloseData.setState(CardOrdersStateConfig.CLOSE);
//            pushWebSocketOrderData.setPushOutletsPosOrderCloseData(pushOutletsPosOrderCloseData);
//            orderWebSocketServer.sendInfo(outletsOrderPayTraces.get(0).getCashId(), JSONObject.toJSONString(pushWebSocketOrderData));
//        }
    }

    /**
     * 奥特莱斯 退款
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/refund")
    public void refund(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        logger.info("上送的退款数据为:" + decryptDataStr);
        OutletsRefundData outletsRefundData = JSONObject.parseObject(decryptDataStr, OutletsRefundData.class);
        logger.info("上送的退款数据为:" + outletsRefundData);

        if (!StringGeneralUtil.checkNotNull(outletsRefundData.getRefundAmount()) ||
                Long.parseLong(outletsRefundData.getRefundAmount()) < 1) {
            throw new CheckException(ResultTypeEnum.MONEY_ERROR);
        }

        List<OutletsOrderPayTrace> outletsOrderPayTraces = outletsOrderPayTraceService.queryTraceByOrderCode(outletsRefundData.getOrderCode());

        if (outletsOrderPayTraces == null || outletsOrderPayTraces.size() < 1) {
            throw new CheckException(ResultTypeEnum.REFUND_MONEY_ZERO_ERROR);
        }

        OutletsOrderRefRefundCancel queryDBRefundOrder = outletsOrderRefRefundCancelService.getByReqsn(outletsRefundData.getRefundCode());
        if (queryDBRefundOrder!=null){
            throw new CheckException(1500,"上送的退款单号已存在");
        }

        outletsOrdersService.refund(outletsOrderPayTraces, outletsRefundData);
        logger.info(outletsRefundData.getRefundCode()+"退款处理完成,响应收银台");
    }

    /**
     * 奥特莱斯 撤销交易
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/cancel")
    public void orderCancel(@RequestBody DESDataStr desDataStr) throws Exception {
        logger.info("上送的撤销订单加密数据:" + desDataStr.getDesDataStr());
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            logger.info("上送的撤销订单解密数据:" + decryptDataStr);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        OutletsOrderCancelData cancelData = JSONObject.parseObject(decryptDataStr, OutletsOrderCancelData.class);
        logger.info("上送的撤销订单实体数据:" + cancelData);
        OutletsOrders outletsOrders = outletsOrdersService.queryByOrderCodeNotMany(cancelData.getOrderCode());
        if (outletsOrders != null) {
            if (!CardOrdersStateConfig.PAID.equals(outletsOrders.getState())) {
                throw new CheckException(ResultTypeEnum.CANCEL_ERROR);
            }
        } else {
            throw new CheckException(ResultTypeEnum.CANCEL_ERROR);
        }

        OutletsOrderRefRefundCancel queryDBRefundOrder = outletsOrderRefRefundCancelService.getByReqsn(cancelData.getCancelCode());
        if (queryDBRefundOrder!=null){
            throw new CheckException(1500,"上送的撤销单号已存在");
        }

        outletsOrdersService.orderCancel(cancelData);
        logger.info(cancelData.getCancelCode()+"撤销处理完成,响应收银台");
    }

    /**
     * 测试通道 无加密 奥特莱斯 上送云mis订单
     *
     * @param misOrderData
     * @return
     */
    @PostMapping("/testMisOrder")
    public ResponMisOrder testMisOrder(MisOrderData misOrderData) throws Exception {
        logger.info("上送的mis订单数据为:" + misOrderData);
        if (misOrderData == null) {
            throw new CheckException(ResultTypeEnum.MIS_ORDER_NULL_ERROR);
        }

        if (!StringGeneralUtil.checkNotNull(misOrderData.getAmount()) ||
                Long.parseLong(misOrderData.getAmount()) < 1) {
            throw new CheckException(ResultTypeEnum.MONEY_ERROR);
        }

        //测试商品信息数据构建
        List<UploadOrderDetails> list = new ArrayList<>();
        UploadOrderDetails uploadOrderDetails = new UploadOrderDetails();
        uploadOrderDetails.setGoodsGroupCode("1232344");
        uploadOrderDetails.setCategoryCode("12312344");
        uploadOrderDetails.setBrandCode("123123");
        uploadOrderDetails.setGoodsCode("234234");
        uploadOrderDetails.setGoodsName("测试商品");
        uploadOrderDetails.setGoodsCount("12");
        uploadOrderDetails.setGoodsPrice("100");
        uploadOrderDetails.setGoodsDiscount("10");
        uploadOrderDetails.setGoodsPayPrice("90");
        uploadOrderDetails.setGoodsActivityType("1010");

        UploadOrderDetails uploadOrderDetailsTwo = new UploadOrderDetails();
        uploadOrderDetailsTwo.setGoodsGroupCode("1232344");
        uploadOrderDetailsTwo.setCategoryCode("12312344");
        uploadOrderDetailsTwo.setBrandCode("123123");
        uploadOrderDetailsTwo.setGoodsCode("234234");
        uploadOrderDetailsTwo.setGoodsName("测试商品");
        uploadOrderDetailsTwo.setGoodsCount("12");
        uploadOrderDetailsTwo.setGoodsPrice("100");
        uploadOrderDetailsTwo.setGoodsDiscount("10");
        uploadOrderDetailsTwo.setGoodsPayPrice("90");
        uploadOrderDetailsTwo.setGoodsActivityType("1010");

        list.add(uploadOrderDetails);
        list.add(uploadOrderDetailsTwo);
        misOrderData.setOrderDetail(JSONObject.toJSONString(list));

        //响应调用端
        ResponMisOrder responMisOrder = new ResponMisOrder();
        responMisOrder.setOrderCode(misOrderData.getOrderCode());

        //创建mis订单数据
        OutletsOrderPayTrace outletsOrderPayTrace = outletsOrdersService.createMisOrderPayTrace(misOrderData);

        //封装数据推送pos
//        PushWebSocketOrderData pushWebSocketOrderData = new PushWebSocketOrderData();
//        pushWebSocketOrderData.setType("pay");
//        ResponseOutletsPosOrderData responseMisOrderData = new ResponseOutletsPosOrderData();
//        responseMisOrderData.setAmount(Integer.parseInt(misOrderData.getAmount()));
//        responseMisOrderData.setOrderNo(misOrderData.getOrderCode());
//        responseMisOrderData.setLimitPayType(misOrderData.getLimitPayType());
//        pushWebSocketOrderData.setResponseOutletsPosOrderData(responseMisOrderData);
//        orderWebSocketServer.sendInfo(misOrderData.getCashId(),JSONObject.toJSONString(pushWebSocketOrderData));

        return responMisOrder;
    }


    /**
     * 奥特莱斯 订单主表分页列表
     *
     * @param queryOutletsOrdersVO
     * @return
     */
    @PostMapping("/list/page")
    public IPage<OutletsOrdersVO> findPage(@RequestBody QueryOutletsOrdersVO queryOutletsOrdersVO) throws Exception {
        Integer pageNo = queryOutletsOrdersVO.getPageNo();
        Integer pageSize = queryOutletsOrdersVO.getPageSize();
        String orderCode = queryOutletsOrdersVO.getOrderCode();
        String type = queryOutletsOrdersVO.getType();
        String state = queryOutletsOrdersVO.getState();
        String actualPhone = queryOutletsOrdersVO.getActualPhone();
        String sourceId = queryOutletsOrdersVO.getSourceId();
        String cashId = queryOutletsOrdersVO.getCashId();
        String payCode = queryOutletsOrdersVO.getPayCode();
        String startCreateAt = queryOutletsOrdersVO.getStartCreateAt();
        String endCreateAt = queryOutletsOrdersVO.getEndCreateAt();
        pageNo = HelperUtils.getDefaultIntValue(pageNo, HelperUtils.PAGE_NO_DEFAULT_VALUE);
        pageSize = HelperUtils.getDefaultIntValue(pageSize, HelperUtils.PAGE_SIZE_DEFAULT_VALUE);

        IPage<OutletsOrders> page = new Page<>(pageNo, pageSize);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("orderCode", orderCode);
        paramsMap.put("type", type);
        paramsMap.put("state", state);
        paramsMap.put("actualPhone", actualPhone);
        paramsMap.put("sourceId", sourceId);
        paramsMap.put("cashId", cashId);
        paramsMap.put("payCode", payCode);
        paramsMap.put("startCreateAt", startCreateAt);
        paramsMap.put("endCreateAt", endCreateAt);
        IPage<OutletsOrdersVO> result = outletsOrdersService.findPageLeftJoinPayTrace(page, paramsMap);
        return result;
    }

    /**
     * 奥特莱斯 (后台)退款
     *
     * @param outletsRefundData
     * @throws Exception
     */
    @PostMapping("/backstage/refund")
    public void refund(@RequestBody OutletsRefundData outletsRefundData) throws Exception {
        logger.info("上送的退款数据为:" + outletsRefundData);

        if (!StringGeneralUtil.checkNotNull(outletsRefundData.getRefundAmount()) ||
                Long.parseLong(outletsRefundData.getRefundAmount()) < 1) {
            throw new CheckException(ResultTypeEnum.MONEY_ERROR);
        }

        //判断退款密码
        DicConstant dicConstant = dicConstantService.findPassword();
        if (!dicConstant.getValue().equals(outletsRefundData.getRefundPassword())) {
            throw new CheckException(ResultTypeEnum.BIND_EXCEPTION);
        }

        //自生成退款单号
        String refundCode = IdWorker.getIdStr();
        outletsRefundData.setRefundCode(refundCode);

        List<OutletsOrderPayTrace> outletsOrderPayTraces = outletsOrderPayTraceService.queryTraceByOrderCode(outletsRefundData.getOrderCode());

        if (outletsOrderPayTraces == null || outletsOrderPayTraces.size() < 1) {
            throw new CheckException(ResultTypeEnum.REFUND_MONEY_ZERO_ERROR);
        }

        outletsOrdersService.refund(outletsOrderPayTraces, outletsRefundData);
    }

    /**
     * 修改退款密码
     *
     * @param updateRefundPasswordVO
     * @throws Exception
     */
    @PostMapping("/backstage/refundPassword/modify")
    public void refundPasswordModify(@RequestBody UpdateRefundPasswordVO updateRefundPasswordVO) throws Exception {
        logger.info("上送的修改密码数据为:" + updateRefundPasswordVO);

        if (!dicConstantService.updatePassword(updateRefundPasswordVO)) {
            throw new CheckException(ResultTypeEnum.BIND_EXCEPTION);
        }

    }

    /**
     * 奥特莱斯 pos机 发起退款
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/posOrderListRefund")
    public void posRefund(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        logger.info("pos上送的退款数据为:" + decryptDataStr);
        OutletsPosRefundData outletsPosRefundData = JSONObject.parseObject(decryptDataStr, OutletsPosRefundData.class);
        logger.info("pos上送的退款数据为:" + outletsPosRefundData);


        OutletsOrderPayTrace outletsOrderPayTraceQuery = outletsOrderPayTraceService.queryByRefBatchCode(outletsPosRefundData.getOrderCode());


        if (outletsOrderPayTraceQuery == null ) {
            throw new CheckException(ResultTypeEnum.REFUND_MONEY_ZERO_ERROR);
        }


        //pos号校验
        if (!StringGeneralUtil.checkNotNull(outletsPosRefundData.getPosSerialNumber())) {
            throw new CheckException(ResultTypeEnum.POS_NUM_ERROR);
        }
        if (!StringGeneralUtil.checkNotNull(outletsOrderPayTraceQuery.getPosSerialNum())) {
            throw new CheckException(ResultTypeEnum.TRACE_POS_NUMBER_ERROR);
        }
        if (!outletsOrderPayTraceQuery.getPosSerialNum().equals(outletsPosRefundData.getPosSerialNumber())) {
            throw new CheckException(ResultTypeEnum.TRACE_POS_NUMBER_ERROR);
        }

        outletsPosRefundData.setRefundAmount(outletsOrderPayTraceQuery.getAmount() + "");
        outletsPosRefundData.setRefundCode("pos" + IdWorker.getIdStr());

        if (!StringGeneralUtil.checkNotNull(outletsPosRefundData.getRefundAmount()) ||
                Long.parseLong(outletsPosRefundData.getRefundAmount()) < 1) {
            throw new CheckException(ResultTypeEnum.MONEY_ERROR);
        }

        OutletsRefundData outletsRefundData = new OutletsRefundData();
        outletsRefundData.setOrderCode(outletsOrderPayTraceQuery.getOrderCode());
        outletsRefundData.setOperator(outletsPosRefundData.getOperator() + "-" + outletsPosRefundData.getPosSerialNumber());
        outletsRefundData.setRefundCode(outletsPosRefundData.getRefundCode());
        outletsRefundData.setRefundAmount(outletsPosRefundData.getRefundAmount());
        List<OutletsOrderPayTrace> outletsOrderPayTraces = new ArrayList<>();
        outletsOrderPayTraces.add(outletsOrderPayTraceQuery);
        outletsOrdersService.refund(outletsOrderPayTraces, outletsRefundData);
    }

    /**
     * 测试websocket推送
     *
     * @param cashId
     * @return
     */
    @GetMapping("/test/push/{cashId}")
    public void misOrder(@PathVariable("cashId") String cashId) throws Exception {
        //封装数据推送pos
        PushWebSocketOrderData pushWebSocketOrderData = new PushWebSocketOrderData();
        pushWebSocketOrderData.setType("pay");
        ResponseOutletsPosOrderData responseMisOrderData = new ResponseOutletsPosOrderData();
        responseMisOrderData.setAmount(100);
        responseMisOrderData.setOrderNo("123123");
        responseMisOrderData.setLimitPayType("123123");
        pushWebSocketOrderData.setResponseOutletsPosOrderData(responseMisOrderData);
//        orderWebSocketServer.sendInfo(cashId,JSONObject.toJSONString(pushWebSocketOrderData));
    }

    /**
     * 奥特莱斯 订单关闭
     *
     * @param orderCloseData
     * @return
     */
    @PostMapping("/posOrderClose")
    public void posOrderClose(@RequestBody OutletsOrderCloseData orderCloseData) throws Exception {
//        OutletsOrders data = outletsOrdersService.queryByOrderCodeNotMany(orderCloseData.getOrderCode());
        OutletsOrderPayTrace data = outletsOrderPayTraceService.queryByRefBatchCode(orderCloseData.getOrderCode());
        if (data != null) {
            if (!CardOrdersStateConfig.UNPAID.equals(data.getState())) {
                throw new CheckException(ResultTypeEnum.ORDER_CLOSE_ERROR);
            }
            outletsOrdersService.updateAllState(data.getOrderCode(), CardOrdersStateConfig.CLOSE);
        }
    }

    /**
     * 奥特莱斯 测试
     *
     * @param
     * @return
     */
    @GetMapping("/testError")
    public String testError() throws Exception {
        return "123123123";
    }

    /**
     * 获取通联调取 H5 支付数据 (线上商城购物支付使用)
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/mallUnionOrderBuyApiWeb")
    public Map mallUnionOrderBuyApiWeb(@RequestBody PayOrderData payOrderData) throws Exception {
        Map map = sybPayService.mallUnionOrderBuyApiWeb(payOrderData.getTrxamt(),
                payOrderData.getOrderCode(),
                payOrderData.getBody(),
                payOrderData.getReturl(),
                payOrderData.getNotifyUrl(),
                payOrderData.getPaytype());
        return map;
    }

    /**
     * 获取通联调取 H5 支付数据 (线上商城购物支付使用)
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/webapipay")
    public Map webapipay(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        try {
            logger.info("上送的mis订单加密数据为:" + desDataStr.getDesDataStr());
            decryptDataStr = straitDESUtil.decrypt(desDataStr.getDesDataStr());
            logger.info("上送的mis订单解密字符串数据为:" + decryptDataStr);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        PayOrderData payOrderData = JSONObject.parseObject(decryptDataStr, PayOrderData.class);
        logger.info("上送的mis订单数据为:" + payOrderData);

        Map map = sybPayService.mallUnionOrderBuyApiWeb(payOrderData.getTrxamt(),
                payOrderData.getOrderCode(),
                payOrderData.getBody(),
                payOrderData.getReturl(),
                payOrderData.getNotifyUrl(),
                payOrderData.getPaytype());

        return map;
    }


    @PostMapping("/testQuery")
    public Map<String, String> testQuery(@RequestParam("orderCode")String orderCode) throws Exception {
        PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(CompanyPayWeight.qrPayCompany);
        return payCompanyStrategy.query(orderCode, "");
    }

}

