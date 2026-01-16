package com.ht.user.outlets.config;

import com.ht.user.outlets.controller.OutletsOrdersController;
import com.ht.user.outlets.entity.OutletsMerchantCash;
import com.ht.user.outlets.service.IOutletsMerchantCashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MerchantCashMapConstant {

    public static String DEFAULT_CASH = "default-cash";

    private Logger logger = LoggerFactory.getLogger(MerchantCashMapConstant.class);


    public static Map<String, String> merchantCashMap = new HashMap<String, String>();

    public static Map<String, String> cashClientAddQrDeviceMap = new HashMap<String, String>();

    @Autowired
    private IOutletsMerchantCashService iOutletsMerchantCashService;

    @PostConstruct
    public void init(){
        logger.info("系统加载,收银台和商户名称对应关系");
        List<OutletsMerchantCash> list = iOutletsMerchantCashService.list();
        for (OutletsMerchantCash outletsMerchantCash : list) {
            merchantCashMap.put(outletsMerchantCash.getCashId(),outletsMerchantCash.getMerchName());
        }
        logger.info(merchantCashMap.toString());

        for (OutletsMerchantCash outletsMerchantCash : list) {
            cashClientAddQrDeviceMap.put(outletsMerchantCash.getCashId(),outletsMerchantCash.getTermNo());
        }
        cashClientAddQrDeviceMap.put(DEFAULT_CASH,"S2982670");
        logger.info(cashClientAddQrDeviceMap.toString());
    }

}
