package com.ht.user.outlets.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.excel.OutletsOrderPayTraceExcelVO;
import com.ht.user.outlets.excel.OutletsOrderRefundCancelExcelVO;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.service.IOutletsOrderPayTraceService;
import com.ht.user.outlets.service.IOutletsOrderRefundCancelService;
import com.ht.user.outlets.util.DESUtil;
import com.ht.user.outlets.util.DateStrUtil;
import com.ht.user.outlets.util.HelperUtils;
import com.ht.user.outlets.util.StringGeneralUtil;
import com.ht.user.outlets.vo.*;
import com.ht.user.result.ResultTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-11-17
 */
@RestController
@RequestMapping("/tonglian/outlets/orderRefundCancel")
public class OutletsOrderRefundCancelController {

    private Logger logger = LoggerFactory.getLogger(OutletsOrderRefundCancelController.class);

    @Autowired
    private DESUtil desUtil;

    @Autowired
    private IOutletsOrderRefundCancelService outletsOrderRefundCancelService;

    /**
     * 奥特莱斯 根据订单号查询支付流水明细
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/queryRefundCancelData")
    public List<OutletsOrderRefundCancel> queryRefundCancelData(@RequestBody DESDataStr desDataStr) throws Exception {
        String decryptDataStr = null;
        try {
            logger.info("上送的退款查询数据为:"+desDataStr.getDesDataStr());
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            logger.info("上送的退款查询解密字符串数据为:"+decryptDataStr);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        SearchTraceData searchTraceData = JSONObject.parseObject(decryptDataStr, SearchTraceData.class);
        logger.info("上送的退款查询数据为:"+searchTraceData);
        if (searchTraceData==null){
            throw new CheckException(ResultTypeEnum.ORDER_CODE_NULL);
        }

        //查询订单流水数据
        if (!StringGeneralUtil.checkNotNull(searchTraceData.getRefundCancelCode())&&!StringGeneralUtil.checkNotNull(searchTraceData.getOrderCode())){
            throw new CheckException(ResultTypeEnum.SEARCH_DATA_NULL_ERROR);
        }
        return outletsOrderRefundCancelService.queryRefundCancelData(searchTraceData);
    }

    /**
     * 奥特莱斯 退款订单分页列表
     *
     * @param queryOutletsOrderRefundCancelVO
     * @return
     */
    @PostMapping("/list/page")
    public IPage<OutletsOrderRefundCancelVO> findPage(@RequestBody QueryOutletsOrderRefundCancelVO queryOutletsOrderRefundCancelVO) throws Exception {
        Integer pageNo = queryOutletsOrderRefundCancelVO.getPageNo();
        Integer pageSize = queryOutletsOrderRefundCancelVO.getPageSize();
        String refundCancelCode = queryOutletsOrderRefundCancelVO.getRefundCancelCode();
        String oriOrderCode = queryOutletsOrderRefundCancelVO.getOriOrderCode();
        String state = queryOutletsOrderRefundCancelVO.getState();
        String startCreateAt = queryOutletsOrderRefundCancelVO.getStartCreateAt();
        String endCreateAt = queryOutletsOrderRefundCancelVO.getEndCreateAt();

        pageNo = HelperUtils.getDefaultIntValue(pageNo, HelperUtils.PAGE_NO_DEFAULT_VALUE);
        pageSize = HelperUtils.getDefaultIntValue(pageSize, HelperUtils.PAGE_SIZE_DEFAULT_VALUE);

        IPage<OutletsOrderRefundCancel> page = new Page<>(pageNo,pageSize);
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("refundCancelCode", refundCancelCode);
        paramsMap.put("oriOrderCode", oriOrderCode);
        paramsMap.put("state", state);
        paramsMap.put("startCreateAt", startCreateAt);
        paramsMap.put("endCreateAt", endCreateAt);
        IPage<OutletsOrderRefundCancelVO> result = outletsOrderRefundCancelService.findPage(page, paramsMap);
        return result;
    }

    /**
     * 导出退款订单excel
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

        if (StringUtils.isEmpty(startCreateAt)){
            startCreateAt = DateStrUtil.nowDateStrYearMoonDay();
        }
        if (StringUtils.isEmpty(endCreateAt)){
            endCreateAt = DateStrUtil.nowDateStrYearMoonDay();
        }
        endCreateAt = endCreateAt + " 23:59:59";

        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("startCreateAt", startCreateAt);
        paramsMap.put("endCreateAt", endCreateAt);

        List<OutletsOrderRefundCancelVO> result = outletsOrderRefundCancelService.findlist(paramsMap);

        List<OutletsOrderRefundCancelExcelVO> list = outletsOrderRefundCancelService.packageOutletsOrderRefundCancel(result);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName=new String(("退款订单导出"+date + ".xlsx").getBytes("gb2312"),  "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            EasyExcel.write(response.getOutputStream(), OutletsOrderRefundCancelExcelVO.class).sheet("Sheet1").doWrite(list);
        } catch (Exception e) {
            System.out.println("告警信息下载失败" + e.getMessage());
        }
    }

    /**
     * 导出对应商户的退款订单excel
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

        if (!StringGeneralUtil.checkNotNull(startTime)){
            startTime = DateStrUtil.nowDateStrYearMoonDay();
        }
        if (!StringGeneralUtil.checkNotNull(endTime)){
            endTime = DateStrUtil.nowDateStrYearMoonDay();
        }
        endTime = endTime + " 23:59:59";

        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);
        paramsMap.put("cashId",cashId);

        List<OutletsOrderRefundCancelVO> result = outletsOrderRefundCancelService.findlist(paramsMap);

        List<OutletsOrderRefundCancelExcelVO> list = outletsOrderRefundCancelService.packageOutletsOrderRefundCancel(result);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());
            String fileName=new String(("退款订单导出"+date + ".xlsx").getBytes("gb2312"),  "iso8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            EasyExcel.write(response.getOutputStream(), OutletsOrderRefundCancelExcelVO.class).sheet("Sheet1").doWrite(list);
        } catch (Exception e) {
            System.out.println("告警信息下载失败" + e.getMessage());
        }
    }
}

