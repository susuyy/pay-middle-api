package com.ht.feignapi.prime.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.prime.cardenum.CardElectronicEnum;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.prime.excel.ActualCardImportVo;
import com.ht.feignapi.prime.excel.CreateCardOrderListener;
import com.ht.feignapi.prime.excel.SummaryExcelData;
import com.ht.feignapi.prime.service.PrimeCardService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderPayTraceClientService;
import com.ht.feignapi.tonglian.card.entity.CardOrders;
import com.ht.feignapi.tonglian.card.entity.DESDataStr;
import com.ht.feignapi.tonglian.card.entity.OrderCloseData;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceTypeConfig;
import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.util.DESUtil;
import com.ht.feignapi.util.DateStrUtil;
import com.ht.feignapi.util.MoneyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author: zheng weiguang
 * @Date: 2021/2/7 11:51
 */
@RestController
@RequestMapping("/ms/card")
@CrossOrigin(allowCredentials = "true")
public class PrimeCardController {


    private Logger logger = LoggerFactory.getLogger(PrimeUserConsumerController.class);

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;

    @Autowired
    private PrimeCardService primeCardService;

    @Autowired
    private DESUtil desUtil;

    //管理员操作接口控制
    private static Map<String,String> sendCardMap = new HashMap<>();

    /**
     * 后台 售卡 发卡 绑定接口 (线下绑卡 根据收款类型生成订单)
     *
     * @param cardActualMapUser
     * @return
     */
    @PostMapping("/adminSetUserCard")
    public Object adminSetUserCard(CardActualMapUser cardActualMapUser) {
        Result<CardElectronic> cardElectronicResult = msPrimeClient.adminSetUserCard(cardActualMapUser);
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(cardElectronicResult.getCode())){
//            if (!StringUtils.isEmpty(cardActualMapUser.getPayType()) && !"free".equals(cardActualMapUser.getPayType())){
            CardElectronic cardElectronic = cardElectronicResult.getData();
            String payAmount = cardActualMapUser.getPayAmount();
            if (StringUtils.isEmpty(payAmount)){
                payAmount="0";
            }
            orderClientService.createAdminSetUserCardOrder(cardElectronic,cardActualMapUser.getPayType(),payAmount);
//            }
        }
        return cardElectronicResult;
    }


    /**
     * 机构渠道 后台售卡 发卡 绑定接口 (线下绑卡 根据收款类型生成订单)
     *
     * @param partyCardReceiveUserVo
     * @return
     */
    @PostMapping("/adminChannelSetUserCard")
    public Object adminChannelSetUserCard(@RequestBody PartyCardReceiveUserVo partyCardReceiveUserVo) {
        Result<CardElectronic> cardElectronicResult = msPrimeClient.adminChannelSetUserCard(partyCardReceiveUserVo);
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(cardElectronicResult.getCode())){
            CardElectronic cardElectronic = cardElectronicResult.getData();
            String payAmount = partyCardReceiveUserVo.getPayAmount();
            if (StringUtils.isEmpty(payAmount)){
                payAmount="0";
            }
            orderClientService.createAdminSetUserCardOrder(cardElectronic,partyCardReceiveUserVo.getPayType(),payAmount);
        }
        return cardElectronicResult;
    }

    /**
     * excel导入订单
     * @param file
     * @param operatorId
     * @param operatorAccount
     * @return
     */
    @PostMapping("/adminExcelImportCard/{operatorId}/{operatorAccount}/{payType}/{payAmount}")
    public void adminExcelImportCard(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "certFile",required = false)MultipartFile certFile,
            @PathVariable("operatorId")String operatorId,
            @PathVariable("operatorAccount")String operatorAccount,
            @PathVariable("payType")String payType,
            @PathVariable("payAmount")String payAmount) throws Exception {
//        Result result = msPrimeClient.adminExcelImportCard(file, certFile, operatorId, operatorAccount);
//        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(result.getCode())) {
        logger.info(DateStrUtil.nowDateStr()+"批量发卡,金额(分):"+payAmount+",收款类型:"+payType+",操作人:"+operatorAccount);
//            if (!"free".equals(payType)) {
                ExcelReader excelReader = null;
                try {
                    excelReader = EasyExcel.read(file.getInputStream(), ActualCardImportVo.class, new CreateCardOrderListener(payType, payAmount, orderClientService, msPrimeClient)).build();
                    ReadSheet readSheet = EasyExcel.readSheet(0).build();
                    excelReader.read(readSheet);
                } finally {
                    if (excelReader != null) {
                        // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                        excelReader.finish();
                    }
                }
//            }
//        }
//        return result;
    }


    /**
     * 后天管理员用户批量发放电子卡
     * @param adminBatchBindCardData
     * @return
     */
    @PostMapping("/adminBatchBindCard")
    public Result adminBatchBindCard(@RequestBody AdminBatchBindCardData adminBatchBindCardData) throws Exception {
        //操作限定
        String useOperatorAccount = sendCardMap.get(adminBatchBindCardData.getBatchCode());
        if (!StringUtils.isEmpty(useOperatorAccount)){
            return new Result(ResultTypeEnum.BATCH_PENDING.getCode(),useOperatorAccount+"正在操作发卡,"+ResultTypeEnum.BATCH_PENDING.getMessage());
        }

        sendCardMap.put(adminBatchBindCardData.getBatchCode(),adminBatchBindCardData.getOperatorAccount());

        logger.info(DateStrUtil.nowDateStr()+"批量发卡,金额(分):"+adminBatchBindCardData.getPayAmount()+",收款类型:"+adminBatchBindCardData.getPayType()+",操作人:"+adminBatchBindCardData.getOperatorAccount());
        Result<AdminBatchBindCardRet> result = msPrimeClient.adminBatchBindCard(adminBatchBindCardData);
        if (result.getCode()!=1200){
            return result;
        }
        AdminBatchBindCardRet adminBatchBindCardRet = result.getData();
        List<CardElectronic> cardElectronicList = adminBatchBindCardRet.getCardElectronicList();
        Map<String, CardElectronic> cardElectronicMap = new HashMap<>();
        for (CardElectronic cardElectronic : cardElectronicList) {
            cardElectronicMap.put(cardElectronic.getCardNo(),cardElectronic);
        }

        orderClientService.createAdminSetUserCardOrderBatch(cardElectronicMap,adminBatchBindCardData.getPayType());

        sendCardMap.remove(adminBatchBindCardData.getBatchCode());

        return new Result(ResultTypeEnum.SERVICE_SUCCESS);
    }


    /**
     * 获取退卡列表
     * @param orderCode
     * @return
     */
    @GetMapping("/getCanRefundCardList")
    public List<RefundCardMsgDetail> getCanRefundCardList(@RequestParam("orderCode") String orderCode) {
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
        List<RefundCardMsgDetail> list=new ArrayList<>();
        List<CardOrderDetails> cardOrderDetailsList = cardOrdersVO.getCardOrderDetailsList();
        for (CardOrderDetails cardOrderDetails : cardOrderDetailsList) {
            String cardNoJsonStr = cardOrderDetails.getProductionCode();
            if (!StringUtils.isEmpty(cardNoJsonStr)) {
                List cardNoList = JSONObject.parseObject(cardNoJsonStr, List.class);
                for (Object cardNo : cardNoList) {
                    RefundCardMsgDetail refundCardMsgDetail = msPrimeClient.checkCardCanRefund(cardNo + "", orderCode).getData();
                    list.add(refundCardMsgDetail);
                }
            }
        }
        return list;
    }


    /**
     * 卡核销 汇总台 导出excel
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/summaryExcel")
    public void summaryExcel(@RequestParam(value = "startTime",required = false)String startTime,
                             @RequestParam(value = "endTime",required = false)String endTime,
                             HttpServletResponse response) {
        List<SummaryData> summaryDataList = summary(startTime, endTime);
        List<SummaryExcelData> summaryExcelDataList = new ArrayList<>();
        for (SummaryData summaryData : summaryDataList) {
            SummaryExcelData summaryExcelData = new SummaryExcelData();
            summaryExcelData.setCardType(summaryData.getCardType());
            summaryExcelData.setCardTotalAmount(MoneyUtil.changeF2Y(summaryData.getCardTotalAmount()));
            summaryExcelData.setConsumeAmount(MoneyUtil.changeF2Y(summaryData.getConsumeAmount()));
            summaryExcelData.setRefundAmount(MoneyUtil.changeF2Y(summaryData.getRefundAmount()));
            summaryExcelData.setRemainingAmount(MoneyUtil.changeF2Y(summaryData.getRemainingAmount()));
            summaryExcelDataList.add(summaryExcelData);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName = new String(("导出汇总数据" + date + ".xlsx").getBytes("gb2312"), "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            EasyExcel.write(response.getOutputStream(), SummaryExcelData.class).sheet("Sheet1").doWrite(summaryExcelDataList);
        } catch (Exception e) {
            logger.info("告警信息下载失败" + e.getMessage());
        }
    }

    /**
     * 卡核销 汇总台
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/summary")
    public List<SummaryData> summary(@RequestParam(value = "startTime",required = false)String startTime,
                                     @RequestParam(value = "endTime",required = false)String endTime) {
        logger.info("1========="+DateStrUtil.nowDateStr());
        List<SummaryData> data = msPrimeClient.summary().getData();
        if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime) ){
            List<SummaryData> summaryDataList = primeCardService.summaryNoTimeScope(data);
            logger.info("2========="+DateStrUtil.nowDateStr());
            return summaryDataList;
        }
        startTime = startTime.split(" ")[0]+" 00:00:00";
        endTime = endTime.split(" ")[0]+" 23:59:59";

        List<SummaryData> summaryDataList = primeCardService.summaryTimeScope(data,startTime,endTime);
        logger.info("2========="+DateStrUtil.nowDateStr());
        return summaryDataList;
    }


    /**
     * 机构渠道卡核销 汇总台
     * @param startTime
     * @param endTime
     * @param channelId
     * @return
     */
    @GetMapping("/channel/summary")
    public List<ChannelSummaryData> channelSummary(@RequestParam(value = "startTime")String startTime,
                                     @RequestParam(value = "endTime")String endTime,
                                     @RequestParam(value = "channelId")String channelId) {


        logger.info("11========="+DateStrUtil.nowDateStr());
        List<SummaryData> data = msPrimeClient.channelSummary(channelId).getData();

        if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime) ){
            List<ChannelSummaryData> channelSummaryDataList = primeCardService.channelSummaryNoTimeScope(channelId,data);
            logger.info("21========="+DateStrUtil.nowDateStr());
            return channelSummaryDataList;

        }
        startTime = startTime.split(" ")[0]+" 00:00:00";
        endTime = endTime.split(" ")[0]+" 23:59:59";

        List<ChannelSummaryData> channelSummaryDataList = primeCardService.channelSummaryTimeScope(channelId,data,startTime,endTime);
        logger.info("22========="+DateStrUtil.nowDateStr());

        return channelSummaryDataList;
    }


    /**
     * hailv_v1.1.0
     * 海旅(线上商城 预付费卡) 用户账号余额
     * @param desDataStr
     * @return
     */
    @PostMapping("/user/accountBalance")
    public Result userAccountBalance(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            logger.error("userAccountBalance desDataStr={},error={}",desDataStr,e);
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        CardQueryData cardQueryData = JSONObject.parseObject(decryptDataStr, CardQueryData.class);
        String userPhone = cardQueryData.getUserPhone();
        logger.info("userAccountBalance userPhone={},cardQueryData={}",userPhone,cardQueryData);
        Long amount = msPrimeClient.userShoppingMalAccountBalance(userPhone).getData();
        JSONObject resultJson = new JSONObject();
        resultJson.put("amount",amount);
        String encrypt = desUtil.encrypt(resultJson.toString());
        logger.info("userAccountBalance userPhone={} 响应数据为:{}",userPhone,resultJson);
        logger.info("userAccountBalance userPhone={} 加密响应数据为:{}:",userPhone,encrypt);
        return Result.success(encrypt);
    }

    /**
     * DES解密
     * @param postData
     * @return
     */
    @PostMapping("/des/decrypt")
    public Result desDecrypt(@RequestBody JSONObject postData){
        String data = postData.getString("data");
        String decrypt = desUtil.decrypt(data);
        try{
            JSONObject jsonDecrypt= JSONObject.parseObject(decrypt);
            logger.info("desDecrypt json data={},jsonDecrypt={}",data,jsonDecrypt);
            return Result.success(jsonDecrypt);
        }catch (Exception e){
            JSONArray array = JSONArray.parseArray(decrypt);
            logger.info("desDecrypt array data={},jsonDecrypt={}",data,array);
            return Result.success(array);
        }
    }


    /**
     * DES加密
     * @param postData
     * @return
     */
    @PostMapping("/des/encrypt")
    public Result desEncrypt(@RequestBody JSONObject postData){
        JSONObject dataJson = postData.getJSONObject("data");
        String encrypt = desUtil.encrypt(dataJson.toString());
        logger.info("desDecrypt data={},encrypt={}",dataJson,encrypt);
        return Result.success(encrypt);
    }

}
