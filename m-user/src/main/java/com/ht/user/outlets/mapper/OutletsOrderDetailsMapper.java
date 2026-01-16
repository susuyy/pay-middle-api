package com.ht.user.outlets.mapper;

import com.ht.user.outlets.entity.OutletsOrderDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

/**
 * <p>
 * 订单明细 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
public interface OutletsOrderDetailsMapper extends BaseMapper<OutletsOrderDetails> {


    @Update("update outlets_order_details set state = #{state} , update_at = #{date} where order_code = #{orderCode}")
    void updateStateByOrderCode(@Param("orderCode") String orderCode, @Param("state") String state, @Param("date") Date date);
}
