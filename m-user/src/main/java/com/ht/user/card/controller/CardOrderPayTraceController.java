package com.ht.user.card.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.*;
import com.ht.user.card.excel.ConsumeCardOrderExcelVo;
import com.ht.user.card.service.CardMapUserCardsService;
import com.ht.user.card.service.CardOrderPayTraceService;
import com.ht.user.card.service.CardOrdersService;
import com.ht.user.card.vo.*;
import com.ht.user.common.Result;
import com.ht.user.common.StatusCode;
import com.ht.user.config.*;
import com.ht.user.utils.Cache;
import com.ht.user.utils.CacheManager;
import com.ht.user.utils.RequestQrCodeDataStrUtil;
import com.ht.user.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单支付流水 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-22
 */
@RestController
@RequestMapping("/orderPayTrace")
@CrossOrigin(allowCredentials = "true")
public class CardOrderPayTraceController {

    private Logger logger = LoggerFactory.getLogger(CardOrderPayTraceController.class);

    @Autowired
    private CardOrderPayTraceService cardOrderPayTraceService;

    @Autowired
    private CardMapUserCardsService cardMapUserCardsService;

    @Autowired
    private CardOrdersService cardOrdersService;

    /**
     * 根据订单号查询支付流水 ,获取现金金额
     *
     * @param orderCode
     * @return
     */
    @GetMapping("/getTraceByOrderCode/{orderCode}")
    public CardOrderPayTrace getTraceByOrderCode(@PathVariable(value = "orderCode", required = true) String orderCode) {
        return cardOrderPayTraceService.queryTraceByOrderCodeAndCashPay(orderCode);
    }

    /**
     * 创建 优惠券 抵扣支付流水
     * @param userId
     * @param merchantCode
     * @param orderCode
     * @param cardPayDetailData
     */
    @PostMapping("/createCouponCardPayTrace")
    public void createCouponCardPayTrace(@RequestParam("userId") Long userId,
                                   @RequestParam("merchantCode")String merchantCode,
                                   @RequestParam("orderCode")String orderCode,
                                   @RequestBody CardPayDetailData cardPayDetailData){
        if ("mis".equals(orderCode.substring(0,3))) {
            cardOrderPayTraceService.createCouponCardPayTrace(userId, merchantCode, orderCode.substring(3), cardPayDetailData);
        }else {
            cardOrderPayTraceService.createCouponCardPayTrace(userId, merchantCode, orderCode, cardPayDetailData);
        }
    }


    /**
     * POS端 根据pos机串号查询消费交易流水记录
     *
     * @param posSerialNum
     * @return
     */
    @GetMapping("/getTracePosNum/{posSerialNum}")
    public List<CardOrderPayTrace> getTrace(@PathVariable(value = "posSerialNum", required = true) String posSerialNum) {
        return cardOrderPayTraceService.queryListByPosSerialNum(posSerialNum);
    }

    /**
     * 根据支付号 获取流水
     *
     * @param payCode
     * @return
     */
    @GetMapping("/getTraceCardCode/{payCode}")
    public List<CardOrderPayTrace> getTraceCardCode(@PathVariable(value = "payCode", required = true) String payCode) {
        return cardOrderPayTraceService.queryListByPayCode(payCode);
    }

    /**
     * 用户余额充值 支付成功后 记录支付流水
     *
     * @param posPayTraceData
     * @return
     */
    @PostMapping("/posPayTrace")
    public void posPayTrace(@RequestBody PosPayTraceData posPayTraceData) {
        cardOrderPayTraceService.createPosPayTrace(posPayTraceData);
    }

