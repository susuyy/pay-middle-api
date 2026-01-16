package com.ht.feignapi.tonglian.merchant.controller;


import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfig;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
@RestController
@RequestMapping("/tonglian/merchantsConfig")
public class MerchantsConfigController {

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    /**
     * 获取商户支付 配置参数
     * @param merchantCode
     * @return
     */
    @GetMapping("/getPayData/{merchantCode}")
    public List<MerchantsConfigVO> getPayData(@PathVariable("merchantCode")String merchantCode){
        return merchantsConfigClientService.getPayData(merchantCode).getData();
    }

    /**
     * 获取商户的Appid
     * @param merchantCode
     * @return
     */
    @GetMapping("/getAppId/{merchantCode}")
    public String getAppId(@PathVariable("merchantCode")String merchantCode){
        return merchantsConfigClientService.getConfigByKey(merchantCode,"WX_APPID").getData();
    }

    /**
     * 获取商户的微信数据
     * @param merchantCode
     * @return
     */
    @GetMapping("/getWxData/{merchantCode}")
    public List<MerchantsConfig> getWxData(@PathVariable("merchantCode")String merchantCode){
        List<MerchantsConfig> openidDataList = merchantsConfigClientService.getListByGroupCode(merchantCode, "get_openid_data").getData();
        return openidDataList;
    }

}

