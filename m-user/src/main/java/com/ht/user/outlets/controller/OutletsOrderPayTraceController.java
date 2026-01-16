package com.ht.user.outlets.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.CardOrderPayTrace;
import com.ht.user.config.CardOrdersStateConfig;
import com.ht.user.outlets.allinpay.SybPayService;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.excel.OutletsOrderPayTraceExcelVO;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.paystrategy.PayCompanyStrategy;
import com.ht.user.outlets.paystrategy.PayCompanyStrategyFactory;
import com.ht.user.outlets.paystrategy.PayCompanyTypeEnum;
import com.ht.user.outlets.service.IOutletsOrderPayTraceService;
import com.ht.user.outlets.service.IOutletsOrderRefTraceService;
import com.ht.user.outlets.service.IOutletsOrdersService;
import com.ht.user.outlets.util.CheckPayTimeUtil;
import com.ht.user.outlets.util.DESUtil;
import com.ht.user.outlets.util.DateStrUtil;
import com.ht.user.outlets.util.StringGeneralUtil;
import com.ht.user.outlets.vo.*;
import com.ht.user.result.ResultTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 订单支付流水 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@RestController
@RequestMapping("/tonglian/outlets/orderPayTrace")
public class OutletsOrderPayTraceController {

    private Logger logger = LoggerFactory.getLogger(OutletsOrdersController.class);

    @Autowired
    private DESUtil desUtil;

    @Autowired
    private IOutletsOrderPayTraceService outletsOrderPayTraceService;

    @Autowired
    private IOutletsOrderRefTraceService iOutletsOrderRefTraceService;

    @Autowired
    private IOutletsOrdersService outletsOrdersService;

    @Autowired
    private SybPayService sybPayService;


