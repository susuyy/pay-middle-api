package com.ht.user.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.CardMapMerchantCards;
import com.ht.user.mall.constant.ProductionsCategoryConstant;
import com.ht.user.mall.entity.*;
import com.ht.user.mall.mapper.OrderShoppingCartMapper;
import com.ht.user.mall.service.OrderProductionsService;
import com.ht.user.mall.service.OrderShoppingCartDetailsService;
import com.ht.user.mall.service.OrderShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单主表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-03
 */
@Service
public class OrderShoppingCartServiceImpl extends ServiceImpl<OrderShoppingCartMapper, OrderShoppingCart> implements OrderShoppingCartService {

    @Autowired
    private OrderShoppingCartDetailsService orderShoppingCartDetailsService;

    @Autowired
    private OrderProductionsService orderProductionsService;

    /**
     * 用户 提交商品 信息 添加购物车
     * @param addShoppingCartDate
     */
    @Override
    public void addShoppingCartDate(AddShoppingCartDate addShoppingCartDate) {
        String categoryLevel01Code = addShoppingCartDate.getCategoryLevel01Code();
        if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(categoryLevel01Code)){
            CardMapMerchantCards cardMapMerchantCards = addShoppingCartDate.getCardMapMerchantCards();

            //校验   购物车商品重复
            ShowShoppingCartDate showShoppingCartDate = queryUserProductionCodeAndMerchantCode(cardMapMerchantCards.getCardCode(),addShoppingCartDate.getUserId(),addShoppingCartDate.getStoreMerchantCode());

            if (showShoppingCartDate!=null){
                //修改数量
                updateQuantityByOrderCode(showShoppingCartDate.getQuantity().intValue(),addShoppingCartDate.getQuantity(),showShoppingCartDate.getOrderCode());
                Integer orgQuantity = showShoppingCartDate.getQuantity().intValue();
                Integer addQuantity = addShoppingCartDate.getQuantity();
                Integer totalQuantity = orgQuantity + addQuantity;
                Integer newAmount = cardMapMerchantCards.getPrice() * totalQuantity;
                //修改金额
                updateAmountByOrderCode(newAmount,showShoppingCartDate.getOrderCode());
            }else {
                String orderCode = IdWorker.getIdStr();
                //添加购物车主表数据
                addOrderShoppingCart(orderCode,
                        0,
                        cardMapMerchantCards.getMerchantCode(),
                        new BigDecimal(addShoppingCartDate.getQuantity()),
                        cardMapMerchantCards.getPrice() * addShoppingCartDate.getQuantity().intValue(),
                        "shop",
                        "unpaid",
                        addShoppingCartDate.getUserId(),
                        addShoppingCartDate.getNotice(),
                        "Y");

                //添加购物车明细数据
                orderShoppingCartDetailsService.addOrderShoppingCartDetails(cardMapMerchantCards.getMerchantCode(),
                        orderCode,
                        addShoppingCartDate.getQuantity(),
                        cardMapMerchantCards.getPrice() * addShoppingCartDate.getQuantity().intValue(),
                        cardMapMerchantCards.getCardCode(),
                        cardMapMerchantCards.getCardName(),
                        cardMapMerchantCards.getCategoryCode(),
                        cardMapMerchantCards.getCategoryName(),
                        IdWorker.getIdStr(),
                        "unpaid",
                        "shop",
                        0);
            }
        }else {
            OrderProductions orderProductions = orderProductionsService.queryByProductionCode(addShoppingCartDate.getOrderProductions().getProductionCode(), addShoppingCartDate.getStoreMerchantCode());
            String orderCode = IdWorker.getIdStr();

            //校验   购物车商品重复
            ShowShoppingCartDate showShoppingCartDate = queryUserProductionCodeAndMerchantCode(orderProductions.getProductionCode(),addShoppingCartDate.getUserId(),addShoppingCartDate.getStoreMerchantCode());

            if (showShoppingCartDate!=null){
                //修改数量
                updateQuantityByOrderCode(showShoppingCartDate.getQuantity().intValue(),addShoppingCartDate.getQuantity(),showShoppingCartDate.getOrderCode());
            }else {
                //添加购物车主表数据
                addOrderShoppingCart(orderCode,
                        0,
                        orderProductions.getMerchantCode(),
                        new BigDecimal(addShoppingCartDate.getQuantity()),
                        orderProductions.getPrice() * addShoppingCartDate.getQuantity().intValue(),
                        "shop",
                        "unpaid",
                        addShoppingCartDate.getUserId(),
                        orderProductions.getNotice(),
                        "Y");

                //添加购物车明细数据
                orderShoppingCartDetailsService.addOrderShoppingCartDetails(orderProductions.getMerchantCode(),
                        orderCode,
                        addShoppingCartDate.getQuantity(),
                        orderProductions.getPrice() * addShoppingCartDate.getQuantity().intValue(),
                        orderProductions.getProductionCode(),
                        orderProductions.getProductionName(),
                        orderProductions.getCategoryCode(),
                        orderProductions.getCategoryName(),
                        IdWorker.getIdStr(),
                        "unpaid",
                        "shop",
                        0);
            }
        }
    }

    public void updateAmountByOrderCode(Integer newAmount, String orderCode) {
        this.baseMapper.updateAmountByOrderCode(newAmount,orderCode);
        orderShoppingCartDetailsService.updateAmountByOrderCode(newAmount,orderCode);
    }

    public void updateQuantityByOrderCode(Integer orgQuantity, Integer newQuantity,String orderCode) {
        Integer totalQuantity = orgQuantity+newQuantity;
        this.baseMapper.updateQuantityByOrderCode(totalQuantity , orderCode);
        orderShoppingCartDetailsService.updateQuantityByOrderCode(totalQuantity,orderCode);
    }

    public ShowShoppingCartDate queryUserProductionCodeAndMerchantCode(String cardCode, Long userId, String storeMerchantCode) {
        return this.baseMapper.selectUserProductionCodeAndMerchantCode(cardCode,userId,storeMerchantCode);
    }

    /**
     * 添加购物车 主表数据
     * @param orderCode
     * @param discount
     * @param merchantCode
     * @param quantity
     * @param amount
     * @param type
     * @param state
     * @param userId
     * @param comments
     * @param selected
     */
    @Override
    public void addOrderShoppingCart(String orderCode,
                                     Integer discount,
                                     String merchantCode,
                                     BigDecimal quantity,
                                     Integer amount,
                                     String type,
                                     String state,
                                     Long userId,
                                     String comments,
                                     String selected){
        OrderShoppingCart orderShoppingCart = new OrderShoppingCart();
        orderShoppingCart.setOrderCode(orderCode);
        orderShoppingCart.setDiscount(discount);
        orderShoppingCart.setMerchantCode(merchantCode);
        orderShoppingCart.setQuantity(quantity);
        orderShoppingCart.setAmount(amount);
        orderShoppingCart.setType(type);
        orderShoppingCart.setState(state);
        orderShoppingCart.setUserId(userId);
        orderShoppingCart.setComments(comments);
        orderShoppingCart.setSelected(selected);
        this.baseMapper.insert(orderShoppingCart);
    }

    /**
     * 获取用户展示的 购物车数据
     * @param merchantCode
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public RetPageCartData showShoppingCart(String merchantCode, Long userId, Integer pageNo, Integer pageSize) {
        List<ReturnShowShoppingCartData> returnShowShoppingCartDataList =new ArrayList<>();
        Page<OrderShoppingCart> page = new Page<>(pageNo, pageSize);
        IPage<OrderShoppingCart> orderShoppingCartIPage = this.baseMapper.selectGroupByMerchantPage(page,userId,"unpaid");
        List<OrderShoppingCart> orderShoppingCarts = orderShoppingCartIPage.getRecords();

        for (OrderShoppingCart orderShoppingCart : orderShoppingCarts) {

            //对内容数据进行分页
//            Page<ShowShoppingCartDate> sonPage = new Page<>(1,5);
//            Page<ShowShoppingCartDate> resultPage = this.baseMapper.selectShowShoppingCartDatePage(sonPage,userId,orderShoppingCart.getMerchantCode(),"unpaid");

            List<ShowShoppingCartDate> showShoppingCartDateList=this.baseMapper.selectShowShoppingCartDate(userId,orderShoppingCart.getMerchantCode(),"unpaid");
            ReturnShowShoppingCartData returnShowShoppingCartData = new ReturnShowShoppingCartData();
            returnShowShoppingCartData.setMerchantCode(orderShoppingCart.getMerchantCode());
            returnShowShoppingCartData.setShowShoppingCartDateList(showShoppingCartDateList);

            //对内容 分页 数据 进行封装
//            returnShowShoppingCartData.setShowShoppingCartDatePage(resultPage);

            returnShowShoppingCartDataList.add(returnShowShoppingCartData);
        }

        RetPageCartData retPageCartData = new RetPageCartData();
        retPageCartData.setRecords(returnShowShoppingCartDataList);
        retPageCartData.setTotal(orderShoppingCartIPage.getTotal());
        retPageCartData.setSize(orderShoppingCartIPage.getSize());
        retPageCartData.setCurrent(orderShoppingCartIPage.getCurrent());
        retPageCartData.setOrders(orderShoppingCartIPage.orders());
        retPageCartData.setOptimizeCountSql(orderShoppingCartIPage.optimizeCountSql());
        retPageCartData.setHitCount(orderShoppingCartIPage.isHitCount());
        retPageCartData.setSearchCount(orderShoppingCartIPage.isSearchCount());
        retPageCartData.setPages(orderShoppingCartIPage.getPages());

        return retPageCartData;
    }


    /**
     * 获取购物车失效宝贝
     * @param userId
     * @param pageNo
     * @param pageSize
     */
    @Override
    public IPage<ShowShoppingCartDate> invalidCart(Long userId, String state, Integer pageNo, Integer pageSize) {
        Page<OrderShoppingCart> page = new Page<>(pageNo, pageSize);
        return this.baseMapper.selectInvalidCart(page,userId,state);
    }

    /**
     * 获取购物车 商品数量
     * @param userId
     * @return
     */
    @Override
    public Integer productionsCount(String userId) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("state","unpaid");
        return this.baseMapper.selectCount(queryWrapper);
    }

    /**
     * 清空失效宝贝
     * @param userId
     */
    @Override
    public void delInvalidCart(String userId) {
        List<ShowShoppingCartDate> showShoppingCartDateList = this.baseMapper.selectInvalidCartNoPage(userId, "invalid");
        this.baseMapper.delInvalidCart(userId,"invalid");
        List<String> orderCodeList = new ArrayList<>();
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
            orderCodeList.add(showShoppingCartDate.getOrderCode());
        }
        orderShoppingCartDetailsService.delInvalidState(orderCodeList);
    }

    /**
     * 点击 更多 获取 某个子商户(门店) 下的购物车数据 分页
     * @param merchantCode
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public IPage<ShowShoppingCartDate> moreShoppingCart(String merchantCode, Long userId, Integer pageNo, Integer pageSize) {
        Page page=new Page(pageNo,pageSize);
        return this.baseMapper.selectShowShoppingCartDateMorePage(page,userId,merchantCode,"unpaid");
    }

    /**
     * 根据orderCode 查询购物车信息
     * @param shoppingCartOrderCode
     * @return
     */
    @Override
    public OrderShoppingCart queryByOrderCode(String shoppingCartOrderCode) {
        QueryWrapper<OrderShoppingCart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",shoppingCartOrderCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据商品编码集合 和商户编码 查询购物车 数据
     * @param shoppingCartOrderCodeList
     * @param merchantCode
     * @return
     */
    @Override
    public List<ShowShoppingCartDate> queryByOrderCodeListAndMerchantCode(List<String> shoppingCartOrderCodeList, String merchantCode) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.in("orc.order_code",shoppingCartOrderCodeList);
        queryWrapper.eq("orc.merchant_code",merchantCode);
        List<ShowShoppingCartDate> showShoppingCartDateList = this.baseMapper.selectByOrderCodeListAndMerchantCode(queryWrapper);
        return showShoppingCartDateList;
    }

    /**
     * 根据 order_code 删除购物车数据
     * @param orderCode
     */
    @Override
    public void delByOrderCode(String orderCode) {
        QueryWrapper<OrderShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        this.baseMapper.delete(queryWrapper);
    }

    /**
     * 根据购物车的orderCode 集合 查询购物车 数据
     * @param shoppingCartOrderCodeList
     * @return
     */
    @Override
    public List<ShowShoppingCartDate> queryByOrderCodeList(List<String> shoppingCartOrderCodeList) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.in("orc.order_code",shoppingCartOrderCodeList);
        List<ShowShoppingCartDate> showShoppingCartDateList = this.baseMapper.selectByOrderCodeList(queryWrapper);
        return showShoppingCartDateList;
    }

    /**
     * 根据id 查询购物车 和 明细
     * @param id
     * @return
     */
    @Override
    public ShowShoppingCartDate queryShowShoppingCartDateById(String id) {
        return this.baseMapper.selectShowShoppingCartDateById(id);
    }

    @Override
    public void updateStateById(Long id, String state) {
        this.baseMapper.updateStateById(id,state);
    }

    @Override
    public List<ShowShoppingCartDate> queryMyShoppingCartUnpaid(Long userId, String state) {
        return this.baseMapper.selectMyShoppingCartUnpaid(userId,state);
    }

    /**
     * 更新购物车选中状态
     * @param orderCode
     * @param selected
     */
    @Override
    public void updateSelectedByOrderCode(String orderCode, String selected) {
        this.baseMapper.updateSelectedByOrderCode(orderCode,selected);
    }


}
