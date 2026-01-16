package com.ht.user.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.ht.user.card.entity.CardMapMerchantCards;
import com.ht.user.card.entity.CardMapUserCards;
import com.ht.user.mall.entity.*;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 订单主表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
public interface OrderOrdersService extends IService<OrderOrders> {

    /**
     * 保存 所有的 订单数据 订单主表,明细表,流水表
     * @param userId
     * @param productionCode
     * @param quantity
     * @param storeMerchantCode
     * @param objectMerchantCode
     * @param orderWayBills
     * @param categoryLevel01Code
     * @param cardMapMerchantCards
     * @param discount
     * @return
     */
    RetUnionOrderData saveOrderAllData(Long userId, String productionCode, Integer quantity, String storeMerchantCode, String objectMerchantCode, OrderWayBills orderWayBills, String categoryLevel01Code, CardMapMerchantCards cardMapMerchantCards, Integer discount);

    /**
     * 保存订单主表数据
     * @param orderCode
     * @param type
     * @param state
     * @param merchantCode
     * @param userId
     * @param saleId
     * @param quantity
     * @param amount
     * @param comments
     * @param discount
     * @return
     */
    OrderOrders saveOrderOrders(String orderCode,
                                String type,
                                String state,
                                String merchantCode,
                                Long userId,
                                String saleId,
                                Integer quantity,
                                Integer amount,
                                String comments,
                                Integer discount);



    /**
     * 更新 订单主表 明细表 流水表状态
     * @param paySuccess
     */
    void updateMallOrderAllPaid(@RequestBody PaySuccess paySuccess);

    /**
     * 修改订单状态 根据orderCode
     * @param orderCode
     * @param state
     */
    void updateStateByOrderCode(String orderCode, String state);

    /**
     * 修改订单主表 折扣数据
     * @param orderCode
     * @param discount
     */
    void updateDiscountByOrderCode(String orderCode, Integer discount);

    /**
     * 按商户分页展示用户 订单主表数据
     * @param userId
     * @param pageNo
     * @param pageSize
     * @param state
     * @return
     */
    IPage<OrderOrders> queryMyOrderMaster(Long userId, Integer pageNo, Integer pageSize, String state);

    /**
     * 查询用户某个商户下的更多订单
     * @param merchantCode
     * @param userId
     * @param pageNo
     * @param pageSize
     * @param state
     * @return
     */
    Page<OrderOrders> moreMyOrderMaster(String merchantCode, Long userId, Integer pageNo, Integer pageSize, String state);
}
