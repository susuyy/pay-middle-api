package com.ht.feignapi.tonglian.merchant.clientservice;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfig;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/13 14:59
 */
@FeignClient(name = "${custom.client.merchant.name}",contextId = "merchantConfig")
public interface MerchantsConfigClientService {

    /**
     * 获取商户支付参数
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants-config/getPayData/{merchantCode}")
    Result<List<MerchantsConfigVO>> getPayData(@PathVariable("merchantCode")String merchantCode);

    /**
     * 获取商户下某个固定key的配置
     * @param merchantCode
     * @param key
     * @return
     */
    @GetMapping("/merchants-config/{merchantCode}/key/{key}")
    Result<String> getConfigByKey(@PathVariable("merchantCode") String merchantCode, @PathVariable("key") String key);

    /**
     * 获取商户下某个group的配置list
     * @param merchantCode
     * @param groupCode
     * @return
     */
    @GetMapping("/merchants-config/{merchantCode}/group/{groupCode}")
    Result<List<MerchantsConfig>> getListByGroupCode(@PathVariable("merchantCode") String merchantCode,@PathVariable("groupCode") String groupCode);

    /**
     * 保存merchantConfig
     * @param merchantsConfig
     */
    @PostMapping("/merchants-config")
    void save(@RequestBody MerchantsConfig merchantsConfig);
}
