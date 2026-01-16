package com.ht.user.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.mall.entity.OrderOrderDetails;
import com.ht.user.mall.entity.OrderOrders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 订单主表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
public interface OrderOrdersMapper extends BaseMapper<OrderOrders> {


    /**
     * 修改订单主表 折扣数据
     * @param orderCode
     * @param discount
     */
    @Update("UPDATE order_orders SET discount = #{discount} WHERE order_code = #{orderCode} ")
    void updateDiscountByOrderCode(@Param("orderCode") String orderCode,@Param("discount") Integer discount);

    /**
     * 查询订单 全部 分页 不根据state筛选
     * @param page
     * @param userId
     * @return
     */
    @Select("select * from order_orders where user_id = #{userId} ORDER BY create_at DESC")
    IPage<OrderOrders> selectPageNoState(Page<OrderOrderDetails> page, @Param("userId") Long userId);

    /**
     * 查询用户的订单 不根据state筛选
     *
     * @param userId
     * @param merchantCode
     * @return
     */
    @Select("select * from order_orders WHERE merchant_code = #{merchantCode} AND user_id = #{userId} ORDER BY create_at DESC limit 5")
    List<OrderOrders> selectByUserIdMerchant(@Param("userId") Long userId, @Param("merchantCode") String merchantCode);

    /**
     * 按 商户 分页 分组 根据 state筛选
     * @param page
     * @param userId
     * @param state
     * @return
     */
    @Select("select * from order_orders where user_id = #{userId} AND state = #{state}  ORDER BY create_at DESC")
    IPage<OrderOrders> selectPageAndState(Page<OrderOrderDetails> page,@Param("userId") Long userId, @Param("state")String state);


    /**
     * 查询用户的订单  根据state筛选
     *
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Select("select * from order_orders WHERE merchant_code = #{merchantCode} AND user_id = #{userId} AND state = #{state} ORDER BY create_at DESC limit 5")
    List<OrderOrders> selectByUserIdMerchantState(@Param("userId") Long userId, @Param("merchantCode") String merchantCode,@Param("state") String state);
}