    /**
     * 奥特莱斯 根据订单号查询支付流水明细
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/queryPayTrace")
    public List<OutletsOrderPayTrace> queryPayTrace(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        try {
            logger.info("上送的流水查询数据为:" + desDataStr.getDesDataStr());
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            logger.info("上送的流水查询解密字符串数据为:" + decryptDataStr);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        SearchTraceData searchTraceData = JSONObject.parseObject(decryptDataStr, SearchTraceData.class);
        logger.info("上送的流水查询数据为:" + searchTraceData);
        if (searchTraceData == null) {
            throw new CheckException(ResultTypeEnum.SEARCH_DATA_NULL_ERROR);
        }

        if (!StringGeneralUtil.checkNotNull(searchTraceData.getOrderCode())) {
            logger.info("上送了空订单号查询:" + searchTraceData);
            throw new CheckException(ResultTypeEnum.ORDER_NULL);
        }

        //查询订单流水数据
        List<OutletsOrderPayTrace> outletsOrderPayTraces = outletsOrderPayTraceService.queryPayTrace(searchTraceData);

        //判断支付
        if (outletsOrderPayTraces != null && outletsOrderPayTraces.size() > 0) {
            OutletsOrderPayTrace outletsOrderPayTraceOne = outletsOrderPayTraces.get(0);
            OutletsOrderRefTrace outletsOrderRefTrace = iOutletsOrderRefTraceService.queryByReqsn(outletsOrderPayTraceOne.getOrderCode());
            if (outletsOrderRefTrace == null) {

                //mis订单反查
                if (CardOrdersStateConfig.UNPAID.equals(outletsOrderPayTraceOne.getState())
                        || CardOrdersStateConfig.CLOSE.equals(outletsOrderPayTraceOne.getState())) {
                    PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(StringGeneralUtil.checkNotNull(outletsOrderPayTraceOne.getChannelApi()) ? outletsOrderPayTraceOne.getChannelApi() : PayCompanyTypeEnum.ALLINPAY.getPayCompany());
                    Map<String, String> queryOrderMap = payCompanyStrategy.posOrderPayQuery(outletsOrderPayTraceOne.getRefBatchCode(), "", DateStrUtil.dateStrMMdd(outletsOrderPayTraceOne.getCreateAt()));
//                    Map<String, String> queryOrderMap = sybPayService.posOrderPayQuery(outletsOrderPayTraceOne.getRefBatchCode(), "", DateStrUtil.dateStrMMdd(outletsOrderPayTraceOne.getCreateAt()));
                    logger.info(searchTraceData.getOrderCode() + "反查支付平台订单状态数据:" + queryOrderMap);
                    if ("SUCCESS".equals(queryOrderMap.get("retcode")) && "0000".equals(queryOrderMap.get("trxstatus"))) {
                        //转化map为实体
                        PosQueryRefOrderData posQueryRefOrderData = new PosQueryRefOrderData();
                        BeanMap beanMap = BeanMap.create(posQueryRefOrderData);
                        beanMap.putAll(queryOrderMap);
                        //更新订单数据
                        PosPayTraceSuccessData posPayTraceSuccessData = new PosPayTraceSuccessData();
                        posPayTraceSuccessData.setAmount(Integer.parseInt(posQueryRefOrderData.getAmount()));
                        posPayTraceSuccessData.setFee(StringGeneralUtil.checkNotNull(posQueryRefOrderData.getFee()) ? Integer.parseInt(posQueryRefOrderData.getFee()) : 0);
                        posPayTraceSuccessData.setTraceNo(posQueryRefOrderData.getTraceno());
                        posPayTraceSuccessData.setBatchNo(posQueryRefOrderData.getTermbatchid());
                        posPayTraceSuccessData.setMerchId(posQueryRefOrderData.getCusid());
                        posPayTraceSuccessData.setMerchName("佰申商业管理（海南）有限公司");
                        posPayTraceSuccessData.setTerId(posQueryRefOrderData.getTermno());
                        posPayTraceSuccessData.setRefNo(posQueryRefOrderData.getTermrefnum());
                        posPayTraceSuccessData.setAuthNo(posQueryRefOrderData.getTermauthno());
                        posPayTraceSuccessData.setRejCode("00");
                        Date finDate = DateStrUtil.StrToDateyyyyMMddHHmmss(posQueryRefOrderData.getFintime());
                        posPayTraceSuccessData.setDate(DateStrUtil.dateStrMMdd(finDate));
                        posPayTraceSuccessData.setTime(DateStrUtil.dateStrHHmmss(finDate));
                        posPayTraceSuccessData.setRejCodeCn("反查订单交易成功");
                        posPayTraceSuccessData.setTransTicketNo(posQueryRefOrderData.getTrxid());
                        posPayTraceSuccessData.setOrderCode(outletsOrderPayTraceOne.getOrderCode());
                        posPayTraceSuccessData.setChannelApi("AllinPay");
                        posPayTraceSuccessData.setPayTime(posQueryRefOrderData.getFintime());
                        outletsOrderPayTraceService.updateMisOrderState(posPayTraceSuccessData);

                        List<OutletsOrderPayTrace> outletsOrderPayTracesRet = outletsOrderPayTraceService.queryPayTrace(searchTraceData);
                        for (OutletsOrderPayTrace outletsOrderPayTrace : outletsOrderPayTracesRet) {
                            outletsOrderPayTrace.setSourceCode(posQueryRefOrderData.getTrxcode());

                            //转化字段
                            String trxid = outletsOrderPayTrace.getPayCode();
                            outletsOrderPayTrace.setTraceNo(trxid);
                            outletsOrderPayTrace.setRefTraceNo(trxid);
                        }
                        logger.info(searchTraceData.getOrderCode()+"查询接口响应数据:" + outletsOrderPayTracesRet);
                        return outletsOrderPayTracesRet;
                    }
                }
                for (OutletsOrderPayTrace outletsOrderPayTrace : outletsOrderPayTraces) {
                    outletsOrderPayTrace.setSourceCode("VSP001");

                    //转化字段
                    String trxid = outletsOrderPayTrace.getPayCode();
                    outletsOrderPayTrace.setTraceNo(trxid);
                    outletsOrderPayTrace.setRefTraceNo(trxid);
                }
                logger.info(searchTraceData.getOrderCode()+"查询接口响应数据:" + outletsOrderPayTraces);
                return outletsOrderPayTraces;
            } else {
                if ("SUCCESS".equals(outletsOrderRefTrace.getRetcode()) && "2000".equals(outletsOrderRefTrace.getTrxstatus())) {
                    iOutletsOrderRefTraceService.querySybOrderUpDateLocal(outletsOrderRefTrace);
                    List<OutletsOrderPayTrace> outletsOrderPayTracesRet = outletsOrderPayTraceService.queryPayTrace(searchTraceData);
                    for (OutletsOrderPayTrace outletsOrderPayTrace : outletsOrderPayTracesRet) {
                        outletsOrderPayTrace.setSourceCode(outletsOrderRefTrace.getTrxcode());

                        //转化字段
                        String trxid = outletsOrderPayTrace.getRefTraceNo();
                        outletsOrderPayTrace.setTraceNo(trxid);
//                        outletsOrderPayTrace.setRefTraceNo("");
                    }
                    logger.info(searchTraceData.getOrderCode()+"查询接口响应数据:" + outletsOrderPayTraces);
                    return outletsOrderPayTracesRet;
                } else {
                    for (OutletsOrderPayTrace outletsOrderPayTrace : outletsOrderPayTraces) {
                        outletsOrderPayTrace.setSourceCode(outletsOrderRefTrace.getTrxcode());

                        //转化字段
                        String trxid = outletsOrderPayTrace.getRefTraceNo();
                        outletsOrderPayTrace.setTraceNo(trxid);
//                        outletsOrderPayTrace.setRefTraceNo("");
                    }
                    logger.info(searchTraceData.getOrderCode()+"查询接口响应数据:" + outletsOrderPayTraces);
                    return outletsOrderPayTraces;
                }
            }
        } else {
            throw new CheckException(ResultTypeEnum.ORDER_NULL);
//            return outletsOrderPayTraces;
        }
    }

    /**
     * 奥特莱斯 根据订单号查询支付流水列表
     *
     * @param queryOutletsOrderPayTraceVO
     * @return
     */
    @PostMapping("/list/all")
    public List<OutletsOrderPayTraceVO> findPage(@RequestBody QueryOutletsOrderPayTraceVO queryOutletsOrderPayTraceVO) throws Exception {
        String orderCode = queryOutletsOrderPayTraceVO.getOrderCode();

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("orderCode", orderCode);

        List<OutletsOrderPayTraceVO> result = outletsOrderPayTraceService.findlist(paramsMap);
        return result;
    }

