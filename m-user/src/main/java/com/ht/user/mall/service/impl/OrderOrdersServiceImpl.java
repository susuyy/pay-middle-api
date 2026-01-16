package com.ht.user.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ht.user.card.entity.CardMapMerchantCards;
import com.ht.user.mall.constant.OrderConstant;
import com.ht.user.mall.constant.OrderWayBillsTypeConstant;
import com.ht.user.mall.constant.ProductionsCategoryConstant;
import com.ht.user.mall.entity.*;
import com.ht.user.mall.mapper.OrderOrdersMapper;
import com.ht.user.mall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 订单主表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
@Service
public class OrderOrdersServiceImpl extends ServiceImpl<OrderOrdersMapper, OrderOrders> implements OrderOrdersService {

    @Autowired
    private OrderOrderDetailsService orderOrderDetailsService;

    @Autowired
    private OrderPayTraceService orderPayTraceService;

    @Autowired
    private OrderProductionsService orderProductionsService;

    @Autowired
    private OrderShoppingCartService orderShoppingCartService;

    @Autowired
    private OrderWayBillsService orderWayBillsService;


    /**
     * 保存 所有的 订单数据 订单主表,明细表,流水表
     *
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
    @Override
    public RetUnionOrderData saveOrderAllData(Long userId, String productionCode, Integer quantity, String storeMerchantCode, String objectMerchantCode, OrderWayBills orderWayBills, String categoryLevel01Code, CardMapMerchantCards cardMapMerchantCards, Integer discount) {
        String orderCode = IdWorker.getIdStr();
        if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(categoryLevel01Code)) {
            //创建订单主表数据
            saveOrderOrders(orderCode,
                    OrderConstant.SHOP_TYPE,
                    OrderConstant.UNPAID_STATE,
                    storeMerchantCode,
                    userId,
                    "-1",
                    quantity,
                    cardMapMerchantCards.getPrice() * quantity,
                    cardMapMerchantCards.getCardName() + "*" + quantity,
                    discount);
            //创建明细表数据
            OrderOrderDetails orderOrderDetails = orderOrderDetailsService.saveOrderOrderDetails(orderCode,
                    storeMerchantCode,
                    userId,
                    quantity,
                    cardMapMerchantCards.getPrice() * quantity,
                    cardMapMerchantCards.getCardCode(),
                    cardMapMerchantCards.getCardName(),
                    cardMapMerchantCards.getCategoryCode(),
                    cardMapMerchantCards.getCategoryName(),
                    cardMapMerchantCards.getBatchCode(),
                    "",
                    OrderConstant.UNPAID_STATE,
                    OrderConstant.SHOP_TYPE,
                    discount);
            if (orderWayBills != null) {
                orderWayBillsService.saveOrderWayBills(userId,
                        orderCode,
                        IdWorker.getIdStr(),
                        orderWayBills.getProvince(),
                        orderWayBills.getCity(),
                        orderWayBills.getCounty(),
                        orderWayBills.getAddress(),
                        orderWayBills.getTel(),
                        orderWayBills.getName(),
                        orderWayBills.getBillFee(),
                        OrderWayBillsTypeConstant.CARDS_TYPE,
                        OrderConstant.NORMAL,
                        storeMerchantCode);
            }
        } else {
            OrderProductions orderProductions = orderProductionsService.queryByProductionCode(productionCode, storeMerchantCode);
            //创建订单主表数据
            saveOrderOrders(orderCode,
                    OrderConstant.SHOP_TYPE,
                    OrderConstant.UNPAID_STATE,
                    orderProductions.getMerchantCode(),
                    userId,
                    "-1",
                    quantity,
                    orderProductions.getPrice() * quantity,
                    orderProductions.getProductionName() + "*" + quantity,
                    discount);
            //创建明细表数据
            OrderOrderDetails orderOrderDetails = orderOrderDetailsService.saveOrderOrderDetails(orderCode,
                    orderProductions.getMerchantCode(),
                    userId,
                    quantity,
                    orderProductions.getPrice() * quantity,
                    orderProductions.getProductionCode(),
                    orderProductions.getProductionName(),
                    orderProductions.getCategoryCode(),
                    orderProductions.getCategoryName(),
                    "",
                    "",
                    OrderConstant.UNPAID_STATE,
                    OrderConstant.SHOP_TYPE,
                    discount);

            if (orderWayBills != null) {
                orderWayBillsService.saveOrderWayBills(userId,
                        orderCode,
                        IdWorker.getIdStr(),
                        orderWayBills.getProvince(),
                        orderWayBills.getCity(),
                        orderWayBills.getCounty(),
                        orderWayBills.getAddress(),
                        orderWayBills.getTel(),
                        orderWayBills.getName(),
                        orderWayBills.getBillFee(),
                        orderWayBills.getType(),
                        OrderConstant.NORMAL,
                        orderProductions.getMerchantCode());
            }
        }
        RetUnionOrderData retUnionOrderData = new RetUnionOrderData();
        retUnionOrderData.setOrderCode(orderCode);
        retUnionOrderData.setIsToPay(true);
        retUnionOrderData.setUnionOrderMessage("下单成功");
        retUnionOrderData.setMerchantCode(storeMerchantCode);
        return retUnionOrderData;
    }

    /**
     * 保存订单主表数据
     *
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
    @Override
    public OrderOrders saveOrderOrders(String orderCode,
                                       String type,
                                       String state,
                                       String merchantCode,
                                       Long userId,
                                       String saleId,
                                       Integer quantity,
                                       Integer amount,
                                       String comments,
                                       Integer discount) {
        OrderOrders orderOrders = new OrderOrders();
        orderOrders.setOrderCode(orderCode);
        orderOrders.setType(type);
        orderOrders.setState(state);
        orderOrders.setMerchantCode(merchantCode);
        orderOrders.setUserId(userId);
        orderOrders.setSaleId(saleId);
        orderOrders.setQuantity(new BigDecimal(quantity));
        orderOrders.setAmount(amount);
        orderOrders.setComments(comments);
        orderOrders.setDiscount(discount);
        orderOrders.setCreateAt(new Date());
        orderOrders.setUpdateAt(new Date());
        this.baseMapper.insert(orderOrders);
        return orderOrders;
    }

    /**
     * 更新订单主表 明细表 流水表状态
     *
     * @param paySuccess
     */
    public void updateMallOrderAllPaid(PaySuccess paySuccess) {
        //更新主表 状态
        updateStateByOrderCode(paySuccess.getOrderCode(), OrderConstant.PAID_UN_USE_STATE);

        //更新明细表状态
        orderOrderDetailsService.updateStateByOrderCode(paySuccess.getOrderCode(), OrderConstant.PAID_UN_USE_STATE);

        //更新流水表状态
        orderPayTraceService.updateStateByOrderCode(paySuccess.getOrderCode(),
                OrderConstant.PAID_UN_USE_STATE,
                paySuccess.getPayCode());
    }

