package com.ht.user.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.mall.entity.AddShoppingCartDate;
import com.ht.user.mall.entity.OrderShoppingCart;
import com.ht.user.mall.entity.RetPageCartData;
import com.ht.user.mall.entity.ShowShoppingCartDate;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 订单主表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-09
 */
public interface OrderShoppingCartService extends IService<OrderShoppingCart> {

    /**
     * 用户 提交商品 信息 添加购物车
     * @param addShoppingCartDate
     */
    void addShoppingCartDate(AddShoppingCartDate addShoppingCartDate);

    /**
     * 添加购物车主表 数据
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
    void addOrderShoppingCart(String orderCode,
                              Integer discount,
                              String merchantCode,
                              BigDecimal quantity,
                              Integer amount,
                              String type,
                              String state,
                              Long userId,
                              String comments,
                              String selected);

    /**
     * 按商户分页 查询购物车数据
     * @param merchantCode
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    RetPageCartData showShoppingCart(String merchantCode, Long userId, Integer pageNo, Integer pageSize);

    /**
     * 查询购物车 失效商品
     * @param userId
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<ShowShoppingCartDate> invalidCart(Long userId, String state, Integer pageNo, Integer pageSize);

    /**
     * 查询用户总 商品数
     * @param userId
     * @return
     */
    Integer productionsCount(String userId);

    /**
     * 删除 购物车 失效商品 与 购物车失效商品明细
     * @param userId
     */
    void delInvalidCart(String userId);

    /**
     * 获取某个商户下的更多购物车 数据
     * @param merchantCode
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<ShowShoppingCartDate> moreShoppingCart(String merchantCode, Long userId, Integer pageNo, Integer pageSize);

    /**
     * 根据 orderCode 查询购物车信息
     * @param shoppingCartOrderCode
     * @return
     */
    OrderShoppingCart queryByOrderCode(String shoppingCartOrderCode);

    /**
     * 查询购物车 数据 根据 orderCode,merchantCode
     * @param shoppingCartOrderCodeList
     * @param merchantCode
     * @return
     */
    List<ShowShoppingCartDate> queryByOrderCodeListAndMerchantCode(List<String> shoppingCartOrderCodeList, String merchantCode);

    /**
     * 根据orderCode删除购物车数据
     * @param orderCode
     */
    void delByOrderCode(String orderCode);

    /**
     * 根据orderCode 集合 查询购物车集合
     * @param shoppingCartOrderCodeList
     * @return
     */
    List<ShowShoppingCartDate> queryByOrderCodeList(List<String> shoppingCartOrderCodeList);

    /**
     * 根据id 查询购物车 和明细数据 封装
     * @param id
     * @return
     */
    ShowShoppingCartDate queryShowShoppingCartDateById(String id);

    /**
     * 根据id  修改 主表和明细表的状态
     * @param id
     * @param state
     */
    void updateStateById(Long id, String state);

    /**
     * 查询用户未支付购物车
     * @param userId
     * @param state
     * @return
     */
    List<ShowShoppingCartDate> queryMyShoppingCartUnpaid(Long userId, String state);

    /**
     * 更新 购物车选中状态
     * @param orderCode
     * @param selected
     */
    void updateSelectedByOrderCode(String orderCode, String selected);
}
