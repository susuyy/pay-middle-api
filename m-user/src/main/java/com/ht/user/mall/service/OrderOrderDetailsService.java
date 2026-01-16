package com.ht.user.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.mall.entity.OrderOrderDetails;
import com.ht.user.mall.entity.RetPageOrderData;


/**
 * <p>
 * 订单明细 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
public interface OrderOrderDetailsService extends IService<OrderOrderDetails> {


    /**
     * 保存订单详情
     * @param orderCode
     * @param merchantCode
     * @param userId
     * @param quantity
     * @param amount
     * @param productionCode
     * @param productionName
     * @param productionCategoryCode
     * @param productionCategoryName
     * @param activityCode
     * @param shoppingCartOrderCode
     * @param state
     * @param type
     * @param discount
     * @return
     */
    OrderOrderDetails saveOrderOrderDetails(String orderCode,
                                            String merchantCode,
                                            Long userId,
                                            Integer quantity,
                                            Integer amount,
                                            String productionCode,
                                            String productionName,
                                            String productionCategoryCode,
                                            String productionCategoryName,
                                            String activityCode,
                                            String shoppingCartOrderCode,
                                            String state,
                                            String type,
                                            Integer discount);

    /**
     * 展示用户订单信息 按商户分组 分页
     * @param userId
     * @param pageNo
     * @param pageSize
     */
    IPage<OrderOrderDetails> queryMyOrderDetail(Long userId, Integer pageNo, Integer pageSize, String state);

    /**
     * 点击更多 分页获取某个商户下的 用户订单
     * @param merchantCode
     * @param userId
     * @param pageNo
     * @param pageSize
     * @param state
     * @return
     */
    IPage<OrderOrderDetails> moreMyOrder(String merchantCode, Long userId, Integer pageNo, Integer pageSize, String state);

    /**
     * 更新明细状态 根据orderCode
     * @param orderCode
     * @param state
     */
    void updateStateByOrderCode(String orderCode, String state);

    /**
     * 根据id 修改 折扣金额
     * @param id
     * @param oneDetailDiscount
     */
    void updateDiscountById(Long id, Integer oneDetailDiscount);

    /**
     * 根据id 修改订单明细状态
     * @param orderDetailId
     * @param state
     */
    void updateStateById(String orderDetailId, String state);
}
