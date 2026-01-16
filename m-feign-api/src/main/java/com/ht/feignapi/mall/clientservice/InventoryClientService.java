package com.ht.feignapi.mall.clientservice;

import com.ht.feignapi.mall.entity.Inventory;
import com.ht.feignapi.mall.entity.MallProductions;
import com.ht.feignapi.mall.entity.MallShops;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: zheng weiguang
 * @Date: 2020/9/11 17:59
 */
@FeignClient(name = "${custom.client.inventory.name}",contextId = "mallInventory")
public interface InventoryClientService {

    /**
     * 通过商户号和产品code获取库存数目
     * @param merchantCode
     * @param productionCode
     * @return
     */
    @GetMapping("/inventory/{merchantCode}/{productionCode}")
    Result<Integer> getInventory(@PathVariable("merchantCode") String merchantCode,
                        @PathVariable("productionCode") String productionCode);

    /**
     * 扣减库存
     * @param merchantCode 商户号
     * @param productionCode 产品code
     * @param request : 包含amount字段的请求体
     * @return
     */
    @PutMapping("/inventory/subtract/{merchantCode}/{productionCode}")
    void subtractInventory(@PathVariable("merchantCode") String merchantCode,
                           @PathVariable("productionCode") String productionCode,
                           @RequestBody Map<String,Integer> request);

    /**
     * 增加库存
     * @param merchantCode 商户号
     * @param productionCode 产品code
     * @param request : 包含amount、batchCode字段的请求体
     * @return
     */
    @PutMapping("/inventory/add/{merchantCode}/{productionCode}")
    void addInventory(@PathVariable("merchantCode") String merchantCode,
                           @PathVariable("productionCode") String productionCode,
                           @RequestBody Map<String,String> request);

    /**
     * 新增某个商品的库存
     * @param merchantCode
     * @param productionCode
     * @param amountMap 包含amount键的一个map
     */
    @PostMapping("/inventory/{merchantCode}/{productionCode}")
    Result<Inventory> createProductionInventory(@PathVariable("merchantCode") String merchantCode,
                                                @PathVariable("productionCode") String productionCode,
                                                @RequestBody Map<String,Integer> amountMap);

    /**
     * 创建商户默认仓库
     * @param merchantCode 商户号
     */
    @PostMapping("/map-merchant-warehouse/default/{merchantCode}")
    void createDefaultMerchantWarehouse(@PathVariable("merchantCode") String merchantCode);
}
