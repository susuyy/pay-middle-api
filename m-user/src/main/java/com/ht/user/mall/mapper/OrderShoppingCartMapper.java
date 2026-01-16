package com.ht.user.mall.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.mall.entity.OrderShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.user.mall.entity.ShowShoppingCartDate;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

/**
 * <p>
 * 订单主表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-09-03
 */
public interface OrderShoppingCartMapper extends BaseMapper<OrderShoppingCart> {

    /**
     * 查询用户的购物车数据
     *
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code " +
            "WHERE orc.merchant_code = #{merchantCode} AND orc.state = #{state}  AND user_id = #{userId} ORDER BY orc.create_at DESC limit 5")
    List<ShowShoppingCartDate> selectShowShoppingCartDate(@Param("userId") Long userId, @Param("merchantCode") String merchantCode, @Param("state") String state);


    /**
     * 查询用户的购物车数据
     *
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code " +
            "WHERE orc.merchant_code = #{merchantCode} AND orc.state = #{state}  AND user_id = #{userId} ORDER BY orc.create_at DESC")
    List<ShowShoppingCartDate> selectShowShoppingCartDateNoLimit(@Param("userId") Long userId, @Param("merchantCode") String merchantCode, @Param("state") String state);

    /**
     * 点击 更多 获取 某个子商户(门店) 下的购物车数据 分页
     *
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Select("select osc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart osc LEFT JOIN order_shopping_cart_details oscd ON osc.order_code = oscd.order_code " +
            "WHERE osc.merchant_code = #{merchantCode} AND osc.state = #{state}  AND user_id = #{userId} ORDER BY osc.create_at DESC")
    IPage<ShowShoppingCartDate> selectShowShoppingCartDateMorePage(Page page,
                                                                   @Param("userId") Long userId,
                                                                   @Param("merchantCode") String merchantCode,
                                                                   @Param("state") String state);


    /**
     * 获取购物车失效宝贝
     *
     * @param page
     * @param userId
     * @param state
     * @return
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code " +
            "WHERE user_id = #{userId} AND orc.state = #{state} ORDER BY orc.create_at DESC")
    IPage<ShowShoppingCartDate> selectInvalidCart(Page<OrderShoppingCart> page, @Param("userId") Long userId, @Param("state") String state);

    /**
     * 按id 更新状态
     * @param id
     * @param state
     */
    @Update("update order_shopping_cart set state = #{state} where id = #{id}")
    void updateStateById(@Param("id") Long id,@Param("state") String state);

    /**
     * 按 商户 分页 分组
     * @param Page
     * @param userId
     * @param state
     * @return
     */
    @Select("select merchant_code from order_shopping_cart where user_id = #{userId} AND state = #{state} group by merchant_code ORDER BY MAX(create_at) DESC")
    IPage<OrderShoppingCart> selectGroupByMerchantPage(Page<OrderShoppingCart> Page, @Param("userId") Long userId, @Param("state") String state);

    /**
     * 获取购物车失效宝贝 不分页
     *
     * @param userId
     * @param state
     * @return
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code " +
            "WHERE user_id = #{userId} AND orc.state = #{state} ORDER BY orc.create_at DESC")
    List<ShowShoppingCartDate> selectInvalidCartNoPage( @Param("userId") String userId, @Param("state") String state);

    /**
     * 清空购物车失效商品
     * @param userId
     * @param state
     */
    @Delete("delete from order_shopping_cart where user_id = #{userId} AND state = #{state}")
    void delInvalidCart(@Param("userId") String userId, @Param("state") String state);

    /**
     * 查询按商户拆单数据
     * @param queryWrapper
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code ${ew.customSqlSegment}")
    List<ShowShoppingCartDate> selectByOrderCodeListAndMerchantCode(@Param(Constants.WRAPPER)QueryWrapper queryWrapper);

    /**
     * 查询购物车数据
     * @param queryWrapper
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code ${ew.customSqlSegment}")
    List<ShowShoppingCartDate> selectByOrderCodeList(@Param(Constants.WRAPPER)QueryWrapper queryWrapper);

    /**
     * 根据id 查询购物车和明细数据
     * @param id
     * @return
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code WHERE orc.id = #{id}")
    ShowShoppingCartDate selectShowShoppingCartDateById(@Param("id") String id);

    /**
     * 查询用户 购物车 和明细 封装数据
     * @param userId
     * @param state
     * @return
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code WHERE orc.user_id = #{userId} AND orc.state = #{state}")
    List<ShowShoppingCartDate> selectMyShoppingCartUnpaid(@Param("userId") Long userId, @Param("state") String state);

    /**
     * 查询用户的购物车数据 分页
     * @param sonPage
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code " +
            "WHERE orc.merchant_code = #{merchantCode} AND orc.state = #{state}  AND user_id = #{userId} ORDER BY orc.create_at DESC")
    Page<ShowShoppingCartDate> selectShowShoppingCartDatePage(Page<ShowShoppingCartDate> sonPage,
                                                              @Param("userId") Long userId,
                                                              @Param("merchantCode") String merchantCode,
                                                              @Param("state") String state);

    /**
     * 记录购物车选中状态
     * @param orderCode
     * @param selected
     */
    @Update("update order_shopping_cart set selected = #{selected} where order_code = #{orderCode}")
    void updateSelectedByOrderCode(@Param("orderCode") String orderCode, @Param("selected") String selected);

    /**
     * 查询购物车数据
     * @param productionCode
     * @param userId
     * @param merchantCode
     * @return
     */
    @Select("select orc.* , oscd.production_code, oscd.production_name, oscd.production_category_code, oscd.production_category_name , oscd.activity_code " +
            "from  order_shopping_cart orc LEFT JOIN order_shopping_cart_details oscd ON orc.order_code = oscd.order_code " +
            "WHERE orc.merchant_code = #{merchantCode}  AND orc.user_id = #{userId} AND oscd.production_code = #{productionCode}")
    ShowShoppingCartDate selectUserProductionCodeAndMerchantCode(@Param("productionCode")String productionCode,@Param("userId") Long userId, @Param("merchantCode") String merchantCode);

    /**
     * 更新数量 购物车
     * @param totalQuantity
     * @param orderCode
     */
    @Update("UPDATE order_shopping_cart SET quantity = #{totalQuantity} where order_code = #{orderCode}")
    void updateQuantityByOrderCode(@Param("totalQuantity") Integer totalQuantity, @Param("orderCode") String orderCode);

    /**
     * 修改金额
     * @param newAmount
     * @param orderCode
     */
    @Update("UPDATE order_shopping_cart SET amount = #{newAmount} where order_code = #{orderCode}")
    void updateAmountByOrderCode(@Param("newAmount") Integer newAmount, @Param("orderCode") String orderCode);

}
