package com.ht.feignapi.mall.clientservice;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.mall.entity.OrderProductions;
import com.ht.feignapi.mall.entity.OrderProductionsInstruction;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.DELETE;
import java.util.List;

@FeignClient(name = "${custom.client.order.name}",contextId = "orderProductions")
public interface OrderProductionsClientService {

    /**
     * 用户添加购物车
     * @param productionsCode
     */
    @GetMapping("/mall/productions/getByCode")
    Result<OrderProductions> getByCode(@RequestParam("productionsCode")String productionsCode,@RequestParam("storeMerchantCode") String storeMerchantCode);


    /**
     * 查看productionCode是否已经存在
     * @param productionCode
     * @return
     */
    @GetMapping("/mall/productions/exist/{productionCode}/{storeMerchantCode}")
    Result<Boolean> checkProductionCodeExist(@PathVariable("productionCode") String productionCode,@PathVariable("storeMerchantCode") String storeMerchantCode);

    /**
     * 保存出售实际商品
     * @param orderProductions
     */
    @PostMapping("/mall/productions")
    void saveOrderProduction(@RequestBody OrderProductions orderProductions);

    /**
     * 获取subMerchantList下所有的商品
     * @param subMerchantList
     * @param state
     * @param pageNo
     * @param pageSize
     * @param productionName
     * @param productionCode
     * @return
     */
    @PostMapping("/mall/productions/subMerchantProductions")
    Result<Page<OrderProductions>> selectOrderProductionPage(
            @RequestBody List<Merchants> subMerchantList,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize,
            @RequestParam("productionName") String productionName,
            @RequestParam("productionCode") String productionCode,
            @RequestParam("state") String state);

    /**
     * 查询 商品集合
     * @param productionCode
     * @return
     */
    @GetMapping("/mall/productions/byProductionCodeNotMerchant")
    Result<List<OrderProductions>> queryByProductionCodeNotMerchant(@RequestParam("productionCode") String productionCode);

    /**
     * 获取产品的使用说明
     * @param merchantCode
     * @param productionCode
     * @param type
     * @return
     */
    @GetMapping("/mall/productions-instruction/{merchantCode}/{productionCode}/{type}")
    Result<List<OrderProductionsInstruction>> getProductionInstruments(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("productionCode") String productionCode,
            @PathVariable("type") String type);

    /**
     * 保存说明
     * @param productionsInstruction
     */
    @PostMapping("/mall/productions-instruction")
    void saveOrderInstruments(@RequestBody OrderProductionsInstruction productionsInstruction);

    /**
     * 删除某个code的说明
     * @param productionCode
     * @param merchantCode
     */
    @DeleteMapping("/mall/productions-instruction")
    void removeInstruments(@RequestParam("productionCode") String productionCode,@RequestParam("merchantCode") String merchantCode);
}
