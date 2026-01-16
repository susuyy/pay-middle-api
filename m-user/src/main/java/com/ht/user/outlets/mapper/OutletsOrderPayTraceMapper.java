package com.ht.user.outlets.mapper;

import com.ht.user.outlets.entity.OutletsOrderPayTrace;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单支付流水 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
public interface OutletsOrderPayTraceMapper extends BaseMapper<OutletsOrderPayTrace> {

    @Update("update outlets_order_pay_trace set state = #{state} , update_at = #{date} where order_code = #{orderCode}")
    void updateStateByOrderCode(@Param("orderCode") String orderCode, @Param("state") String state, @Param("date") Date date);

    @Update("update outlets_order_pay_trace set state = #{state} , update_at = #{date} , refund_amount = refund_amount + #{refundAmount} where order_code = #{orderCode}")
    void updateStateRefundAmountByOrderCode(@Param("orderCode") String orderCode, @Param("state") String state, @Param("date") Date date,@Param("refundAmount") long refundAmount);

    @Update("update outlets_order_pay_trace set pay_code = #{payCode} , pay_time = #{payTime} ,fee = #{fee} where order_code = #{orderCode}")
    void updatePayCodeByOrderCode(@Param("payCode")String payCode,
                                  @Param("orderCode")String orderCode,
                                  @Param("payTime")String payTime,
                                  @Param("fee")Integer fee);

    @Select("select date_format(create_at,'%Y-%m') as 'month', IFNULL(SUM(amount),0) as 'monthAmount'\n" +
            "from outlets_order_pay_trace\n" +
            "where create_at >= date(now()) - interval 3 month AND date_format(create_at,'%Y-%m') <> date_format(date(now()) - interval 3 month,'%Y-%m') and state = 'paid'\n" +
            "group by month(create_at)\n" +
            "order by create_at desc")
    List<Map<String, Object>> selectLastThreeMonthsAmount();


    @Select("select date_format(create_at,'%Y-%m-%d') as 'date', IFNULL(SUM(amount),0) as 'dayAmount'\n" +
            "from outlets_order_pay_trace\n" +
            "where create_at >= date(now()) - interval 6 day and state = 'paid' \n" +
            "group by day(create_at)\n" +
            "order by create_at desc")
    List<Map<String, Object>> selectLastSevenDaysAmount();

    @Select("select * from outlets_order_pay_trace where cash_id = #{cashId} and type = 'pos_mis_order' and (state = 'unpaid' or state = 'close') order by create_at desc limit 1 ")
    List<OutletsOrderPayTrace> checkHaveOrder(@Param("cashId")String cashId);


    @Select("select * from outlets_order_pay_trace where pay_code = #{payTraceNo} or ref_trace_no = #{payTraceNo} order by create_at desc limit 1 ")
    OutletsOrderPayTrace selectByRefTraceNoOrPayCode(@Param("payTraceNo") String payTraceNo);

}
