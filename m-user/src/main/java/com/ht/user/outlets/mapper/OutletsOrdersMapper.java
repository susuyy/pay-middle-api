package com.ht.user.outlets.mapper;

import com.ht.user.outlets.entity.OutletsOrderPayTrace;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ht.user.outlets.entity.OutletsOrders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.user.outlets.vo.OutletsOrdersVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单主表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
public interface OutletsOrdersMapper extends BaseMapper<OutletsOrders> {

    @Update("update outlets_orders set state = #{state} , update_at = #{date} where order_code = #{orderCode}")
    void updateStateByOrderCode(@Param("orderCode") String orderCode,@Param("state") String state,@Param("date") Date date);

    @Update("update outlets_orders set state = #{state} , update_at = #{date} , channel_api = #{channelApi} where order_code = #{orderCode}")
    void updateStateChannelByOrderCode(@Param("orderCode") String orderCode,@Param("state") String state,@Param("date") Date date, @Param("channelApi")String channelApi);

    @Select("select * from outlets_orders as oo\n" +
            "left join outlets_order_pay_trace as oopt on oo.order_code = oopt.order_code\n" +
            "${ew.customSqlSegment}")
    IPage<OutletsOrders> findPageLeftJoinPayTrace(IPage<OutletsOrders> page, @Param(Constants.WRAPPER) Wrapper<OutletsOrders> wrapper);

}
