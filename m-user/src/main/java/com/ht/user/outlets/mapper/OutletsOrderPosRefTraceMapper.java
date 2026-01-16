package com.ht.user.outlets.mapper;

import com.ht.user.outlets.entity.OutletsOrderPosRefTrace;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2021-11-22
 */
public interface OutletsOrderPosRefTraceMapper extends BaseMapper<OutletsOrderPosRefTrace> {

    @Select("select date_format(create_at,'%Y-%m-%d') as 'date', IFNULL(SUM(amount),0) as 'dayAmount'\n" +
            "from outlets_order_pos_ref_trace\n" +
            "where create_at >= date(now()) - interval 6 day AND rejcode = '00'\n" +
            "group by day(create_at)")
    List<Map<String, Object>> selectLastSevenDaysAmount();

}
