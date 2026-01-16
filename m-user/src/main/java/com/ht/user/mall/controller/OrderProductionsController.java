package com.ht.user.mall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.common.CardConstant;
import com.ht.user.card.entity.Merchants;
import com.ht.user.mall.entity.OrderProductions;
import com.ht.user.mall.service.OrderProductionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mall/productions")
@CrossOrigin(allowCredentials = "true")
public class OrderProductionsController {

    @Autowired
    private OrderProductionsService orderProductionsService;

    /**
     * 根据 商品编码 查询商品
     *
     * @param productionsCode
     * @param storeMerchantCode
     * @return
     */
    @GetMapping("/getByCode")
    public OrderProductions getByCode(@RequestParam("productionsCode") String productionsCode,
                                      @RequestParam("storeMerchantCode") String storeMerchantCode) {
        OrderProductions orderProductions = orderProductionsService.queryByProductionCode(productionsCode, storeMerchantCode);
        return orderProductions;
    }

    /**
     * 获取商户所有产品信息
     *
     * @param adminMerchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/subMerchantProductions")
    public IPage<OrderProductions> getOrderProductionPage(
            @RequestBody List<Merchants> adminMerchantCode,
            @RequestParam(value = "state", required = false, defaultValue = CardConstant.MERCHANT_CARD_ON_SALE_STATE_N) String onSaleState,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") Long pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize,
            @RequestParam(value = "productionName", required = false, defaultValue = "") String productionName,
            @RequestParam(value = "productionCode", required = false, defaultValue = "") String productionCode) {
        IPage<OrderProductions> pageInfo = new Page<>(pageNo, pageSize);
        return orderProductionsService.selectPage(adminMerchantCode, pageInfo, productionName, productionCode, onSaleState);
    }

    /**
     * 判断产品code是否存在
     *
     * @param productionCode
     * @param storeMerchantCode
     * @return
     */
    @GetMapping("/exist/{productionCode}/{storeMerchantCode}")
    public boolean getProductionByCode(@PathVariable("productionCode") String productionCode, @PathVariable("storeMerchantCode") String storeMerchantCode) {
        return orderProductionsService.queryByProductionCode(productionCode, storeMerchantCode) != null;
    }

    /**
     * 保存产品信息
     *
     * @param productions
     */
    @PostMapping
    public void save(@RequestBody OrderProductions productions) {
        orderProductionsService.saveOrUpdate(productions);
    }

    /**
     * 保存产品信息
     *
     * @param productions
     */
    @PutMapping
    public void update(@RequestBody OrderProductions productions) {
        orderProductionsService.updateById(productions);
    }

    /**
     * 保存产品信息
     *
     * @param id
     * @param state
     */
    @PutMapping("/{id}/state/{state}")
    public void updateState(@PathVariable("id") String id,
                            @PathVariable("state") String state) {
        if ("normal".equals(state) || "frozen".equals(state)) {
            OrderProductions productions = orderProductionsService.getById(id);
            productions.setState(state);
            orderProductionsService.updateById(productions);
        }
    }
}
