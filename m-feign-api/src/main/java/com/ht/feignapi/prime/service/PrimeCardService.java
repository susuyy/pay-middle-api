package com.ht.feignapi.prime.service;

import com.ht.feignapi.prime.cardenum.CardElectronicEnum;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.controller.PrimeUserConsumerController;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderPayTraceClientService;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceTypeConfig;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import com.ht.feignapi.util.DateStrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PrimeCardService {


    private Logger logger = LoggerFactory.getLogger(PrimeUserConsumerController.class);

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;

    public List<SummaryData> summaryNoTimeScope(List<SummaryData> data) {
        long totalAmount = 0L;
        long totalConsumeAmount = 0L;
        long totalRemAmount = 0L;
        long totalRefundAmount = 0L;

        long physicalRefundAmount = 0L;
        long offlineRefundAmount = 0L;
        long onlineSellRefundAmount = 0L;
        long offlineSellRefundAmount = 0L;
        long passwordRefundAmount = 0L;
        long rebatePasswordRefundAmount = 0L;


        List<SummaryCardNoRefundData> summaryCardNoRefundDataList = new ArrayList<>();
        List<CardRefundOrder> cardRefundOrders = msPrimeClient.allRefundList().getData();
        if (cardRefundOrders!=null && cardRefundOrders.size()>0) {
            for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
                if (cardRefundOrder.getCardId().contains("ELECARD")) {
                    SummaryCardNoRefundData summaryCardNoRefundData = new SummaryCardNoRefundData();
                    summaryCardNoRefundData.setCardRefundOrder(cardRefundOrder);
                    summaryCardNoRefundData.setCardNo(cardRefundOrder.getCardId());
                    summaryCardNoRefundDataList.add(summaryCardNoRefundData);
                } else {
                    physicalRefundAmount = physicalRefundAmount + cardRefundOrder.getAmount();
                }
                totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
            }
            if (summaryCardNoRefundDataList!=null && summaryCardNoRefundDataList.size()>0) {
                List<SummaryCardNoRefundData> retSummaryCardNoRefundData = msPrimeClient.summaryRefundCardNoData(summaryCardNoRefundDataList).getData();
                for (SummaryCardNoRefundData retSummaryCardNoRefundDatum : retSummaryCardNoRefundData) {
                    if (CardElectronicEnum.ONLINE_SELL.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        onlineSellRefundAmount = onlineSellRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                    if (CardElectronicEnum.OFFLINE_SELL.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        offlineSellRefundAmount = offlineSellRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                    if (CardElectronicEnum.OFFLINE.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        offlineRefundAmount = offlineRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                    if (CardElectronicEnum.PASSWORD_CARD.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        passwordRefundAmount = passwordRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                    if (CardElectronicEnum.REBATE_PASSWORD_CARD.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        rebatePasswordRefundAmount = rebatePasswordRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                }
            }
        }

        for (SummaryData summaryData : data) {
            if (CardElectronicEnum.ONLINE_SELL.getDesc().equals(summaryData.getCardType())){
                summaryData.setConsumeAmount(summaryData.getConsumeAmount() + onlineSellRefundAmount);
                totalAmount = totalAmount + summaryData.getCardTotalAmount();
                totalConsumeAmount = totalConsumeAmount + summaryData.getConsumeAmount();
                totalRemAmount = totalRemAmount + summaryData.getRemainingAmount();
                summaryData.setRefundAmount(onlineSellRefundAmount);
            }else if (CardElectronicEnum.OFFLINE_SELL.getDesc().equals(summaryData.getCardType())){
                summaryData.setConsumeAmount(summaryData.getConsumeAmount() + offlineSellRefundAmount);
                totalAmount = totalAmount + summaryData.getCardTotalAmount();
                totalConsumeAmount = totalConsumeAmount + summaryData.getConsumeAmount();
                totalRemAmount = totalRemAmount + summaryData.getRemainingAmount();
                summaryData.setRefundAmount(offlineSellRefundAmount);
            }else if (CardElectronicEnum.OFFLINE.getDesc().equals(summaryData.getCardType())){
                summaryData.setConsumeAmount(summaryData.getConsumeAmount() + offlineRefundAmount);
                totalAmount = totalAmount + summaryData.getCardTotalAmount();
                totalConsumeAmount = totalConsumeAmount + summaryData.getConsumeAmount();
                totalRemAmount = totalRemAmount + summaryData.getRemainingAmount();
                summaryData.setRefundAmount(offlineRefundAmount);
            }else if (CardElectronicEnum.PHYSICAL.getDesc().equals(summaryData.getCardType())){
                summaryData.setConsumeAmount(summaryData.getConsumeAmount() + physicalRefundAmount);
                totalAmount = totalAmount + summaryData.getCardTotalAmount();
                totalConsumeAmount = totalConsumeAmount + summaryData.getConsumeAmount();
                totalRemAmount = totalRemAmount + summaryData.getRemainingAmount();
                summaryData.setRefundAmount(physicalRefundAmount);
            }else if (CardElectronicEnum.PASSWORD_CARD.getDesc().equals(summaryData.getCardType())){
                summaryData.setConsumeAmount(summaryData.getConsumeAmount() + passwordRefundAmount);
                totalAmount = totalAmount + summaryData.getCardTotalAmount();
                totalConsumeAmount = totalConsumeAmount + summaryData.getConsumeAmount();
                totalRemAmount = totalRemAmount + summaryData.getRemainingAmount();
                summaryData.setRefundAmount(passwordRefundAmount);
            }else if (CardElectronicEnum.REBATE_PASSWORD_CARD.getDesc().equals(summaryData.getCardType())){
                summaryData.setConsumeAmount(summaryData.getConsumeAmount() + rebatePasswordRefundAmount);
                totalAmount = totalAmount + summaryData.getCardTotalAmount();
                totalConsumeAmount = totalConsumeAmount + summaryData.getConsumeAmount();
                totalRemAmount = totalRemAmount + summaryData.getRemainingAmount();
                summaryData.setRefundAmount(rebatePasswordRefundAmount);
            }
        }
        SummaryData totalSummaryData = new SummaryData();
        totalSummaryData.setCardType("合计数");
        totalSummaryData.setCardTotalAmount(totalAmount);
        totalSummaryData.setConsumeAmount(totalConsumeAmount);
        totalSummaryData.setRemainingAmount(totalRemAmount);
        totalSummaryData.setRefundAmount(totalRefundAmount);
        data.add(totalSummaryData);
        return data;
    }

    public List<SummaryData> summaryTimeScope(List<SummaryData> data, String startTime, String endTime) {
        //统计退款金额
        List<CardRefundOrder> cardRefundOrders = msPrimeClient.adminRefundOrderListNoPage("","","","",startTime,endTime,"").getData();
        long physicalRefundAmount = 0L;
        long offlineRefundAmount = 0L;
        long onlineSellRefundAmount = 0L;
        long offlineSellRefundAmount = 0L;
        long passwordRefundAmount = 0L;
        long rebatePasswordRefundAmount = 0L;
        long totalRefundAmount = 0L;

        if (cardRefundOrders!=null && cardRefundOrders.size()>0) {
            List<SummaryCardNoRefundData> summaryCardNoRefundDataList = new ArrayList<>();
            for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
                if (cardRefundOrder.getCardId().contains("ELECARD")) {
                    SummaryCardNoRefundData summaryCardNoRefundData = new SummaryCardNoRefundData();
                    summaryCardNoRefundData.setCardRefundOrder(cardRefundOrder);
                    summaryCardNoRefundData.setCardNo(cardRefundOrder.getCardId());
                    summaryCardNoRefundDataList.add(summaryCardNoRefundData);
                } else {
                    physicalRefundAmount = physicalRefundAmount + cardRefundOrder.getAmount();
                }
                totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
            }
            if (summaryCardNoRefundDataList!=null && summaryCardNoRefundDataList.size()>0) {
                List<SummaryCardNoRefundData> retSummaryCardNoRefundData = msPrimeClient.summaryRefundCardNoData(summaryCardNoRefundDataList).getData();
                for (SummaryCardNoRefundData retSummaryCardNoRefundDatum : retSummaryCardNoRefundData) {
                    if (CardElectronicEnum.ONLINE_SELL.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        onlineSellRefundAmount = onlineSellRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                    if (CardElectronicEnum.OFFLINE_SELL.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        offlineSellRefundAmount = offlineSellRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                    if (CardElectronicEnum.OFFLINE.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        offlineRefundAmount = offlineRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                    if (CardElectronicEnum.PASSWORD_CARD.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        passwordRefundAmount = passwordRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                    if (CardElectronicEnum.REBATE_PASSWORD_CARD.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        rebatePasswordRefundAmount = rebatePasswordRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                }
            }
        }

        //核销金额
        List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceClientService.querySummaryData(startTime, endTime).getData();
        long physicalConsumeAmount = 0L;
        long offlineConsumeAmount = 0L;
        long onlineSellConsumeAmount = 0L;
        long offlineSellConsumeAmount = 0L;
        long passwordCardConsumeAmount = 0L;
        long rebatePasswordCardConsumeAmount = 0L;

        if (cardOrderPayTraces!=null && cardOrderPayTraces.size()>0) {
            List<SummaryCardNoData> summaryCardNoDataList = new ArrayList<>();

            for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
                String type = cardOrderPayTrace.getType();
                if (CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(type)) {
                    Integer amount = cardOrderPayTrace.getAmount();
                    physicalConsumeAmount = physicalConsumeAmount + amount;
                } else if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(type)) {
                    SummaryCardNoData summaryCardNoData = new SummaryCardNoData();
                    summaryCardNoData.setCardOrderPayTrace(cardOrderPayTrace);
                    summaryCardNoData.setCardNo(cardOrderPayTrace.getSourceId());
                    summaryCardNoDataList.add(summaryCardNoData);
                }
            }
            if (summaryCardNoDataList!=null && summaryCardNoDataList.size()>0) {
                List<SummaryCardNoData> retSummaryCardNoDataList = msPrimeClient.summaryCardNoType(summaryCardNoDataList).getData();
                for (SummaryCardNoData summaryCardNoData : retSummaryCardNoDataList) {
                    if (CardElectronicEnum.OFFLINE.getValue().equals(summaryCardNoData.getCardType())) {
                        offlineConsumeAmount = offlineConsumeAmount + summaryCardNoData.getCardOrderPayTrace().getAmount();
                    } else if (CardElectronicEnum.ONLINE_SELL.getValue().equals(summaryCardNoData.getCardType())) {
                        onlineSellConsumeAmount = onlineSellConsumeAmount + summaryCardNoData.getCardOrderPayTrace().getAmount();
                    } else if (CardElectronicEnum.OFFLINE_SELL.getValue().equals(summaryCardNoData.getCardType())) {
                        offlineSellConsumeAmount = offlineSellConsumeAmount + summaryCardNoData.getCardOrderPayTrace().getAmount();
                    } else if (CardElectronicEnum.PASSWORD_CARD.getValue().equals(summaryCardNoData.getCardType())) {
                        passwordCardConsumeAmount = passwordCardConsumeAmount + summaryCardNoData.getCardOrderPayTrace().getAmount();
                    } else if (CardElectronicEnum.REBATE_PASSWORD_CARD.getValue().equals(summaryCardNoData.getCardType())) {
                        rebatePasswordCardConsumeAmount = rebatePasswordCardConsumeAmount + summaryCardNoData.getCardOrderPayTrace().getAmount();
                    }
                }
            }
        }


        List<CardOrderDetails> cardOrderDetailsList = orderClientService.querySummaryDetails(startTime, endTime).getData();
        long physicalFaceValueAmount = 0L;
        long onlineSellFaceValueAmount = 0L;
        long offlineSellFaceValueAmount = 0L;
        long passwordCardFaceValueAmount = 0L;

        if (cardOrderDetailsList!=null && cardOrderDetailsList.size()>0) {
            List<SummaryCardNoDetailData> summaryCardNoDetailDataList = new ArrayList<>();
            for (CardOrderDetails cardOrderDetails : cardOrderDetailsList) {
                SummaryCardNoDetailData summaryCardNoDetailData = new SummaryCardNoDetailData();
                summaryCardNoDetailData.setCardOrderDetails(cardOrderDetails);
                summaryCardNoDetailData.setBatchCode(cardOrderDetails.getBatchCode());
                summaryCardNoDetailDataList.add(summaryCardNoDetailData);
            }
            if (summaryCardNoDetailDataList!=null && summaryCardNoDetailDataList.size()>0) {
                List<SummaryCardNoDetailData> retSummaryCardNoDetailData = msPrimeClient.summaryCardNoDetailDataList(summaryCardNoDetailDataList).getData();
                for (SummaryCardNoDetailData summaryCardNoDetailData : retSummaryCardNoDetailData) {
                    if (CardElectronicEnum.ONLINE_SELL.getValue().equals(summaryCardNoDetailData.getCardType())) {
                        Long oneDetailFaceValueAmount = summaryCardNoDetailData.getFaceValue() * summaryCardNoDetailData.getCardOrderDetails().getQuantity().intValue();
                        onlineSellFaceValueAmount = onlineSellFaceValueAmount + oneDetailFaceValueAmount;
                    } else if (CardElectronicEnum.OFFLINE_SELL.getValue().equals(summaryCardNoDetailData.getCardType())) {
                        Long oneDetailFaceValueAmount = summaryCardNoDetailData.getFaceValue() * summaryCardNoDetailData.getCardOrderDetails().getQuantity().intValue();
                        offlineSellFaceValueAmount = offlineSellFaceValueAmount + oneDetailFaceValueAmount;
                    }
                }
            }
        }

        //处理实体卡激活数据
        long physicalAmount  = msPrimeClient.summaryPhyCard(startTime,endTime).getData();

        // 处理赠送卡数据
        long offlineFaceValueAmount  = msPrimeClient.summaryDataFreeCardFaceValueAmount(startTime, endTime).getData();

        long totalAmount = 0L;
        long totalConsumeAmount = 0L;
        long totalRemAmount = 0L;
        for (SummaryData summaryData : data) {
            if (CardElectronicEnum.ONLINE_SELL.getDesc().equals(summaryData.getCardType())){

                summaryData.setCardTotalAmount(onlineSellFaceValueAmount);
                summaryData.setConsumeAmount(onlineSellConsumeAmount);
                summaryData.setRefundAmount(onlineSellRefundAmount);

            }else if (CardElectronicEnum.OFFLINE_SELL.getDesc().equals(summaryData.getCardType())){

                summaryData.setCardTotalAmount(offlineSellFaceValueAmount);
                summaryData.setConsumeAmount(offlineSellConsumeAmount);
                summaryData.setRefundAmount(offlineSellRefundAmount);

            }else if (CardElectronicEnum.OFFLINE.getDesc().equals(summaryData.getCardType())){

                summaryData.setCardTotalAmount(offlineFaceValueAmount);
                summaryData.setConsumeAmount(offlineConsumeAmount);
                summaryData.setRefundAmount(offlineRefundAmount);

            } else if (CardElectronicEnum.PHYSICAL.getDesc().equals(summaryData.getCardType())){

                summaryData.setCardTotalAmount(physicalAmount);
                summaryData.setConsumeAmount(physicalConsumeAmount);
                summaryData.setRefundAmount(physicalRefundAmount);

            } else if (CardElectronicEnum.PASSWORD_CARD.getDesc().equals(summaryData.getCardType())){

                Long passwordCardFaceValue = msPrimeClient.summaryPasswordCardTimeScope(summaryData.getCardType(), startTime, endTime).getData();
                summaryData.setCardTotalAmount(passwordCardFaceValue);
                summaryData.setConsumeAmount(passwordCardConsumeAmount);
                summaryData.setRefundAmount(passwordRefundAmount);

            } else if (CardElectronicEnum.REBATE_PASSWORD_CARD.getDesc().equals(summaryData.getCardType())){
                Long rebatePasswordCardFaceValue = msPrimeClient.summaryPasswordCardTimeScope(summaryData.getCardType(), startTime, endTime).getData();
                summaryData.setCardTotalAmount(rebatePasswordCardFaceValue);
                summaryData.setConsumeAmount(rebatePasswordCardConsumeAmount);
                summaryData.setRefundAmount(rebatePasswordRefundAmount);

            }
            totalAmount = totalAmount + summaryData.getCardTotalAmount();
            totalConsumeAmount = totalConsumeAmount + summaryData.getConsumeAmount();
            totalRemAmount = totalRemAmount + summaryData.getRemainingAmount();
        }

        SummaryData totalSummaryData = new SummaryData();
        totalSummaryData.setCardType("合计数");
        totalSummaryData.setCardTotalAmount(totalAmount);
        totalSummaryData.setConsumeAmount(totalConsumeAmount);
        totalSummaryData.setRemainingAmount(totalRemAmount);
        totalSummaryData.setRefundAmount(totalRefundAmount);
        data.add(totalSummaryData);
        return data;
    }



    public List<ChannelSummaryData> channelSummaryNoTimeScope(String channelMerId,List<SummaryData> data) {
        long totalAmount = 0L;
        long totalConsumeAmount = 0L;
        long totalRemAmount = 0L;
        long totalRefundAmount = 0L;

        long offlineSellRefundAmount = 0L;

        List<SummaryCardNoRefundData> summaryCardNoRefundDataList = new ArrayList<>();
        List<CardRefundOrder> cardRefundOrders = msPrimeClient.allListForMerId(channelMerId).getData();
        if (cardRefundOrders!=null && cardRefundOrders.size()>0) {
            for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
                if (cardRefundOrder.getCardId().contains("ELECARD")) {
                    SummaryCardNoRefundData summaryCardNoRefundData = new SummaryCardNoRefundData();
                    summaryCardNoRefundData.setCardRefundOrder(cardRefundOrder);
                    summaryCardNoRefundData.setCardNo(cardRefundOrder.getCardId());
                    summaryCardNoRefundDataList.add(summaryCardNoRefundData);
                }
                totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
            }
            if (summaryCardNoRefundDataList!=null && summaryCardNoRefundDataList.size()>0) {
                List<SummaryCardNoRefundData> retSummaryCardNoRefundData = msPrimeClient.summaryRefundCardNoDataMerId(channelMerId,summaryCardNoRefundDataList).getData();
                for (SummaryCardNoRefundData retSummaryCardNoRefundDatum : retSummaryCardNoRefundData) {
                    if (CardElectronicEnum.OFFLINE_SELL.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        offlineSellRefundAmount = offlineSellRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }
                }
            }
        }

        for (SummaryData summaryData : data) {
           if (CardElectronicEnum.OFFLINE_SELL.getDesc().equals(summaryData.getCardType())){
                summaryData.setConsumeAmount(summaryData.getConsumeAmount() + offlineSellRefundAmount);
                totalAmount = totalAmount + summaryData.getCardTotalAmount();
                totalConsumeAmount = totalConsumeAmount + summaryData.getConsumeAmount();
                totalRemAmount = totalRemAmount + summaryData.getRemainingAmount();
                summaryData.setRefundAmount(offlineSellRefundAmount);
            }
        }
        SummaryData totalSummaryData = new SummaryData();
        totalSummaryData.setCardType("合计数");
        totalSummaryData.setCardTotalAmount(totalAmount);
        totalSummaryData.setConsumeAmount(totalConsumeAmount);
        totalSummaryData.setRemainingAmount(totalRemAmount);
        totalSummaryData.setRefundAmount(totalRefundAmount);
        data.add(totalSummaryData);

        return getDataToChannelSummaryData(channelMerId,data);
    }


    public List<ChannelSummaryData> getDataToChannelSummaryData(String channelMerId,List<SummaryData> data){
        List<ChannelSummaryData> resultData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            SummaryData summaryData = data.get(i);
            ChannelSummaryData channelSummaryData = new ChannelSummaryData();
            BeanUtils.copyProperties(summaryData,channelSummaryData);
            channelSummaryData.setChannelId(channelMerId);
            channelSummaryData.setChannelName("THSZ".equals(channelMerId)?"通华":channelMerId);
            resultData.add(channelSummaryData);
        }
        return resultData;
    }


    public List<ChannelSummaryData> channelSummaryTimeScope(String channelMerId,List<SummaryData> data, String startTime, String endTime) {
        //统计退款金额
        List<CardRefundOrder> cardRefundOrders = msPrimeClient.adminRefundOrderListNoPage("","","","",startTime,endTime,channelMerId).getData();
        long offlineSellRefundAmount = 0L;
        long totalRefundAmount = 0L;

        if (cardRefundOrders!=null && cardRefundOrders.size()>0) {
            List<SummaryCardNoRefundData> summaryCardNoRefundDataList = new ArrayList<>();
            for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
                if (cardRefundOrder.getCardId().contains("ELECARD")) {
                    SummaryCardNoRefundData summaryCardNoRefundData = new SummaryCardNoRefundData();
                    summaryCardNoRefundData.setCardRefundOrder(cardRefundOrder);
                    summaryCardNoRefundData.setCardNo(cardRefundOrder.getCardId());
                    summaryCardNoRefundDataList.add(summaryCardNoRefundData);
                }
                totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
            }
            if (summaryCardNoRefundDataList!=null && summaryCardNoRefundDataList.size()>0) {
                List<SummaryCardNoRefundData> retSummaryCardNoRefundData = msPrimeClient.summaryRefundCardNoDataMerId(channelMerId,summaryCardNoRefundDataList).getData();
                for (SummaryCardNoRefundData retSummaryCardNoRefundDatum : retSummaryCardNoRefundData) {

                    if (CardElectronicEnum.OFFLINE_SELL.getValue().equals(retSummaryCardNoRefundDatum.getCardType())) {
                        offlineSellRefundAmount = offlineSellRefundAmount + retSummaryCardNoRefundDatum.getCardRefundOrder().getAmount();
                    }

                }
            }
        }

        //核销金额
        List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceClientService.querySummaryDataForMerchantCode(startTime, endTime,channelMerId).getData();
        long offlineSellConsumeAmount = 0L;

        if (cardOrderPayTraces!=null && cardOrderPayTraces.size()>0) {
            List<SummaryCardNoData> summaryCardNoDataList = new ArrayList<>();

            for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
                String type = cardOrderPayTrace.getType();
                 if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(type)) {
                    SummaryCardNoData summaryCardNoData = new SummaryCardNoData();
                    summaryCardNoData.setCardOrderPayTrace(cardOrderPayTrace);
                    summaryCardNoData.setCardNo(cardOrderPayTrace.getSourceId());
                    summaryCardNoDataList.add(summaryCardNoData);
                }
            }
            if (summaryCardNoDataList!=null && summaryCardNoDataList.size()>0) {
                List<SummaryCardNoData> retSummaryCardNoDataList = msPrimeClient.summaryCardNoTypeChannel(channelMerId,summaryCardNoDataList).getData();
                for (SummaryCardNoData summaryCardNoData : retSummaryCardNoDataList) {
                  if (CardElectronicEnum.OFFLINE_SELL.getValue().equals(summaryCardNoData.getCardType())) {
                        offlineSellConsumeAmount = offlineSellConsumeAmount + summaryCardNoData.getCardOrderPayTrace().getAmount();
                    }
                }
            }
        }


        List<CardOrderDetails> cardOrderDetailsList = orderClientService.querySummaryDetailsForMerchantCode(channelMerId,startTime, endTime).getData();
        long offlineSellFaceValueAmount = 0L;

        if (cardOrderDetailsList!=null && cardOrderDetailsList.size()>0) {
            List<SummaryCardNoDetailData> summaryCardNoDetailDataList = new ArrayList<>();
            for (CardOrderDetails cardOrderDetails : cardOrderDetailsList) {
                SummaryCardNoDetailData summaryCardNoDetailData = new SummaryCardNoDetailData();
                summaryCardNoDetailData.setCardOrderDetails(cardOrderDetails);
                summaryCardNoDetailData.setBatchCode(cardOrderDetails.getBatchCode());
                summaryCardNoDetailDataList.add(summaryCardNoDetailData);
            }
            if (summaryCardNoDetailDataList!=null && summaryCardNoDetailDataList.size()>0) {
                List<SummaryCardNoDetailData> retSummaryCardNoDetailData = msPrimeClient.summaryCardNoDetailDataChannel(channelMerId,summaryCardNoDetailDataList).getData();
                for (SummaryCardNoDetailData summaryCardNoDetailData : retSummaryCardNoDetailData) {
                     if (CardElectronicEnum.OFFLINE_SELL.getValue().equals(summaryCardNoDetailData.getCardType())) {
                        Long oneDetailFaceValueAmount = summaryCardNoDetailData.getFaceValue() * summaryCardNoDetailData.getCardOrderDetails().getQuantity().intValue();
                        offlineSellFaceValueAmount = offlineSellFaceValueAmount + oneDetailFaceValueAmount;
                    }
                }
            }
        }

        long totalAmount = 0L;
        long totalConsumeAmount = 0L;
        long totalRemAmount = 0L;
        for (SummaryData summaryData : data) {
            if (CardElectronicEnum.OFFLINE_SELL.getDesc().equals(summaryData.getCardType())){
                summaryData.setCardTotalAmount(offlineSellFaceValueAmount);
                summaryData.setConsumeAmount(offlineSellConsumeAmount);
                summaryData.setRefundAmount(offlineSellRefundAmount);
            }

            totalAmount = totalAmount + summaryData.getCardTotalAmount();
            totalConsumeAmount = totalConsumeAmount + summaryData.getConsumeAmount();
            totalRemAmount = totalRemAmount + summaryData.getRemainingAmount();
        }

        SummaryData totalSummaryData = new SummaryData();
        totalSummaryData.setCardType("合计数");
        totalSummaryData.setCardTotalAmount(totalAmount);
        totalSummaryData.setConsumeAmount(totalConsumeAmount);
        totalSummaryData.setRemainingAmount(totalRemAmount);
        totalSummaryData.setRefundAmount(totalRefundAmount);
        data.add(totalSummaryData);

        return getDataToChannelSummaryData(channelMerId,data);
    }







}
