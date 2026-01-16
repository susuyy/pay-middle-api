package com.ht.user.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ht.user.mall.constant.OrderConstant;
import com.ht.user.mall.entity.*;
import com.ht.user.mall.mapper.OrderOrderDetailsMapper;
import com.ht.user.mall.service.OrderOrderDetailsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单明细 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
@Service
public class OrderOrderDetailsServiceImpl extends ServiceImpl<OrderOrderDetailsMapper, OrderOrderDetails> implements OrderOrderDetailsService {

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
    @Override
    public OrderOrderDetails saveOrderOrderDetails(String orderCode,
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
                                                   Integer discount) {
        OrderOrderDetails orderOrderDetails = new OrderOrderDetails();
        orderOrderDetails.setOrderCode(orderCode);
        orderOrderDetails.setMerchantCode(merchantCode);
        orderOrderDetails.setUserId(userId);
        orderOrderDetails.setQuantity(new BigDecimal(quantity));
        orderOrderDetails.setAmount(amount);
        orderOrderDetails.setProductionCode(productionCode);
        orderOrderDetails.setProductionName(productionName);
        orderOrderDetails.setProductionCategoryCode(productionCategoryCode);
        orderOrderDetails.setProductionCategoryName(productionCategoryName);
        orderOrderDetails.setActivityCode(activityCode);
        orderOrderDetails.setShoppingCartOrderCode(shoppingCartOrderCode);
        orderOrderDetails.setState(state);
        orderOrderDetails.setType(type);
        orderOrderDetails.setDiscount(discount);
        orderOrderDetails.setCreateAt(new Date());
        orderOrderDetails.setUpdateAt(new Date());
        this.baseMapper.insert(orderOrderDetails);
        return orderOrderDetails;
    }

    /**
     * 展示用户订单信息 按商户分组 分页
     * @param userId
     * @param pageNo
     * @param pageSize
     */
    @Override
    public IPage<OrderOrderDetails> queryMyOrderDetail(Long userId, Integer pageNo, Integer pageSize,String state) {
        List<ReturnShowOrderDetailData> returnShowOrderDetailDataList =new ArrayList<>();
        Page<OrderOrderDetails> page = new Page<>(pageNo, pageSize);
        if (OrderConstant.ALL_STATE.equals(state)){
            IPage<OrderOrderDetails> orderDetailsIPage = this.baseMapper.selectPageNoState(page,userId);
//            List<OrderOrderDetails> orderDetailsMerchantCodeList = orderDetailsIPage.getRecords();
//            for (OrderOrderDetails orderOrderDetails : orderDetailsMerchantCodeList) {
//                List<OrderOrderDetails> orderOrderDetailsListSon=this.baseMapper.selectByUserIdMerchant(userId,orderOrderDetails.getMerchantCode());
//                ReturnShowOrderDetailData returnShowOrderDetailData = new ReturnShowOrderDetailData();
//                returnShowOrderDetailData.setMerchantCode(orderOrderDetails.getMerchantCode());
//                returnShowOrderDetailData.setOrderOrderDetailsList(orderOrderDetailsListSon);
//                returnShowOrderDetailDataList.add(returnShowOrderDetailData);
//            }
//            RetPageOrderData retPageOrderData = new RetPageOrderData();
//            retPageOrderData.setRecords(returnShowOrderDetailDataList);
//            retPageOrderData.setTotal(orderDetailsIPage.getTotal());
//            retPageOrderData.setSize(orderDetailsIPage.getSize());
//            retPageOrderData.setCurrent(orderDetailsIPage.getCurrent());
//            retPageOrderData.setOrders(orderDetailsIPage.orders());
//            retPageOrderData.setOptimizeCountSql(orderDetailsIPage.optimizeCountSql());
//            retPageOrderData.setHitCount(orderDetailsIPage.isHitCount());
//            retPageOrderData.setSearchCount(orderDetailsIPage.isSearchCount());
//            retPageOrderData.setPages(orderDetailsIPage.getPages());
            return orderDetailsIPage;
        }else {
            IPage<OrderOrderDetails> orderDetailsIPage = this.baseMapper.selectPageAndState(page,userId,state);
//            List<OrderOrderDetails> orderDetailsMerchantCodeList = orderDetailsIPage.getRecords();
//            for (OrderOrderDetails orderOrderDetails : orderDetailsMerchantCodeList) {
//                List<OrderOrderDetails> orderOrderDetailsListSon=this.baseMapper.selectByUserIdMerchantState(userId,orderOrderDetails.getMerchantCode(),state);
//                ReturnShowOrderDetailData returnShowOrderDetailData = new ReturnShowOrderDetailData();
//                returnShowOrderDetailData.setMerchantCode(orderOrderDetails.getMerchantCode());
//                returnShowOrderDetailData.setOrderOrderDetailsList(orderOrderDetailsListSon);
//                returnShowOrderDetailDataList.add(returnShowOrderDetailData);
//            }
//            RetPageOrderData retPageOrderData = new RetPageOrderData();
//            retPageOrderData.setRecords(returnShowOrderDetailDataList);
//            retPageOrderData.setTotal(orderDetailsIPage.getTotal());
//            retPageOrderData.setSize(orderDetailsIPage.getSize());
//            retPageOrderData.setCurrent(orderDetailsIPage.getCurrent());
//            retPageOrderData.setOrders(orderDetailsIPage.orders());
//            retPageOrderData.setOptimizeCountSql(orderDetailsIPage.optimizeCountSql());
//            retPageOrderData.setHitCount(orderDetailsIPage.isHitCount());
//            retPageOrderData.setSearchCount(orderDetailsIPage.isSearchCount());
//            retPageOrderData.setPages(orderDetailsIPage.getPages());
            return orderDetailsIPage;
        }
    }

    /**
     * 点击更多 分页获取某个商户下的用户订单
     * @param merchantCode
     * @param userId
     * @param pageNo
     * @param pageSize
     * @param state
     * @return
     */
    @Override
    public IPage<OrderOrderDetails> moreMyOrder(String merchantCode, Long userId, Integer pageNo, Integer pageSize, String state) {
        Page page=new Page(pageNo,pageSize);
        QueryWrapper<OrderOrderDetails> queryWrapper=new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("merchant_code",merchantCode);
        if (!OrderConstant.ALL_STATE.equals(state)){
            queryWrapper.eq("state",state);
        }
        return this.baseMapper.selectPage(page,queryWrapper);
    }

    /**
     * 更新订单明细状态 更具orderCode
     * @param orderCode
     * @param state
     */
    @Override
    public void updateStateByOrderCode(String orderCode, String state) {
        UpdateWrapper<OrderOrderDetails> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("order_code",orderCode);
        OrderOrderDetails orderOrderDetails = new OrderOrderDetails();
        orderOrderDetails.setState(state);
        this.baseMapper.update(orderOrderDetails,updateWrapper);
    }

    /**
     * 根据id 修改折扣金额
     * @param id
     * @param oneDetailDiscount
     */
    @Override
    public void updateDiscountById(Long id, Integer oneDetailDiscount) {
        this.baseMapper.updateDiscountById(id,oneDetailDiscount);
    }

    /**
     * 根据id 修改订单明细状态
     * @param orderDetailId
     * @param state
     */
    @Override
    public void updateStateById(String orderDetailId, String state) {
        this.baseMapper.updateStateById(orderDetailId,state);
    }

}
