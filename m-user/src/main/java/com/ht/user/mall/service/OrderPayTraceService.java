package com.ht.user.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.mall.entity.OrderPayTrace;


/**
 * <p>
 * 订单支付流水 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
public interface OrderPayTraceService extends IService<OrderPayTrace> {

    /**
     * 保存未支付流水数据
     * @param orderCode
     * @param orderDetailId
     * @param payCode
     * @param type
     * @param state
     * @param source
     * @param sourceId
     * @param amount
     * @param posSerialNum
     * @return
     */
    OrderPayTrace saveOrderPayTrace(String orderCode,
                                    Long orderDetailId,
                                    String payCode,
                                    String type,
                                    String state,
                                    String source,
                                    String sourceId,
                                    Integer amount,
                                    String posSerialNum);

    /**
     * 更新 流水表状态 根据orderCode
     * @param orderCode
     * @param state
     * @param payCode
     */
    void updateStateByOrderCode(String orderCode, String state, String payCode);
}