    /**
     * 奥特莱斯 根据订单号查询可退款金额
     *
     * @param queryOutletsOrderPayTraceVO
     * @return
     */
    @PostMapping("/find/refundableAmount")
    public Map<String, Object> findRefundableAmount(@RequestBody QueryOutletsOrderPayTraceVO queryOutletsOrderPayTraceVO) throws Exception {
        String orderCode = queryOutletsOrderPayTraceVO.getOrderCode();

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("orderCode", orderCode);

        Map<String, Object> result = outletsOrderPayTraceService.findRefundableAmount(paramsMap);
        return result;
    }

    /**
     * 奥特莱斯 根据支付类型统计金额
     *
     * @param queryCountSumVO
     * @return
     */
    @PostMapping("/list/countSum")
    public List<Map<String, Object>> countSum(@RequestBody QueryCountSumVO queryCountSumVO) throws Exception {
        String startCreateAt = queryCountSumVO.getStartCreateAt();
        String endCreateAt = queryCountSumVO.getEndCreateAt();

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("startCreateAt", startCreateAt);
        paramsMap.put("endCreateAt", endCreateAt);

        List<Map<String, Object>> result = outletsOrderPayTraceService.countSum(paramsMap);
        return result;
    }

    /**
     * 奥特莱斯 根据商家id分组统计金额
     *
     * @param queryCountSumVO
     * @return
     */
    @PostMapping("/list/countMerchantAmount")
    public List<Map<String, Object>> countMerchantAmount(@RequestBody QueryCountSumVO queryCountSumVO) throws Exception {
        String startCreateAt = queryCountSumVO.getStartCreateAt();
        String endCreateAt = queryCountSumVO.getEndCreateAt();

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("startCreateAt", startCreateAt);
        paramsMap.put("endCreateAt", endCreateAt);

        List<Map<String, Object>> result = outletsOrderPayTraceService.countAmountSumGroupByMerchId(paramsMap);
        return result;
    }

    /**
     * 奥特莱斯 根据cashId统计支付和退款金额
     *
     * @param queryCountCashIdAmountSumVO
     * @return
     */
    @PostMapping("/list/countCashIdAmount")
    public List<Map<String, Object>> countCashIdAmount(@RequestBody QueryCountCashIdAmountSumVO queryCountCashIdAmountSumVO) throws Exception {
        String cashId = queryCountCashIdAmountSumVO.getCashId();
        String startCreateAt = queryCountCashIdAmountSumVO.getStartTime();
        String endCreateAt = queryCountCashIdAmountSumVO.getEndTime();

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("cashId", cashId);
        paramsMap.put("startCreateAt", startCreateAt);
        paramsMap.put("endCreateAt", endCreateAt);

        List<Map<String, Object>> result = outletsOrderPayTraceService.countAmountSumGroupByType(paramsMap);
        return result;
    }

