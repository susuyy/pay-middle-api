package com.ht.feignapi.prime.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.prime.cardenum.CardElectronicEnum;
import com.ht.feignapi.prime.cardenum.PayTraceTypeSourceEnum;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.controller.PrimeOrderController;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.prime.excel.CardRefundOrderExcelData;
import com.ht.feignapi.prime.excel.ConsumeOrdersMasterExcelData;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderPayTraceClientService;
import com.ht.feignapi.tonglian.card.entity.CardOrders;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceStateConfig;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceTypeConfig;
import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.config.CardOrdersTypeConfig;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.util.DateStrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.smartcardio.Card;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.ht.feignapi.mall.constant.OrderConstant.REFUND;
import static com.ht.feignapi.tonglian.config.CardOrdersStateConfig.PAID;
import static com.ht.feignapi.tonglian.config.CardOrdersTypeConfig.CONSUME;

@Service
public class PrimeOrderService {
    private Logger logger = LoggerFactory.getLogger(PrimeOrderService.class);

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;

    /**
     * 查询购卡订单
     * @param phone
     * @param state
     * @param cardNo
     * @param traceNo
     * @param pageNo
     * @param pageSize
     * @param orderNo
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     */
    public Page<CardOrders> getPrimeBuyCardOrders(String phone, String state, String cardNo, String traceNo, Long pageNo, Long pageSize, String orderNo, String startTime, String endTime,Long userId) {
        Result<Page<CardOrders>> ordersResult;
        if (userId!=null) {
            ordersResult = orderClientService.getOrderList(pageNo, pageSize, orderNo, startTime, endTime, state, userId,phone,CardOrdersTypeConfig.PRIME_BUY_CARD, cardNo, traceNo);
        }else {
            ordersResult = orderClientService.getOrderListPhone(pageNo, pageSize, orderNo, startTime, endTime, state, phone , CardOrdersTypeConfig.PRIME_BUY_CARD, cardNo, traceNo);
        }

        if (!ordersResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode()) || ObjectUtils.isEmpty(ordersResult.getData())) {
            return new Page<CardOrders>();
        }
        ordersResult.getData().getRecords().forEach(e -> {


            VipUser vipUser = msPrimeClient.queryUserById(e.getUserId()+"").getData();

            if (e.getState().equals(PAID) || e.getState().equals(REFUND)){
                decorateOrderDetailList(e);
            }
            decorateOrder(e, vipUser);
            List<CardOrderPayTrace> cardOrderPayTraceList = cardOrderPayTraceClientService.queryPayTrace(e.getOrderCode()).getData();
            if ((cardOrderPayTraceList == null) || (cardOrderPayTraceList.size() < 1)){
                e.setCollectMoneyType("未知收款方式");
            }else {
                e.setCollectMoneyType(cardOrderPayTraceList.get(0).getSource());
                if (vipUser == null) {
                    if (PayTraceTypeSourceEnum.TL_POS.getValue().equals(cardOrderPayTraceList.get(0).getType())
                            || PayTraceTypeSourceEnum.ACTUAL_CASH.getValue().equals(cardOrderPayTraceList.get(0).getType())
                            || PayTraceTypeSourceEnum.COMPANY_PAY.getValue().equals(cardOrderPayTraceList.get(0).getType())
                            || PayTraceTypeSourceEnum.REMITTANCE_PAY.getValue().equals(cardOrderPayTraceList.get(0).getType())
                            || PayTraceTypeSourceEnum.FREE.getValue().equals(cardOrderPayTraceList.get(0).getType())
                            || PayTraceTypeSourceEnum.OTHER_PAY.getValue().equals(cardOrderPayTraceList.get(0).getType())) {
                        e.setPhone(cardOrderPayTraceList.get(0).getUserFlag());
                    }
                }

            }
        });

