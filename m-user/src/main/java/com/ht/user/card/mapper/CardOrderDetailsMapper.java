package com.ht.user.card.mapper;

import com.ht.user.card.entity.CardOrderDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.user.mall.entity.OrderOrderDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单明细 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Mapper
public interface CardOrderDetailsMapper extends BaseMapper<CardOrderDetails> {

    /**
     * 修改订单明细状态
     * @param orderCode
     * @param state
     * @param date
     */
    @Update("UPDATE card_order_details SET state = #{state} , update_at = #{date} WHERE order_code = #{orderCode}")
    void updateStateByOrderCode(@Param("orderCode") String orderCode, @Param("state") String state,@Param("date") Date date);

    /**
     * 通过orderCode查找detail
     * @param orderCode
     * @return
     */
    @Select("SELECT od.id,od.order_code,od.merchant_code,od.quantity,od.amount,od.production_code," +
            "od.production_name,od.production_category_code,od.production_category_name," +
            "od.batch_code,state,od.type,od.disccount,od.create_at,od.update_at " +
            "FROM card_order_details od where od.order_code = #{orderCode}")
    List<OrderOrderDetails> getOrderListByOrderCode(@PathParam("orderCode") String orderCode);
}
