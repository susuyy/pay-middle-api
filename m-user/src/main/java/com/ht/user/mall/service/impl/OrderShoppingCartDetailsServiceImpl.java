package com.ht.user.mall.service.impl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.mall.entity.OrderOrderDetails;
import com.ht.user.mall.entity.OrderShoppingCartDetails;
import com.ht.user.mall.mapper.OrderShoppingCartDetailsMapper;
import com.ht.user.mall.service.OrderShoppingCartDetailsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-09
 */
@Service
public class OrderShoppingCartDetailsServiceImpl extends ServiceImpl<OrderShoppingCartDetailsMapper, OrderShoppingCartDetails> implements OrderShoppingCartDetailsService {



    /**
     * 添加购物车数据明细
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
     * @param disccount
     */
    @Override
    public void addOrderShoppingCartDetails(String merchantCode, String orderCode, Integer quantity, Integer amount, String productionCode, String productionName, String categoryCode, String categoryName, String activityCode, String state, String type, int disccount) {
        OrderShoppingCartDetails orderShoppingCartDetails = new OrderShoppingCartDetails();
        orderShoppingCartDetails.setMerchantCode(merchantCode);
        orderShoppingCartDetails.setOrderCode(orderCode);
        orderShoppingCartDetails.setQuantity(new BigDecimal(quantity));
        orderShoppingCartDetails.setAmount(amount);
        orderShoppingCartDetails.setProductionCode(productionCode);
        orderShoppingCartDetails.setProductionName(productionName);
        orderShoppingCartDetails.setProductionCategoryCode(categoryCode);
        orderShoppingCartDetails.setProductionCategoryName(categoryName);
        orderShoppingCartDetails.setActivityCode(activityCode);
        orderShoppingCartDetails.setState(state);
        orderShoppingCartDetails.setType(type);
        orderShoppingCartDetails.setDiscount(disccount);
        this.baseMapper.insert(orderShoppingCartDetails);
    }

    /**
     * 根据orderCode 查询
     * @param orderCode
     * @return
     */
    @Override
    public OrderShoppingCartDetails queryByOrderCode(String orderCode) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("order_code",orderCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 更新明细状态
     * @param orderCode
     * @param state
     */
    @Override
    public void updateStateByOrderCode(String orderCode, String state) {
        this.baseMapper.updateStateByOrderCode(orderCode,state);
    }

    /**
     * 清空 失效 购物车明细
     * @param orderCodeList
     */
    @Override
    public void delInvalidState(List<String> orderCodeList) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.in("order_code",orderCodeList);
        this.baseMapper.delete(queryWrapper);
    }

    /**
     * 根据 batchCode 查询购物车明细
     * @param batchCode
     * @return
     */
    @Override
    public OrderShoppingCartDetails queryByBatchCode(String batchCode) {
        QueryWrapper<OrderShoppingCartDetails> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("activity_code",batchCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据batchCode 删除购物车明细
     * @param orderCode
     */
    @Override
    public void delByOrderCode(String orderCode) {
        QueryWrapper<OrderShoppingCartDetails> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        this.baseMapper.delete(queryWrapper);
    }

    @Override
    public void updateQuantityByOrderCode(Integer totalQuantity, String orderCode) {
        this.baseMapper.updateQuantityByOrderCode(totalQuantity,orderCode);
    }

    @Override
    public void updateAmountByOrderCode(Integer newAmount, String orderCode) {
        this.baseMapper.updateAmountByOrderCode(newAmount,orderCode);
    }
}