        return ordersResult.getData();
    }

    /**
     * 封装订单明细数据
     * @param cardOrders
     */
    private void decorateOrderDetailList(CardOrders cardOrders) {
        List<CardOrderDetailsVo> orderDetailsList = cardOrders.getOrderDetailsList();
        List<CardOrderDetailsVo> detailsVos = new ArrayList<>();
        for (CardOrderDetailsVo cardOrderDetailsVo : orderDetailsList) {
            if (!StringUtils.isEmpty(cardOrderDetailsVo.getProductionCode())) {
                ArrayList<String> cardList = JSON.parseObject(cardOrderDetailsVo.getProductionCode(), ArrayList.class);
                for (String cardNo : cardList) {
                    CardOrderDetailsVo detailsVo = new CardOrderDetailsVo();
                    BeanUtils.copyProperties(cardOrderDetailsVo, detailsVo);
                    CardElectronic cardElectronic = msPrimeClient.queryCardElectronicByCardNo(cardNo).getData();
                    if (cardElectronic != null) {
                        detailsVo.setCardType(cardElectronic.getCardType());
                        detailsVo.setFaceValue(cardElectronic.getAmount().toString());
                    }
                    detailsVo.setCardNo(cardNo);
                    detailsVo.setBoughtPrice(String.valueOf(cardOrderDetailsVo.getAmount() / cardOrderDetailsVo.getQuantity().intValue()));
                    detailsVo.setProductionCode(cardNo);
                    detailsVo.setQuantity(BigDecimal.ONE);
                    detailsVo.setAmount(Integer.parseInt(String.valueOf(cardOrderDetailsVo.getAmount() / cardOrderDetailsVo.getQuantity().intValue())));
                    detailsVos.add(detailsVo);
                }
            }else {
                CardOrderDetailsVo detailsVo = new CardOrderDetailsVo();
                BeanUtils.copyProperties(cardOrderDetailsVo, detailsVo);
                detailsVo.setCardType("");
                detailsVo.setFaceValue("");
                detailsVo.setCardNo("");
                detailsVo.setBoughtPrice(String.valueOf(cardOrderDetailsVo.getAmount() / cardOrderDetailsVo.getQuantity().intValue()));
                detailsVo.setProductionCode("");
                detailsVo.setQuantity(BigDecimal.ONE);
                detailsVo.setAmount(Integer.parseInt(String.valueOf(cardOrderDetailsVo.getAmount() / cardOrderDetailsVo.getQuantity().intValue())));
                detailsVos.add(detailsVo);
            }
        }
        cardOrders.setOrderDetailsList(detailsVos);
    }

    /**
     * 封装订单用户数据
     * @param cardOrders
     * @param vipUser
     */
    private void decorateOrder(CardOrders cardOrders, VipUser vipUser) {
        if (vipUser == null){
            cardOrders.setUserName("不记名用户");
            cardOrders.setPhone("未记录手机");
        }else {
            cardOrders.setUserName(vipUser.getNickName());
            cardOrders.setPhone(vipUser.getPhoneNum());
        }
        Integer realPayAmount = 0;
        if (CollectionUtils.isEmpty(cardOrders.getPayTraceList())){
            cardOrders.setRealPayAmount(0);
        }else {
            for (CardOrderPayTrace cardOrderPayTrace : cardOrders.getPayTraceList()) {
                if ("paid".equals(cardOrderPayTrace.getState())
                ||"refund".equals(cardOrderPayTrace.getState())
                ||"cancel".equals(cardOrderPayTrace.getState())){
                    realPayAmount = realPayAmount + cardOrderPayTrace.getAmount();
                }
                cardOrders.setCashId(cardOrderPayTrace.getCashId());
                if (CardOrdersStateConfig.UNPAID.equals(cardOrderPayTrace.getState())){
                    cardOrders.setPos_serial_num("");
                }else {
                    cardOrders.setPos_serial_num(cardOrderPayTrace.getPosSerialNum());
                }
            }
            cardOrders.setRealPayAmount(realPayAmount);
        }
//        cardOrders.setRealPayAmount(CollectionUtils.isEmpty(cardOrders.getPayTraceList()) ? 0 : cardOrders.getPayTraceList().stream().mapToInt(CardOrderPayTrace::getAmount).sum());
    }

    /**
     * 查询消费订单
     * @param phone
     * @param state
     * @param cardNo
     * @param traceNo
     * @param pageNo
     * @param pageSize
     * @param orderNo
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     */
    public Page<CardOrders> getConsumeOrders(String phone, String state, String cardNo, String traceNo, Long pageNo, Long pageSize, String orderNo, String startTime, String endTime, Long userId) {
        Result<Page<CardOrders>> ordersResult = orderClientService.getOrderList(pageNo,pageSize,orderNo,startTime,endTime, state,userId,phone,CONSUME,cardNo,traceNo);
        if (!ordersResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode())|| ObjectUtils.isEmpty(ordersResult.getData())){
            return new Page<CardOrders>();
        }
        ordersResult.getData().getRecords().forEach(e -> {
            VipUser vipUser = new VipUser();
            if (-1==(e.getUserId())){
                vipUser.setPhoneNum("不记名用户");
                vipUser.setNickName("不记名用户");
            }else {
                vipUser = msPrimeClient.queryUserById(e.getUserId().toString()).getData();
            }
            decorateOrder(e, vipUser);
            if ("refund".equals(e.getState()) || "cancel".equals(e.getState())){
                List<CardRefundOrder> data = msPrimeClient.queryRefundOrder(e.getOrderCode(),e.getMerchantCode(),"").getData();
                if (data!=null && data.size()>0){
                    e.setRefundOperator(data.get(0).getExt2());
                }else {
                    e.setRefundOperator("");
                }
            }else {
                e.setRefundOperator("");
            }
        });
        return ordersResult.getData();
    }

    /**
     * 金额 分 转 元
     * @param e
     * @return
     */
    private String parsePriceToYuan(String e){
        if (!StringUtils.isEmpty(e)){
            BigDecimal amount = new BigDecimal(e);
            return String.valueOf(amount.divide(new BigDecimal(100)));
        }
        return "0";
    }

    /**
     * 封装消费订单excel数据
     * @param cardOrderPayTraceList
     * @return
     */
    public List<ConsumeCardOrderExcelVo> packageConsumeExcelListData(List<CardOrderPayTrace> cardOrderPayTraceList) {
        Integer rowNum = 1;
        List<ConsumeCardOrderExcelVo> list = new ArrayList<>();

        Set<String> userIdSet = new HashSet<>();
        Set<String> phyCardNoSet = new HashSet<>();
        Set<String> eleCardNoSet = new HashSet<>();

        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {

            ConsumeCardOrderExcelVo consumeCardOrderExcelVo = new ConsumeCardOrderExcelVo();
            consumeCardOrderExcelVo.setRowNum(rowNum+"");
            consumeCardOrderExcelVo.setOrderCode(cardOrderPayTrace.getOrderCode());

            consumeCardOrderExcelVo.setPayCode(cardOrderPayTrace.getPayCode());

            consumeCardOrderExcelVo.setAmount(parsePriceToYuan(cardOrderPayTrace.getOrderMasterAmount()));
            consumeCardOrderExcelVo.setDetailAmount(parsePriceToYuan(cardOrderPayTrace.getAmount()+""));
            consumeCardOrderExcelVo.setReceiveAmount(parsePriceToYuan(cardOrderPayTrace.getAmount()+""));
            consumeCardOrderExcelVo.setPayType(cardOrderPayTrace.getSource());

            if (cardOrderPayTrace.getState().equals(PAID)||cardOrderPayTrace.getState().equals(REFUND)) {
                consumeCardOrderExcelVo.setState(cardOrderPayTrace.getState().equals(PAID) ? "已支付" : "已退款");
            }else if (cardOrderPayTrace.getState().equals("close")){
                consumeCardOrderExcelVo.setState("订单关闭,未支付");
                consumeCardOrderExcelVo.setPayType("");
            }else if (cardOrderPayTrace.getState().equals("cancel")){
                consumeCardOrderExcelVo.setState("已撤销");
            }else {
                consumeCardOrderExcelVo.setState("未支付");
                consumeCardOrderExcelVo.setPayType("");
            }

            //购卡来源
            consumeCardOrderExcelVo.setBuyCardSource(StringUtils.isEmpty(cardOrderPayTrace.getRefCardBrhId())? "HLMSD" : cardOrderPayTrace.getRefCardBrhId());

//            if ("refund".equals(cardOrderPayTrace.getState()) || "cancel".equals(cardOrderPayTrace.getState())){
//                List<CardRefundOrder> data = msPrimeClient.queryRefundOrder(cardOrderPayTrace.getOrderCode(),cardOrderPayTrace.getMerchantCode(),"").getData();
//                if (data!=null && data.size()>0){
//                    consumeCardOrderExcelVo.setRefundOperator(data.get(0).getExt2());
//                    consumeCardOrderExcelVo.setRefundTime(DateStrUtil.dateToStr(data.get(0).getCreateAt()));
//                }else {
//                    consumeCardOrderExcelVo.setRefundOperator("");
//                    consumeCardOrderExcelVo.setRefundTime("");
//                }
//            }else {
//                consumeCardOrderExcelVo.setRefundOperator("");
//                consumeCardOrderExcelVo.setRefundTime("");
//            }

            consumeCardOrderExcelVo.setComments(cardOrderPayTrace.getOrderMasterDesc());
            consumeCardOrderExcelVo.setCreatAt(DateStrUtil.dateToStr(cardOrderPayTrace.getCreateAt()));

            if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType()) || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType())){
                consumeCardOrderExcelVo.setCardNo(cardOrderPayTrace.getSourceId());
                if (cardOrderPayTrace.getSourceId().contains("ELECARD")){
//                    CardElectronic data = msPrimeClient.queryCardElectronicByCardNo(cardOrderPayTrace.getSourceId()).getData();
//                    if (data!=null) {
//                        consumeCardOrderExcelVo.setCardType(CardElectronicEnum.getDescByValueKey(data.getCardType()));
//                        consumeCardOrderExcelVo.setCardName(data.getCardName());
//                        consumeCardOrderExcelVo.setBatchCode(data.getBatchCode());
//
//                        consumeCardOrderExcelVo.setCardFaceValue(parsePriceToYuan(data.getFaceValue()));
//                    }
                    eleCardNoSet.add(cardOrderPayTrace.getSourceId());
                }else {
//                    CardPhysical data = msPrimeClient.queryByCardCode(cardOrderPayTrace.getSourceId()).getData();
//                    if (data!=null) {
//                        consumeCardOrderExcelVo.setCardType(CardElectronicEnum.getDescByValueKey(data.getCardType()));
//                        consumeCardOrderExcelVo.setCardName(data.getCardName());
//                        consumeCardOrderExcelVo.setBatchCode(data.getBatchCode());
//
//                        consumeCardOrderExcelVo.setCardFaceValue(parsePriceToYuan(data.getFaceValue()));
//                    }
                    phyCardNoSet.add(cardOrderPayTrace.getSourceId());
                }
            }
            consumeCardOrderExcelVo.setUserId(cardOrderPayTrace.getOrderMasterUserId());
            userIdSet.add(cardOrderPayTrace.getOrderMasterUserId()+"");