    /**
     * 导出订单支付流水excel
     *
     * @param response
     * @param startCreateAt
     * @param endCreateAt
     */
    @GetMapping("/download")
    public void downloadExcel(
            HttpServletResponse response,
            @RequestParam(value = "startCreateAt", required = false) String startCreateAt,
            @RequestParam(value = "endCreateAt", required = false) String endCreateAt
    ) {

        if (StringUtils.isEmpty(startCreateAt)) {
            startCreateAt = DateStrUtil.nowDateStrYearMoonDay();
        }
        if (StringUtils.isEmpty(endCreateAt)) {
            endCreateAt = DateStrUtil.nowDateStrYearMoonDay();
        }
        endCreateAt = endCreateAt + " 23:59:59";

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("startCreateAt", startCreateAt);
        paramsMap.put("endCreateAt", endCreateAt);
        paramsMap.put("state", "excel");

        List<OutletsOrderPayTraceVO> result = outletsOrderPayTraceService.findlist(paramsMap);

        List<OutletsOrderPayTraceExcelVO> list = outletsOrderPayTraceService.packageOutletsOrderPayTrace(result);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName = new String(("订单支付流水导出" + date + ".xlsx").getBytes("gb2312"), "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            EasyExcel.write(response.getOutputStream(), OutletsOrderPayTraceExcelVO.class).sheet("Sheet1").doWrite(list);
        } catch (Exception e) {
            System.out.println("告警信息下载失败" + e.getMessage());
        }
    }

    /**
     * 导出对应商户的订单支付流水excel
     *
     * @param response
     * @param cashId
     * @param startTime
     * @param endTime
     */
    @GetMapping("/merchant/download")
    public void downloadMerchantExcel(
            HttpServletResponse response,
            @RequestParam(value = "cashId", required = false) String cashId,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime
    ) {

        if (!StringGeneralUtil.checkNotNull(startTime)) {
            startTime = DateStrUtil.nowDateStrYearMoonDay();
        }
        if (!StringGeneralUtil.checkNotNull(endTime)) {
            endTime = DateStrUtil.nowDateStrYearMoonDay();
        }
        endTime = endTime + " 23:59:59";

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);
        paramsMap.put("cashId", cashId);
        paramsMap.put("state", "excel");

        List<OutletsOrderPayTraceVO> result = outletsOrderPayTraceService.findlist(paramsMap);

        List<OutletsOrderPayTraceExcelVO> list = outletsOrderPayTraceService.packageOutletsOrderPayTrace(result);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName = new String(("订单支付流水导出" + date + ".xlsx").getBytes("gb2312"), "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            EasyExcel.write(response.getOutputStream(), OutletsOrderPayTraceExcelVO.class).sheet("Sheet1").doWrite(list);
        } catch (Exception e) {
            System.out.println("告警信息下载失败" + e.getMessage());
        }
    }

    /**
     * 奥特莱斯 统计月销售额
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/list/countMonthAmount")
    public List<Map<String, Object>> countMonthAmount() throws Exception {

        logger.info("countMonthAmount");
        List<Map<String, Object>> result = outletsOrderPayTraceService.countLastThreeMonthsAmount();
        return result;

    }

    /**
     * 奥特莱斯 统计日销售额
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/list/countDayAmount")
    public List<Map<String, Object>> countDayAmount() throws Exception {

        logger.info("countDayAmount");
        List<Map<String, Object>> result = outletsOrderPayTraceService.countLastSevenDaysAmount();
        return result;

    }

    /**
     * pos 查询流水
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/posQueryTrace")
    public Page<OutletsOrderPayTrace> posQueryTrace(@RequestBody PosSearchTraceData posSearchTraceData) throws Exception {
        Page<OutletsOrderPayTrace> outletsOrderPayTracePage = outletsOrderPayTraceService.posQueryTrace(posSearchTraceData);
        List<OutletsOrderPayTrace> records = outletsOrderPayTracePage.getRecords();
        for (OutletsOrderPayTrace record : records) {

            String refBatchCode = record.getRefBatchCode();
            String orderCode = record.getOrderCode();
            String payCode = record.getPayCode();

            record.setOrderCode(refBatchCode);
            record.setPayCode(orderCode);
            record.setRefBatchCode(payCode);

            record.setIsShowRefund("false");
        }
        return outletsOrderPayTracePage;
    }


    /**
     * 账单 流水查询
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/checkPayTrace")
    public OutletsOrderRefTrace checkPayTrace(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        try {
            logger.info("上送的流水查询数据为:" + desDataStr.getDesDataStr());
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            logger.info("上送的流水查询解密字符串数据为:" + decryptDataStr);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        CheckPayTraceData checkPayTraceData = JSONObject.parseObject(decryptDataStr, CheckPayTraceData.class);
        if (!StringGeneralUtil.checkNotNull(checkPayTraceData.getTrxid())) {
            throw new CheckException(ResultTypeEnum.CHECK_TRACE_NO_NULL);
        }
        return outletsOrderPayTraceService.queryByRefTraceNoOrPayCode(checkPayTraceData);
    }


}

