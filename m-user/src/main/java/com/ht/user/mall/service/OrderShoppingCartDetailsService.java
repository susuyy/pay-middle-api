package com.ht.user.mall.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.mall.entity.OrderOrderDetails;
import com.ht.user.mall.entity.OrderShoppingCartDetails;

import java.util.List;

/**
 * <p>
 * 订单明细 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-09
 */
public interface OrderShoppingCartDetailsService extends IService<OrderShoppingCartDetails> {





    /**
     * 添加购物车 数据 明细
     * @param merchantCode
     * @param orderCode
     * @param quantity
     * @param amount
     * @param productionCode
     * @param productionName
     * @param categoryCode
     * @param categoryName
     * @param activityCode
     * @param state
     * @param type
     * @param discount
     */
    void addOrderShoppingCartDetails(String merchantCode,
                                     String orderCode,
                                     Integer quantity,
                                     Integer amount,
                                     String productionCode,
                                     String productionName,
                                     String categoryCode,
                                     String categoryName,
                                     String activityCode,
                                     String state,
                                     String type,
                                     int discount);

    /**
     * 根据 order 查询
     * @param orderCode
     * @return
     */
    OrderShoppingCartDetails queryByOrderCode(String orderCode);

    /**
     * 更新 状态 state
     * @param orderCode
     * @param state
     */
    void updateStateByOrderCode(String orderCode, String state);

    /**
     * 清空失效 明细
     * @param orderCodeList
     */
    void delInvalidState(List<String> orderCodeList);

    /**
     * 根据batchCode 查询购物车明细
     * @param batchCode
     * @return
     */
    OrderShoppingCartDetails queryByBatchCode(String batchCode);

    /**
     * 根据batchCode 删除 购物车数据
     * @param orderCode
     */
    void delByOrderCode(String orderCode);

    /**
     * 更新数量
     * @param totalQuantity
     * @param orderCode
     */
    void updateQuantityByOrderCode(Integer totalQuantity, String orderCode);

    /**
     * 修改金额
     * @param newAmount
     * @param orderCode
     */
    void updateAmountByOrderCode(Integer newAmount, String orderCode);
}