//            VipUser vipUser = msPrimeClient.queryUserById(cardOrderPayTrace.getOrderMasterUserId()+"").getData();
//            consumeCardOrderExcelVo.setPhone(vipUser ==null ? "未找到手机" : vipUser.getPhoneNum());
            consumeCardOrderExcelVo.setSellType("消费支出");

            consumeCardOrderExcelVo.setCardType(cardOrderPayTrace.getRefCardType());
            consumeCardOrderExcelVo.setCardName(cardOrderPayTrace.getRefCardName());
            consumeCardOrderExcelVo.setBatchCode(cardOrderPayTrace.getRefBatchCode());
            consumeCardOrderExcelVo.setCardFaceValue(cardOrderPayTrace.getRefRemainFaceValue());

            list.add(consumeCardOrderExcelVo);
            rowNum = rowNum+1;
        }

        List<QueryUserPhoneExcel> queryUserPhoneExcels = msPrimeClient.queryUserIdPhoneList(userIdSet).getData();
        Map<Long, String> userIdPhoneMap = queryUserPhoneExcels.stream().collect(Collectors.toMap(
                QueryUserPhoneExcel::getUserId,
                QueryUserPhoneExcel::getPhone));

        List<QueryExcelBuyCardData> queryExcelBuyCardDataList = msPrimeClient.queryByCardNoList(eleCardNoSet).getData();
        Map<String, CardElectronic> cardElectronicMap = queryExcelBuyCardDataList.stream().collect(Collectors.toMap(
                QueryExcelBuyCardData::getCardNo,
                QueryExcelBuyCardData::getCardElectronic));

        List<QueryExcelPhyCardData> queryExcelPhyCardDataList = msPrimeClient.queryByPhyCardNoList(phyCardNoSet).getData();
        Map<String, CardPhysical> cardPhysicalMap = queryExcelPhyCardDataList.stream().collect(Collectors.toMap(
                QueryExcelPhyCardData::getCardNo,
                QueryExcelPhyCardData::getCardPhysical));

        for (ConsumeCardOrderExcelVo consumeCardOrderExcelVo : list) {
            String userPhone = userIdPhoneMap.get(consumeCardOrderExcelVo.getUserId());
            consumeCardOrderExcelVo.setPhone( StringUtils.isEmpty(userPhone)? "未找到手机" : userPhone);

            //电子卡
            CardElectronic cardElectronic = cardElectronicMap.get(consumeCardOrderExcelVo.getCardNo());
            if (cardElectronic!=null) {
                consumeCardOrderExcelVo.setCardType(CardElectronicEnum.getDescByValueKey(cardElectronic.getCardType()));
                consumeCardOrderExcelVo.setCardName(cardElectronic.getCardName());
                consumeCardOrderExcelVo.setBatchCode(cardElectronic.getBatchCode());
                consumeCardOrderExcelVo.setCardFaceValue(parsePriceToYuan(cardElectronic.getFaceValue()));
            }

            //实体卡
            CardPhysical cardPhysical = cardPhysicalMap.get(consumeCardOrderExcelVo.getCardNo());
            if (cardPhysical!=null) {
                consumeCardOrderExcelVo.setCardType(CardElectronicEnum.getDescByValueKey(cardPhysical.getCardType()));
                consumeCardOrderExcelVo.setCardName(cardPhysical.getCardName());
                consumeCardOrderExcelVo.setBatchCode(cardPhysical.getBatchCode());
                consumeCardOrderExcelVo.setCardFaceValue(parsePriceToYuan(cardPhysical.getFaceValue()));
            }

        }

        return list;
    }

    /**
     * 封装购卡订单excel数据
     * @param result
     * @return
     */
    public List<PrimeBuyCardOrderExcelVo> packageBuyCardExcelOrder(Result<List<PrimeBuyCardOrderExcelVo>> result) {
        List<PrimeBuyCardOrderExcelVo> list = new ArrayList<>();
        Integer rowNum = 1;
        Set<String> queryExcelBuyCardNoDataSet = new HashSet<>();
        Set<String> set=new HashSet<>();
        for (PrimeBuyCardOrderExcelVo e:result.getData()) {
            if (e.getState().equals(PAID)||e.getState().equals(REFUND)) {
                if (!StringUtils.isEmpty(e.getProductionCode())){
                    ArrayList<String> cardList = JSON.parseObject(e.getProductionCode(), ArrayList.class);
                    for (String c:cardList) {
                        PrimeBuyCardOrderExcelVo excelVo = new PrimeBuyCardOrderExcelVo();
                        BeanUtils.copyProperties(e, excelVo);
                        excelVo.setCardNo(c);
                        queryExcelBuyCardNoDataSet.add(c);

                        excelVo.setState(e.getState().equals(PAID)?"已支付":"已退款");
                        excelVo.setRowNum(String.valueOf(rowNum));
                        int detailInt = Integer.parseInt(e.getDetailAmount())/e.getQuantity();
                        excelVo.setDetailAmount(parsePriceToYuan(detailInt+""));
                        excelVo.setReceiveAmount(parsePriceToYuan(detailInt+""));
                        decorateExcelVo(list, e, excelVo);
                        rowNum = rowNum+1;
                    }
                }else {
                    PrimeBuyCardOrderExcelVo excelVo = new PrimeBuyCardOrderExcelVo();
                    BeanUtils.copyProperties(e, excelVo);
                    excelVo.setCardNo("");
                    excelVo.setState(e.getState().equals(PAID)?"已支付":"已退款");
                    excelVo.setRowNum(String.valueOf(rowNum));
                    int detailInt = Integer.parseInt(e.getDetailAmount())/e.getQuantity();
                    excelVo.setDetailAmount(parsePriceToYuan(detailInt+""));
                    excelVo.setReceiveAmount(parsePriceToYuan(detailInt+""));
                    decorateExcelVo(list, e, excelVo);
                    rowNum = rowNum+1;
                }
            }else {
                PrimeBuyCardOrderExcelVo excelVo = new PrimeBuyCardOrderExcelVo();
                BeanUtils.copyProperties(e, excelVo);
                excelVo.setState("未支付");
                excelVo.setReceiveAmount("0");
                excelVo.setDetailAmount("0");
                excelVo.setRowNum(String.valueOf(rowNum));
                decorateExcelVo(list, e, excelVo);
                rowNum = rowNum+1;
            }

            if (!StringUtils.isEmpty(e.getPhone()) && e.getPhone().length()<10){
                set.add(e.getPhone());
            }

        }

        List<QueryExcelBuyCardData> queryExcelBuyCardDataList = msPrimeClient.queryByCardNoList(queryExcelBuyCardNoDataSet).getData();
        Map<String, CardElectronic> cardElectronicMap = queryExcelBuyCardDataList.stream().collect(Collectors.toMap(
                QueryExcelBuyCardData::getCardNo,
                QueryExcelBuyCardData::getCardElectronic));

        List<QueryUserPhoneExcel> queryUserPhoneExcels = msPrimeClient.queryUserIdPhoneList(set).getData();
        Map<Long, String> userIdPhoneMap = queryUserPhoneExcels.stream().collect(Collectors.toMap(
                QueryUserPhoneExcel::getUserId,
                QueryUserPhoneExcel::getPhone));

        for (PrimeBuyCardOrderExcelVo e:list) {
            CardElectronic cardElectronic = cardElectronicMap.get(e.getCardNo());
            if (cardElectronic!=null){
                e.setCardName(cardElectronic.getCardName());
                e.setCardType(CardElectronicEnum.getDescByValueKey(cardElectronic.getCardType()));
                e.setBatchCode(cardElectronic.getBatchCode());
                if (cardElectronic.getBatchCode().contains("HLMSD")){
                    e.setBuyCardSource("海旅免");
                }else {
                    e.setBuyCardSource("通华");
                }
            }
            if (!StringUtils.isEmpty(e.getPhone()) && e.getPhone().length()<10){
                e.setPhone(userIdPhoneMap.get(Long.parseLong(e.getPhone())));
            }
        }
        return list;
    }

    /**
     * 封装购卡订单明细数据excel
     * @param list
     * @param e
//     * @param userResult
     * @param excelVo
     */
    private void decorateExcelVo(List<PrimeBuyCardOrderExcelVo> list,
                                 PrimeBuyCardOrderExcelVo e,
                                 PrimeBuyCardOrderExcelVo excelVo) {
        excelVo.setAmount(parsePriceToYuan(e.getAmount()));
//        excelVo.setPhone(userResult.getData() == null ? "未找到手机" :userResult.getData().getPhoneNum());
        excelVo.setSellType("售卡");
//        String orderCode = excelVo.getOrderCode();
//        List<CardOrderPayTrace> data = cardOrderPayTraceClientService.queryPayTrace(orderCode).getData();
//        if (data==null || data.size()<1){
//            excelVo.setPayType("未确定收款方式");
//        }else {
//            if (userResult.getData() == null) {
//                if (PayTraceTypeSourceEnum.TL_POS.getValue().equals(data.get(0).getType())
//                        || PayTraceTypeSourceEnum.ACTUAL_CASH.getValue().equals(data.get(0).getType())
//                        || PayTraceTypeSourceEnum.FREE.getValue().equals(data.get(0).getType())
//                        || PayTraceTypeSourceEnum.COMPANY_PAY.getValue().equals(data.get(0).getType())
//                        || PayTraceTypeSourceEnum.REMITTANCE_PAY.getValue().equals(data.get(0).getType())
//                        || PayTraceTypeSourceEnum.OTHER_PAY.getValue().equals(data.get(0).getType())) {
//                    excelVo.setPhone(data.get(0).getUserFlag());
//                    e.setPhone(data.get(0).getUserFlag());
//                }
//            }
//            excelVo.setPayType(data.get(0).getSource());
//            if (PayTraceTypeSourceEnum.FREE.getValue().equals(data.get(0).getType())){
//                excelVo.setDetailAmount("0");
//                excelVo.setReceiveAmount("0");
//            }
//        }
        list.add(excelVo);
    }

    public List<CardRefundOrderExcelData> packageRefundOrderExcelListData(List<CardRefundOrder> cardRefundOrderList) {
        List<CardRefundOrderExcelData> list = new ArrayList<>();
        for (CardRefundOrder cardRefundOrder : cardRefundOrderList) {
            CardRefundOrderExcelData cardRefundOrderExcelData = new CardRefundOrderExcelData();

            cardRefundOrderExcelData.setOrderId(cardRefundOrder.getOrderId());
            cardRefundOrderExcelData.setOriOrderId(cardRefundOrder.getOriOrderId());
            cardRefundOrderExcelData.setAmount(parsePriceToYuan(cardRefundOrder.getAmount()+""));
            cardRefundOrderExcelData.setCardId(cardRefundOrder.getCardId());
            cardRefundOrderExcelData.setSubMsg(cardRefundOrder.getSubMsg());
            cardRefundOrderExcelData.setExt2(cardRefundOrder.getExt2());
            cardRefundOrderExcelData.setUserPhone(cardRefundOrder.getUserPhone());
            cardRefundOrderExcelData.setCreateAt(DateStrUtil.dateToStrSs(cardRefundOrder.getCreateAt()));

            list.add(cardRefundOrderExcelData);
        }
        return list;
    }

    public List<ConsumeOrdersMasterExcelData> packageConsumeOrdersMasterExcelListData(List<CardOrders> cardOrders) {
        List<ConsumeOrdersMasterExcelData> consumeOrdersMasterExcelDataList=new ArrayList<>();
        for (CardOrders cardOrder : cardOrders) {
            ConsumeOrdersMasterExcelData consumeOrdersMasterExcelData = new ConsumeOrdersMasterExcelData();

            consumeOrdersMasterExcelData.setOrderCode(cardOrder.getOrderCode());

            consumeOrdersMasterExcelData.setAmount(parsePriceToYuan(cardOrder.getAmount()+""));

            Integer receiveAmount = 0;
            for (CardOrderPayTrace cardOrderPayTrace : cardOrder.getPayTraceList()) {
                if ("paid".equals(cardOrderPayTrace.getState())
                        ||"refund".equals(cardOrderPayTrace.getState())
                        ||"cancel".equals(cardOrderPayTrace.getState())){
                    receiveAmount = receiveAmount + cardOrderPayTrace.getAmount();
                }
            }
            consumeOrdersMasterExcelData.setReceiveAmount(parsePriceToYuan(receiveAmount+""));

            if ("paid".equals(cardOrder.getState())){
                consumeOrdersMasterExcelData.setState("已支付");
            }else if ("refund".equals(cardOrder.getState())){
                consumeOrdersMasterExcelData.setState("已退款");
            }else if ("cancel".equals(cardOrder.getState())){
                consumeOrdersMasterExcelData.setState("撤销");
            }else if ("unpaid".equals(cardOrder.getState())){
                consumeOrdersMasterExcelData.setState("未支付");
            }
            consumeOrdersMasterExcelData.setCreatAt(DateStrUtil.dateToStrSs(cardOrder.getCreateAt()));
            consumeOrdersMasterExcelDataList.add(consumeOrdersMasterExcelData);
        }
        return consumeOrdersMasterExcelDataList;
    }
}