    /**
     * 更新订单主表状态 根究orderCode
     *
     * @param orderCode
     * @param state
     */
    @Override
    public void updateStateByOrderCode(String orderCode, String state) {
        UpdateWrapper<OrderOrders> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_code", orderCode);
        OrderOrders orderOrders = new OrderOrders();
        orderOrders.setState(state);
        this.baseMapper.update(orderOrders, updateWrapper);
    }

    /**
     * 修改订单主表 折扣数据
     * @param orderCode
     * @param discount
     */
    @Override
    public void updateDiscountByOrderCode(String orderCode, Integer discount) {
        this.baseMapper.updateDiscountByOrderCode(orderCode,discount);
    }

    /**
     * 分页展示用户主表数据
     * @param userId
     * @param pageNo
     * @param pageSize
     * @param state
     * @return
     */
    @Override
    public IPage<OrderOrders> queryMyOrderMaster(Long userId, Integer pageNo, Integer pageSize, String state) {
//        List<ReturnShowOrderMasterData> returnShowOrderMasterDataList =new ArrayList<>();
        Page<OrderOrderDetails> page = new Page<>(pageNo, pageSize);
        if (OrderConstant.ALL_STATE.equals(state)){
            IPage<OrderOrders> orderOrdersIPage = this.baseMapper.selectPageNoState(page,userId);
            return orderOrdersIPage;
        }else {
            IPage<OrderOrders> orderOrdersIPage = this.baseMapper.selectPageAndState(page,userId,state);
            return orderOrdersIPage;
        }
    }

    /**
     * 查询用户 在某个商户下的更多数据
     * @param merchantCode
     * @param userId
     * @param pageNo
     * @param pageSize
     * @param state
     * @return
     */
    @Override
    public Page<OrderOrders> moreMyOrderMaster(String merchantCode, Long userId, Integer pageNo, Integer pageSize, String state) {
        Page page=new Page(pageNo,pageSize);
        QueryWrapper<OrderOrders> queryWrapper=new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("merchant_code",merchantCode);
        if (!OrderConstant.ALL_STATE.equals(state)){
            queryWrapper.eq("state",state);
        }
        return this.baseMapper.selectPage(page,queryWrapper);
    }


    /**
     * 校验 商品 分类 是否需要拆单 (待调整)
     *
     * @param showShoppingCartDateList
     * @return
     */
    private Boolean checkProductionsCategory(List<ShowShoppingCartDate> showShoppingCartDateList) {
        List<String> categoryCodeList = new ArrayList<>();
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
            String productionCategoryCode = showShoppingCartDate.getProductionCategoryCode();
            categoryCodeList.add(productionCategoryCode);
        }
        HashSet<String> set = new HashSet<>(categoryCodeList);

        if (set.size()>1 && set.contains(ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE)){
            return true;
        }else {
            return false;
        }
    }


    /**
     * 对 merchantCodeList 去重
     *
     * @param merchantCodeList
     * @return
     */
    public Set<String> distinctMerchantCodeList(List<String> merchantCodeList) {
        HashSet<String> merchantCodeSet = new HashSet<>(merchantCodeList);
        return merchantCodeSet;
    }

    /**
     * 校验商户编码列表 是否有多个商户
     *
     * @param merchantCodeList
     * @return
     */
    public Boolean checkMoreMerchantCode(List<String> merchantCodeList) {
        long count = merchantCodeList.stream().distinct().count();
        System.out.println(merchantCodeList.size());
        System.out.println(count);
        if (count > 1) {
            return true;
        } else {
            return false;
        }
    }
}