    /**
     * pos端收银 微信,支付宝支付成功  数据记录接口(会员收银 支付)
     *
     * @param posPayTraceData
     * @return
     */
    @PostMapping("/settlementSuccess")
    public String settlementSuccess(@RequestBody PosPayTraceData posPayTraceData) {
        String orderCodeOri = posPayTraceData.getOrderCode();
        if (!StringUtils.isEmpty(orderCodeOri)){
            CardOrdersVO cardOrdersVO = cardOrdersService.queryByOrderCode(orderCodeOri);
            if (cardOrdersVO!=null && CardOrdersTypeConfig.POS_MIS_ORDER.equals(cardOrdersVO.getType())){
                if (posPayTraceData.getCardNoList()!=null && posPayTraceData.getCardNoList().size()>0){
                    cardOrderPayTraceService.createCashMisPayTrace(posPayTraceData);
                }else {
                    cardOrderPayTraceService.updateMisOrderState(posPayTraceData);
                }
                return orderCodeOri;
            }
        }
        String orderCode = cardOrderPayTraceService.createPosPayTraceFromCashier(posPayTraceData);
        return orderCode;
    }


    /**
     * 创建其他支付方式的支付成功流水
     * @param posPayTraceData
     */
    @PostMapping("/createMisSuccessTrace")
    public String createMisSuccessTrace(@RequestBody PosPayTraceData posPayTraceData){

        logger.info("获取到的订单号为:"+posPayTraceData.getOrderCode());

        String orderCodeOri = posPayTraceData.getOrderCode();
//        CardOrdersVO cardOrdersVO = cardOrdersService.queryByOrderCode(orderCodeOri);

        QueryWrapper<CardOrders> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCodeOri);
        CardOrders cardOrdersVO = cardOrdersService.getOne(queryWrapper);

//        System.out.println("order========="+ cardOrdersVO);


        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setOrderCode(orderCodeOri);

        List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceService.queryTraceByOrderCode(orderCodeOri);

