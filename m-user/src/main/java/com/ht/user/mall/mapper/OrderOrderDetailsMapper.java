package com.ht.user.mall.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.mall.entity.OrderOrderDetails;
import com.ht.user.mall.entity.OrderShoppingCart;
import com.ht.user.mall.entity.ShowShoppingCartDate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * <p>
 * 订单明细 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
public interface OrderOrderDetailsMapper extends BaseMapper<OrderOrderDetails> {

    /**
     * 按 商户 分页 分组 查询全部 不根据state筛选
     * @param page
     * @param userId
     * @return
     */
    @Select("select * from order_order_details where user_id = #{userId} ORDER BY create_at DESC")
    IPage<OrderOrderDetails> selectPageNoState(Page<OrderOrderDetails> page,@Param("userId")Long userId);

    /**
     * 按 商户 分页 分组 根据 state筛选
     * @param page
     * @param userId
     * @param state
     * @return
     */
    @Select("select * from order_order_details where user_id = #{userId} AND state = #{state}  ORDER BY create_at DESC")
    IPage<OrderOrderDetails> selectPageAndState(Page<OrderOrderDetails> page, @Param("userId") Long userId, @Param("state") String state);


    /**
     * 查询用户的订单 详情数据 根据state筛选
     *
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Select("select * from order_order_details WHERE merchant_code = #{merchantCode} AND user_id = #{userId} AND state = #{state} ORDER BY create_at DESC limit 5")
    List<OrderOrderDetails> selectByUserIdMerchantState(@Param("userId") Long userId, @Param("merchantCode") String merchantCode, @Param("state") String state);


    /**
     * 查询用户的订单 详情数据 不根据state筛选
     *
     * @param userId
     * @param merchantCode
     * @return
     */
    @Select("select * from order_order_details WHERE merchant_code = #{merchantCode} AND user_id = #{userId} ORDER BY create_at DESC limit 5")
    List<OrderOrderDetails> selectByUserIdMerchant(@Param("userId") Long userId, @Param("merchantCode") String merchantCode);

    /**
     * 根据id 修改折扣金额
     * @param id
     * @param oneDetailDiscount
     */
    @Update("UPDATE order_order_details SET discount = #{oneDetailDiscount}  where id = #{id}")
    void updateDiscountById(@Param("id") Long id,@Param("oneDetailDiscount") Integer oneDetailDiscount);

    /**
     * 根据id 修改订单明细状态
     * @param orderDetailId
     * @param state
     */
    @Update("UPDATE order_order_details SET state = #{state}  where id = #{orderDetailId}")
    void updateStateById(@Param("orderDetailId") String orderDetailId, @Param("state") String state);
}
