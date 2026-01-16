package com.ht.user.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ht.user.mall.entity.OrderPayTrace;
import com.ht.user.mall.mapper.OrderPayTraceMapper;
import com.ht.user.mall.service.OrderPayTraceService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 订单支付流水 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
@Service
public class OrderPayTraceServiceImpl extends ServiceImpl<OrderPayTraceMapper, OrderPayTrace> implements OrderPayTraceService {

    /**
     * 保存未支付支付流水数据
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
    @Override
    public OrderPayTrace saveOrderPayTrace(String orderCode,
                                           Long orderDetailId,
                                           String payCode,
                                           String type,
                                           String state,
                                           String source,
                                           String sourceId,
                                           Integer amount,
                                           String posSerialNum) {
        OrderPayTrace orderPayTrace = new OrderPayTrace();
        orderPayTrace.setOrderCode(orderCode);
        orderPayTrace.setOrderDetailId(orderDetailId);
        orderPayTrace.setPayCode(payCode);
        orderPayTrace.setType(type);
        orderPayTrace.setState(state);
        orderPayTrace.setSource(source);
        orderPayTrace.setSourceId(sourceId);
        orderPayTrace.setAmount(amount);
        orderPayTrace.setPosSerialNum(posSerialNum);
        orderPayTrace.setCreateAt(new Date());
        orderPayTrace.setUpdateAt(new Date());
        this.baseMapper.insert(orderPayTrace);
        return orderPayTrace;
    }

    /**
     * 更新流水表状态 根据orderCode
     * @param orderCode
     * @param state
     * @param payCode
     */
    @Override
    public void updateStateByOrderCode(String orderCode, String state, String payCode) {
        UpdateWrapper<OrderPayTrace> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("order_code",orderCode);
        OrderPayTrace orderPayTrace = new OrderPayTrace();
        orderPayTrace.setState(state);
        orderPayTrace.setPayCode(payCode);
        this.baseMapper.update(orderPayTrace,updateWrapper);
    }
}
