package com.ht.feignapi.higo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.higo.entity.InfoSellPro;
import com.ht.feignapi.higo.entity.InfoSellProShowData;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.controller.PrimeUserConsumerController;
import com.ht.feignapi.prime.entity.CardActualMapUser;
import com.ht.feignapi.prime.entity.CardElectronic;
import com.ht.feignapi.prime.entity.CardElectronicSell;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/higo/Sell")
@CrossOrigin(allowCredentials = "true")
public class HiGoSellController {

    private Logger logger = LoggerFactory.getLogger(PrimeUserConsumerController.class);

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    /**
     * 后台 售卡 发卡 绑定接口 (线下绑卡 根据收款类型生成订单)
     *
     * @param
     * @return
     */
    @PostMapping("/infoSellPro")
    public List<InfoSellProShowData> infoSellPro(@RequestBody InfoSellPro infoSellPro) {
        List<Merchants> merchantsList = merchantsClientService.getSubMerchants(infoSellPro.getMerchantCode()).getData();
        List<InfoSellProShowData> retList = new ArrayList<>();
        for (Merchants merchants : merchantsList) {
            Page<CardElectronicSell> sellIPage = msPrimeClient.getSellMerchantList(1L, 3L, "", "", merchants.getMerchantCode()).getData();
            List<CardElectronicSell> records = sellIPage.getRecords();

            InfoSellProShowData infoSellProShowData = new InfoSellProShowData();
            infoSellProShowData.setMerchants(merchants);
            infoSellProShowData.setList(records);

            retList.add(infoSellProShowData);
        }
        return retList;
    }
}
