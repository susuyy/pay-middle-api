package com.ht.user.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.card.entity.Merchants;
import com.ht.user.mall.entity.OrderProductions;

import java.util.List;

/**
 * <p>
 * 卡定义 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-09
 */
public interface OrderProductionsService extends IService<OrderProductions> {

    /**
     * 根据 商品编号 查询商品信息
     * @param productionCode
     * @param storeMerchantCode
     * @return
     */
    OrderProductions queryByProductionCode(String productionCode, String storeMerchantCode);


    /**
     * 搜索主体下所有的产品
     * @param adminMerchantCode 主体code
     * @param pageInfo
     * @param productionName
     * @param productionCode
     * @param onSaleState
     * @return
     */
    IPage<OrderProductions> selectPage(List<Merchants> adminMerchantCode, IPage<OrderProductions> pageInfo, String productionName, String productionCode, String onSaleState);

}