        if (cardOrderPayTraces!=null && cardOrderPayTraces.size()>0){
            cardOrderPayTrace.setPayCode(cardOrderPayTraces.get(0).getPayCode());
        }else {
            cardOrderPayTrace.setPayCode(posPayTraceData.getTraceNo());
        }
        cardOrderPayTrace.setRefTraceNo(posPayTraceData.getTraceNo());
        cardOrderPayTrace.setTraceNo(IdWorker.getIdStr());
        cardOrderPayTrace.setType(CardOrderPayTraceTypeConfig.CASH_PAY);
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setSource(CardOrderPayTraceSourceDescConfig.POS_CASH);
        cardOrderPayTrace.setSourceId(StringUtils.isEmpty(posPayTraceData.getCardNo()) ? posPayTraceData.getCups() : posPayTraceData.getCardNo());
        cardOrderPayTrace.setAmount(posPayTraceData.getAmount());
        cardOrderPayTrace.setPosSerialNum(posPayTraceData.getTerId());
        cardOrderPayTrace.setUserFlag(posPayTraceData.getUserId()+"");
        cardOrderPayTrace.setMerchantCode(posPayTraceData.getMerchantCode().equals("HLMSD") ? "HLSC" : posPayTraceData.getMerchantCode());
        cardOrderPayTrace.setMerchId(posPayTraceData.getMerchId());
        cardOrderPayTrace.setMerchName(posPayTraceData.getMerchName());
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());

        cardOrderPayTraceService.save(cardOrderPayTrace);
        return orderCodeOri;
    }

    /**
     * 非组合支付,记录支付流水,订单
     * @param posPayTraceData
     */
    @PostMapping("/usuallyUserPayTrace")
    public void usuallyUserPayTrace(@RequestBody PosPayTraceData posPayTraceData) {
        cardOrderPayTraceService.createUsuallyUserPayTrace(posPayTraceData);
    }

    /**
     * 根据订单号查询 流水接口
     * @param orderCode
     */
    @GetMapping("/queryPayTrace")
    public List<CardOrderPayTrace>queryPayTrace(@RequestParam("orderCode") String orderCode){
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return cardOrderPayTraceService.list(queryWrapper);
    }

    /**
     * 创建云MIS订单待支付流水
     * @param misOrderData
     */
    @PostMapping("/createMisOrderPayTrace")
    public CardOrderPayTrace createMisOrderPayTrace(@RequestBody MisOrderData misOrderData){
        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        String misOrderPayTrace = cardOrderPayTraceService.createMisOrderPayTrace(misOrderData);
        cardOrderPayTrace.setPayCode(misOrderPayTrace);
        return cardOrderPayTrace;
    }

    /**
     * 云mis订单支付成功,修改订单状态
     * @param posPayTraceData
     */
    @PostMapping("/updateMisOrderState")
    public void updateMisOrderState(@RequestBody PosPayTraceData posPayTraceData){
        cardOrderPayTraceService.updateMisOrderState(posPayTraceData);
    }

    /**
     * 云mis订单组合支付
     * @param posPayTraceData
     */
    @PostMapping("/updateVipMisOrderState")
    public void updateVipMisOrderState(@RequestBody PosPayTraceData posPayTraceData){
        cardOrderPayTraceService.updateVipMisOrderState(posPayTraceData);
    }

    /**
     * 创建电子卡支付 订单明细与流水 (基于云mis订单支付)
     * @param consumeMoney
     * @param orderCode
     * @param cardNo
     */
    @PostMapping("/createCardElectronicPayTrace")
    public void createCardElectronicPayTrace(@RequestParam("consumeMoney") int consumeMoney,
                                             @RequestParam("orderCode") String orderCode,
                                             @RequestParam("cardNo") String cardNo,
                                             @RequestParam("userId") long userId,
                                             @RequestParam("terId")String terId){
        cardOrderPayTraceService.createCardElectronicPayTrace(consumeMoney,orderCode,cardNo,userId,terId);
    }

    /**
     * 创建电子卡支付 订单明细与流水
     * @param consumeMoney
     * @param orderCode
     * @param cardNo
     * @param userId
     */
    @PostMapping("/createCardPhysicalPayTrace")
    public void createCardPhysicalPayTrace(@RequestParam("consumeMoney") int consumeMoney,
                                    @RequestParam("orderCode") String orderCode,
                                    @RequestParam("cardNo") String cardNo,
                                    @RequestParam("userId") long userId,
                                    @RequestParam("terId")String terId,
                                    @RequestBody CardPhysical cardPhysical){
        cardOrderPayTraceService.createCardPhysicalPayTrace(consumeMoney,orderCode,cardNo,userId,terId,cardPhysical);
    }

    /**
     * 根据订单号和商户号查询流水
     * @param orderCode
     * @param merchantCode
     * @return
     */
    @GetMapping("/queryPayTraceByOrderCodeAndMerchantCode")
    public List<CardOrderPayTrace> queryPayTraceByOrderCodeAndMerchantCode(@RequestParam("orderCode")String orderCode,
                                                                            @RequestParam(value = "merchantCode",defaultValue = "HLSC",required = false)String merchantCode){
        return cardOrderPayTraceService.queryPayTraceByOrderCodeAndMerchantCode(orderCode,merchantCode);
    }


    /**
     * 创建 富基对接的 组合支付流水
     * @param saveCardPayTraceList
     * @param totalAmount
     */
    @PostMapping("/createPosCombinationPayTrace")
    public void createPosCombinationPayTrace(@RequestBody List<CardOrderPayTrace> saveCardPayTraceList,
                                             @RequestParam("totalAmount") Integer totalAmount,
                                             @RequestParam("userId")Long userId,
                                             @RequestParam(value = "storeCode",required = false)String storeCode,
                                             @RequestParam(value = "actualPhone",required = false)String actualPhone,
                                             @RequestParam(value = "idCardNo",required = false)String idCardNo){
        cardOrderPayTraceService.createPosCombinationPayTrace(saveCardPayTraceList,totalAmount,userId,storeCode,actualPhone,idCardNo);
    }

    @PostMapping("/testStr")
    public String testStr(){
        String idStr = IdWorker.getIdStr();
        return idStr;
    }

    /**
     * 创建 富基对接的 现金支付 支付流水
     * @param posCombinationPaySuccess
     */
    @PostMapping("/createPosCombinationCashPay")
    public void createPosCombinationCashPay(@RequestBody PosCombinationPaySuccess posCombinationPaySuccess){
        cardOrderPayTraceService.createPosCombinationCashPay(posCombinationPaySuccess);
    }

    /**
     * 修改退款状态
     * @param refundCardOrderPayTraces
     */
    @PostMapping("/updateRefundData")
    public void updateRefundData(@RequestBody List<CardOrderPayTrace> refundCardOrderPayTraces){
        cardOrderPayTraceService.updateRefundData(refundCardOrderPayTraces);
    }

    /**
     * 查询用户流水
     * @param userFlag
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/queryUserPayTrace")
    public Page<CardOrderPayTrace> queryUserPayTrace(@RequestParam("userFlag")String userFlag,
                                                     @RequestParam("pageNo")String pageNo,
                                                     @RequestParam("pageSize")String pageSize){
        return cardOrderPayTraceService.queryUserPayTrace(userFlag, pageNo, pageSize);
    }

    @GetMapping("/queryUserConsumeOrder")
    public Page<CardOrderPayTrace> queryUserConsumeOrder(@RequestParam("pageNo") Long pageNo,@RequestParam("pageSize") Long pageSize,
                                                          @RequestParam("phoneNum") String phoneNum,@RequestParam("openId") String openId){
        return cardOrderPayTraceService.queryUserConsumeOrder(openId,phoneNum, pageNo, pageSize);
    }

    /**
     * 根据流水号查询支付流水
     * @param payCode
     * @param merchantCode
     * @return
     */
    @GetMapping("/queryTraceByPayCode")
    public List<CardOrderPayTrace> queryTraceByPayCode(@RequestParam("payCode") String payCode,
                                                        @RequestParam(value = "merchantCode",required = false,defaultValue = "HLSC")String merchantCode){
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("pay_code",payCode);
        queryWrapper.eq("merchant_code",merchantCode);
        return cardOrderPayTraceService.list(queryWrapper);
    }

    /**
     * pos查询交易明细
     * @param searchPosOrderListData
     * @return
     */
    @PostMapping("/posOrderList")
    public Page<CardOrderPayTrace> posOrderList(@RequestBody SearchPosOrderListData searchPosOrderListData){
        return cardOrderPayTraceService.posOrderList(searchPosOrderListData);
    }

    @GetMapping("/querySummaryData")
    public List<CardOrderPayTrace> querySummaryData(@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime){
        return cardOrderPayTraceService.querySummaryData(startTime,endTime);
    }

    @GetMapping("/querySummaryDataForMerchantCode")
    public List<CardOrderPayTrace> querySummaryDataForMerchantCode(@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime,@RequestParam("merchantCode") String merchantCode){
        return cardOrderPayTraceService.querySummaryDataForMerChantCode(startTime,endTime,merchantCode);
    }

    @PostMapping("/fixCardOrderTraceUser")
    public void fixCardOrderTraceUser(@RequestParam("orderCode") String orderCode,@RequestParam("userPhone") String userPhone){
        List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceService.queryTraceByOrderCode(orderCode);
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
            cardOrderPayTrace.setUserFlag(userPhone);
            cardOrderPayTrace.setUpdateAt(cardOrderPayTrace.getCreateAt());
            cardOrderPayTraceService.updateById(cardOrderPayTrace);
        }
    }

    @GetMapping("/queryConsumeAll")
    public List<CardOrderPayTrace> queryConsumeAll(){
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("type","card_electronic").or().eq("type","card_physical");
        List<CardOrderPayTrace> list = cardOrderPayTraceService.list(queryWrapper);
        return list;
    }

    @PostMapping("/fixOrderTraceRefUser")
    public void fixOrderTraceRefUser(@RequestBody List<CardOrderPayTrace> cardOrderPayTraces){
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
            cardOrderPayTraceService.updateById(cardOrderPayTrace);
        }
    }

    @GetMapping("/batchConsumeExcel")
    public void batchConsumeExcel(HttpServletResponse response,
                                  @RequestParam("batchCode")String batchCode){
        List<ConsumeCardOrderExcelVo> consumeCardOrderExcelVos= cardOrderPayTraceService.batchConsumeExcel(batchCode);
        System.out.println(consumeCardOrderExcelVos);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName=new String(("消费订单导出"+date + ".xlsx").getBytes("gb2312"),  "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            EasyExcel.write(response.getOutputStream(), ConsumeCardOrderExcelVo.class).sheet("Sheet1").doWrite(consumeCardOrderExcelVos);
        } catch (Exception e) {
            logger.info("告警信息下载失败" + e.getMessage());
        }
    }
}

