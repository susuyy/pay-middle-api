package com.ht.user.mall.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.user.mall.entity.OrderShoppingCartDetails;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 订单明细 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-09-09
 */
public interface OrderShoppingCartDetailsMapper extends BaseMapper<OrderShoppingCartDetails> {

    /**
     * 根据order_code 修改state
     * @param orderCode
     * @param state
     */
    @Update("update order_shopping_cart_details set state = #{state} where order_code = #{orderCode}")
    void updateStateByOrderCode(@Param("orderCode") String orderCode, @Param("state") String state);

    /**
     * 更新数量
     * @param totalQuantity
     * @param orderCode
     */
    @Update("update order_shopping_cart_details set quantity = #{totalQuantity} where order_code = #{orderCode}")
    void updateQuantityByOrderCode(@Param("totalQuantity")Integer totalQuantity, @Param("orderCode") String orderCode);

    /**
     * 修改金额
     * @param newAmount
     * @param orderCode
     */
    @Update("update order_shopping_cart_details set amount = #{newAmount} where order_code = #{orderCode}")
    void updateAmountByOrderCode(@Param("newAmount") Integer newAmount,@Param("orderCode") String orderCode);

}
