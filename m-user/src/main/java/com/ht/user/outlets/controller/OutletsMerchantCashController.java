package com.ht.user.outlets.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.MisOrderData;
import com.ht.user.config.CardOrdersStateConfig;
import com.ht.user.ordergoods.entity.UploadOrderDetails;
import com.ht.user.outlets.allinpay.SybPayService;
import com.ht.user.outlets.config.MerchantCashMapConstant;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.jlpay.contants.CustomClientAddQrDeviceResponse;
import com.ht.user.outlets.jlpay.contants.TransConstants;
import com.ht.user.outlets.jlpay.trans.ClientAddQrDeviceService;
import com.ht.user.outlets.service.IOutletsMerchantCashService;
import com.ht.user.outlets.service.IOutletsOrderPayTraceService;
import com.ht.user.outlets.service.IOutletsOrderRefTraceService;
import com.ht.user.outlets.service.IOutletsOrdersService;
import com.ht.user.outlets.util.*;
import com.ht.user.outlets.vo.OutletsOrdersVO;
import com.ht.user.outlets.vo.QueryOutletsOrdersVO;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单主表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@RestController
@RequestMapping("/tonglian/outlets/merchantCash")
public class OutletsMerchantCashController {

    @Autowired
    private IOutletsMerchantCashService iOutletsMerchantCashService;

    /**
     * 奥特莱斯 根据收银台获取缓存商户名
     *
     * @param
     * @return
     */
    @GetMapping("/getCacheMerchantName")
    public String getCacheMerchantName(@RequestParam("cashId") String cashId) {
        return MerchantCashMapConstant.merchantCashMap.get(cashId);
    }

    /**
     * 奥特莱斯 刷新缓存商户名
     *
     * @param
     * @return
     */
    @GetMapping("/refreshCacheMerchantName")
    public Map<String, Map<String,String>> refreshCacheMerchantName() {
        List<OutletsMerchantCash> list = iOutletsMerchantCashService.list();
        for (OutletsMerchantCash outletsMerchantCash : list) {
            MerchantCashMapConstant.merchantCashMap.put(outletsMerchantCash.getCashId(), outletsMerchantCash.getMerchName());
            MerchantCashMapConstant.cashClientAddQrDeviceMap.put(outletsMerchantCash.getCashId(), outletsMerchantCash.getTermNo());
        }
        Map<String, Map<String,String>> retMap = new HashMap<>();
        retMap.put("merchantCashMap",MerchantCashMapConstant.merchantCashMap);
        retMap.put("cashClientAddQrDeviceMap",MerchantCashMapConstant.cashClientAddQrDeviceMap);
        return retMap;
    }


    @PostMapping("/summaryMerchantCashPayData")
    public Page<RetSummaryMerchantCashPayData> summaryMerchantCashPayData(@RequestBody ReqSummaryMerchantCashPayData reqSummaryMerchantCashPayData) {
        if (StringGeneralUtil.checkNotNull(reqSummaryMerchantCashPayData.getStartTime())) {
            reqSummaryMerchantCashPayData.setStartTime(reqSummaryMerchantCashPayData.getStartTime() + " 00:00:01");
        } else {
            reqSummaryMerchantCashPayData.setStartTime(DateStrUtil.nowDateStrYearMoonDay() + " 00:00:01");
        }
        if (StringGeneralUtil.checkNotNull(reqSummaryMerchantCashPayData.getEndTime())) {
            reqSummaryMerchantCashPayData.setEndTime(reqSummaryMerchantCashPayData.getEndTime() + " 23:59:59");
        } else {
            reqSummaryMerchantCashPayData.setEndTime(DateStrUtil.nowDateStrYearMoonDay() + " 23:59:59");
        }
        return iOutletsMerchantCashService.summaryMerchantCashPayData(reqSummaryMerchantCashPayData);
    }

    /**
     * 刷新码付加机 clientAddQrDevice
     *
     * @param
     * @return
     *
     */
    @PostMapping("/clientAddQrDeviceAll")
    public void clientAddQrDeviceAll() throws InterruptedException {
//        QueryWrapper<OutletsMerchantCash> queryWrapper = new QueryWrapper<>();
//        queryWrapper.ge("id",163);
//        List<OutletsMerchantCash> list = iOutletsMerchantCashService.list(queryWrapper);
//        for (OutletsMerchantCash outletsMerchantCash : list) {
//            if (!StringGeneralUtil.checkNotNull(outletsMerchantCash.getTermNo())) {
//                Thread.sleep(15000);
//                // 码付加机调用
//                CustomClientAddQrDeviceResponse customClientAddQrDeviceResponse = ClientAddQrDeviceService.clientAddQrDevice();
//                System.out.println(customClientAddQrDeviceResponse);
//                outletsMerchantCash.setTermNo(customClientAddQrDeviceResponse.getTermNo());
//                iOutletsMerchantCashService.updateById(outletsMerchantCash);
//            }
//        }

        for (int i = 1; i <= 15; i++) {

            System.out.println("开始");
            Thread.sleep(15000);
            // 码付加机调用
            CustomClientAddQrDeviceResponse customClientAddQrDeviceResponse = ClientAddQrDeviceService.clientAddQrDevice();
            System.out.println(customClientAddQrDeviceResponse);
        }
    }


    /**
     * 奥特莱斯 线上 修改支付公司标识
     *
     * @param
     * @return
     */
    @PostMapping("/changePayCompany")
    public Map<String, String> changePayCompany(@RequestBody Map<String, String> requestMap) {

        if (StringGeneralUtil.checkNotNull(requestMap.get("qr"))) {
            CompanyPayWeight.qrPayCompany = requestMap.get("qr");
        }

        if (StringGeneralUtil.checkNotNull(requestMap.get("pos"))) {
            CompanyPayWeight.posPayCompany = requestMap.get("pos");
        }

        Map<String, String> retMap = new HashMap<>();
        retMap.put("qrPayCompany", CompanyPayWeight.qrPayCompany);
        retMap.put("posPayCompany", CompanyPayWeight.posPayCompany);
        return retMap;
    }

    /**
     * 奥莱线上修改pos开关标识
     *
     * @param
     * @return
     */
    @PostMapping("/updateOpenPosFlag")
    public Map<String, String> updateOpenPosFlag(@RequestBody Map<String, String> requestMap) {
        String flag = requestMap.get("ifOpenPos");
        CompanyPayWeight.ifOpenPos = flag;

        Map<String, String> retMap = new HashMap<>();
        retMap.put("ifOpenPos", CompanyPayWeight.ifOpenPos);
        return retMap;
    }

}

