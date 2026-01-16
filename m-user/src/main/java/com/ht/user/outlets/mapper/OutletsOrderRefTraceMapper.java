package com.ht.user.outlets.mapper;

import com.ht.user.outlets.entity.OutletsOrderRefTrace;
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
 * @since 2021-11-16
 */
public interface OutletsOrderRefTraceMapper extends BaseMapper<OutletsOrderRefTrace> {

    @Select("select trxcode,date_format(create_at,'%Y-%m-%d') as 'date', IFNULL(SUM(trxamt),0) as 'dayAmount'\n" +
            "from outlets_order_ref_trace\n" +
            "where create_at >= date(now()) - interval 6 day AND trxstatus = '0000'\n" +
            "group by trxcode,day(create_at);\n")
    List<Map<String, Object>> selectLastSevenDaysAmount();

}
