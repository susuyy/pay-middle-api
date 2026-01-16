package com.ht.user.card.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.admin.vo.OrdersVo;
import com.ht.user.card.entity.CardOrderDetailsVo;
import com.ht.user.card.entity.CardOrders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.user.card.entity.PrimeBuyCardOrderExcelVo;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单主表 Mapper 接口
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-22
 */
@Mapper
public interface CardOrdersMapper extends BaseMapper<CardOrders> {

    /**
     * 查询用户订单 根据类型查询
     * @param userId
     * @param merchantCode
     * @param type
     * @param state
     * @param iPage
     * @return
     */
    @Select({"<script>" +
            "SELECT * FROM card_orders " +
            "WHERE user_Id = #{userId} AND merchant_code=#{merchantCode} " +
            "<if test='type != \"all\" '> AND type = #{type} </if>" +
            "<if test='state != \"all\" '> AND state = #{state} </if>" +
            "ORDER BY create_at DESC" +
            "</script>"})
    List<CardOrders> listByUserIdAndMerchantCodeAndTypeAndState(@Param("userId") Long userId,
                                                                @Param("merchantCode")String merchantCode,
                                                                @Param("type") String type,
                                                                @Param("state")String state,
                                                                IPage<CardOrders> iPage);

    /**
     * 修改订单 状态
     * @param orderCode
     * @param state
     */
    @Update("UPDATE card_orders SET state = #{state} ,update_at = #{updateAt}  WHERE order_code = #{orderCode}")
    void updateStateByOrderCode(@Param("orderCode") String orderCode, @Param("state") String state, @Param("updateAt") Date updateAt);

    @Select("SELECT o.id,o.user_id,o.sale_id,o.amount,o.type,o.order_code,o.merchant_code,o.create_at FROM card_orders o \n" +
            "where o.type LIKE \"%admin%\" and o.merchant_code=#{merchantCode} and o.type=#{orderType}\n" +
            "GROUP BY o.id,o.user_id,o.amount,o.type,o.order_code,o.merchant_code,o.create_at")
    List<OrdersVo> getAdminOrderList(@Param("merchantCode") String merchantCode, @Param("orderType") String orderType, IPage<OrdersVo> page);

    @Select("SELECT\n" +
            "\to.order_code,\n" +
            "\to.user_id,\n" +
            "\to.merchant_code,\n" +
            "\topt.pos_serial_num,\n" +
            "\to.update_at,\n" +
            "\to.create_at,\n" +
            "\to.amount,\n" +
            "\tCASE o.type\n" +
            "\tWHEN 'admin_adjust' THEN '后台调账'\n" +
            "\tWHEN 'admin_recharge' THEN '后台充值'\n" +
            "\tWHEN \"shop\" THEN \"购物\"\n" +
            "\tWHEN \"consume\" THEN \"消费\"\n" +
            "\tWHEN \"qr_pay\" THEN \"二维码支付\"\n" +
            "\tWHEN \"pos_account_pay\" THEN \"POS机余额消费\"\n" +
            "\tWHEN \"pos_cash\" THEN \"POS机现金消费\"\n" +
            "\t\tELSE \"其他消费\"\n" +
            "END as type\n" +
            "FROM\n" +
            "\tcard_orders o\n" +
            "\tLEFT JOIN card_order_details od ON o.order_code = od.order_code\n" +
            "\tLEFT JOIN card_order_pay_trace opt ON o.order_code = opt.order_code\n" +
            " where o.merchant_code = #{merchantCode}")
    List<OrdersVo> getOrderList(@Param("merchantCode") String merchantCode,IPage<OrdersVo> page);

    @Update("UPDATE card_orders SET state = #{state} ,update_at = #{updateAt},user_id = #{userId}  WHERE order_code = #{orderCode}")
    void updateStateAndUserIdByOrderCode(@Param("orderCode")String orderCode,@Param("state") String state,@Param("userId") Long userId,@Param("updateAt") Date updateAt);

    @Override
    @Select("select o.id,o.order_code,o.order_code as detail_order_code,o.type,o.state,o.merchant_code,o.user_id,o.sale_id,o.quantity,o.amount," +
            "o.comments,o.discount,o.create_at,o.update_at from card_orders o ${ew.customSqlSegment}")
    @Result(property="orderDetailsList",
            javaType= List.class,
            column="detail_order_code",
            many=@Many(select="com.ht.user.card.mapper.CardOrderDetailsMapper.getOrderListByOrderCode"))
    @Result(property="payTraceList", javaType= List.class, column="detail_order_code",
            many=@Many(select="com.ht.user.card.mapper.CardOrderPayTraceMapper.getPayTraceByOrderCode"))
    <E extends IPage<CardOrders>> E selectPage(E page, @Param(Constants.WRAPPER) Wrapper<CardOrders> queryWrapper);


    @Select({
            "<script> select o.id,o.order_code as orderCode,o.order_code,o.type,o.state,o.merchant_code,o.user_id,o.sale_id,o.quantity,o.amount," +
            "o.comments,o.discount,o.create_at,o.update_at from card_orders o where 1=1 " +
            "<if test='orderNo != null and orderNo != &quot;&quot;'>" +
                "and o.order_code = #{orderNo} " +
            "</if>" +
            "<if test='startTime != null'>"+
                "and o.create_at &gt;= #{startTime} " +
            "</if>" +
            "<if test='endTime != null'>"+
                "and o.create_at &lt;= #{endTime} " +
            "</if>" +
            "<if test='state != null and state != &quot;&quot;'>"+
                "and o.state = #{state} " +
            "</if>" +
            "<if test='type != null and type != &quot;&quot;'>"+
                "and o.type = #{type} " +
            "</if>"
//                    +
//            "<if test='userId != null'>"+
//                "and o.user_id = #{userId} " +
//            "</if>"
                    +
                    "<if test='phone != null and phone != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where user_flag = #{phone} or user_flag = #{userId})" +
                    "</if>"
                    +
            "<if test='cardNo != null and cardNo != &quot;&quot; and type == \"prime_buy_card\" '>"+
                    "and o.order_code in (select order_code from card_order_details where LOCATE(#{cardNo},production_code)) " +
            "</if>" +
                    "<if test='cardNo != null and cardNo != &quot;&quot; and type == \"consume\" '>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where source_id = #{cardNo} ) " +
                    "</if>" +
            "<if test='traceNo != null and traceNo != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where pay_code = #{traceNo}) " +
            "</if>" +
                    " ORDER BY create_at DESC"+"</script>"
    })
    @Result(property="orderDetailsList",
            javaType= List.class,
            column="order_code",
            many=@Many(select="com.ht.user.card.mapper.CardOrderDetailsMapper.getOrderListByOrderCode"))
    @Result(property="payTraceList", javaType= List.class, column="order_code",
            many=@Many(select="com.ht.user.card.mapper.CardOrderPayTraceMapper.getPayTraceByOrderCode"))
    <E extends IPage<CardOrders>> E getOrderPage(E page,String orderNo,String startTime,String endTime,
                                                 String state,Long userId,String phone, String type, String cardNo,String traceNo);


    @Select({
            "<script> select o.id,o.order_code as orderCode,o.order_code,o.type,o.state,o.merchant_code,o.user_id,o.sale_id,o.quantity,o.amount," +
                    "o.comments,o.discount,o.create_at,o.update_at from card_orders o where 1=1 " +
                    "<if test='orderNo != null and orderNo != &quot;&quot;'>" +
                    "and o.order_code = #{orderNo} " +
                    "</if>" +
                    "<if test='startTime != null'>"+
                    "and o.create_at &gt;= #{startTime} " +
                    "</if>" +
                    "<if test='endTime != null'>"+
                    "and o.create_at &lt;= #{endTime} " +
                    "</if>" +
                    "<if test='state != null and state != &quot;&quot;'>"+
                    "and o.state = #{state} " +
                    "</if>" +
                    "<if test='type != null and type != &quot;&quot;'>"+
                    "and o.type = #{type} " +
                    "</if>" +
                    "<if test='phone != null and phone != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where user_flag = #{phone})" +
                    "</if>" +
                    "<if test='cardNo != null and cardNo != &quot;&quot; and type == \"prime_buy_card\" '>"+
                    "and o.order_code in (select order_code from card_order_details where LOCATE(#{cardNo},production_code)) " +
                    "</if>" +
                    "<if test='cardNo != null and cardNo != &quot;&quot; and type == \"consume\" '>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where source_id = #{cardNo} ) " +
                    "</if>" +
                    "<if test='traceNo != null and traceNo != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where pay_code = #{traceNo}) " +
                    "</if>" +
                    " ORDER BY create_at DESC"+"</script>"
    })
    @Result(property="orderDetailsList",
            javaType= List.class,
            column="order_code",
            many=@Many(select="com.ht.user.card.mapper.CardOrderDetailsMapper.getOrderListByOrderCode"))
    @Result(property="payTraceList", javaType= List.class, column="order_code",
            many=@Many(select="com.ht.user.card.mapper.CardOrderPayTraceMapper.getPayTraceByOrderCode"))
    <E extends IPage<CardOrders>> E getOrderPagePhone(E page,String orderNo,String startTime,String endTime,
                                                 String state,String phone, String type, String cardNo,String traceNo);

//
//    @Select({
//            "<script> select o.id,o.order_code as orderCode,o.order_code,o.type,o.state,o.merchant_code,o.user_id,o.sale_id,o.quantity,o.amount," +
//                    "o.comments,o.discount,o.create_at,o.update_at from card_orders o where 1=1 " +
//                    "<if test='orderNo != null and orderNo != &quot;&quot;'>" +
//                    "and o.order_code = #{orderNo} " +
//                    "</if>" +
//                    "<if test='startTime != null'>"+
//                    "and o.create_at &gt;= #{startTime} " +
//                    "</if>" +
//                    "<if test='endTime != null'>"+
//                    "and o.create_at &lt;= #{endTime} " +
//                    "</if>" +
//                    "<if test='state != null and state != &quot;&quot;'>"+
//                    "and o.state = #{state} " +
//                    "</if>" +
//                    "<if test='type != null and type != &quot;&quot;'>"+
//                    "and o.type = #{type} " +
//                    "</if>" +
//                    "<if test='userId != null'>"+
//                    "and o.userId = #{userId} " +
//                    "</if>" +
//                    "<if test='cardNo != null and cardNo != &quot;&quot;'>"+
//                    "and o.order_code in (select order_code from card_order_details where LOCATE(#{cardNo},production_code)) " +
//                    "</if>" +
//                    "<if test='traceNo != null and traceNo != &quot;&quot;'>"+
//                    "and o.order_code in (select order_code from card_order_pay_trace where pay_code = #{traceNo}) " +
//                    "</if>" +
//                    " ORDER BY create_at DESC"+"</script>"
//    })
//    @Result(property="orderDetailsList",
//            javaType= List.class,
//            column="order_code",
//            many=@Many(select="com.ht.user.card.mapper.CardOrderDetailsMapper.getOrderListByOrderCode"))
//    @Result(property="payTraceList", javaType= List.class, column="order_code",
//            many=@Many(select="com.ht.user.card.mapper.CardOrderPayTraceMapper.getPayTraceByOrderCode"))
//    List<CardOrders> selectConsumeOrdersExcelData(String orderNo, Date startTime, Date endTime, String state, Long userId, String type, String cardNo, String traceNo);

    @Select({
            "<script> select  @rownum := @rownum + 1 AS rowNum,o.order_code,o.amount,od.amount as detail_amount,od.state," +
                    "o.comments,o.create_at,o.user_id,od.production_code,o.create_at,od.quantity from card_orders o " +
                    "left join card_order_details od on o.order_code = od.order_code where 1=1 " +
                    "<if test='orderNo != null and orderNo != &quot;&quot;'>" +
                    "and o.order_code = #{orderNo} " +
                    "</if>" +
                    "<if test='startTime != null'>"+
                    "and o.create_at &gt;= #{startTime} " +
                    "</if>" +
                    "<if test='endTime != null'>"+
                    "and o.create_at &lt;= #{endTime} " +
                    "</if>" +
                    "<if test='state != null and state != &quot;&quot;'>"+
                    "and o.state = #{state} " +
                    "</if>" +
                    "<if test='type != null and type != &quot;&quot;'>"+
                    "and o.type = #{type} " +
                    "</if>"
//                    +
//                    "<if test='userId != null'>"+
//                    "and o.user_id = #{userId} " +
//                    "</if>" +

                    +
                            "<if test='phone != null and phone != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where user_flag = #{phone} or user_flag = #{userId})" +
                    "</if>"
                    +


                    "<if test='cardNo != null and cardNo != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_details where LOCATE(#{cardNo},production_code)) " +
                    "</if>" +
                    "<if test='traceNo != null and traceNo != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where pay_code = #{traceNo}) " +
                    "</if>" +
                    " ORDER BY create_at DESC"+"</script>"
    })
    List<PrimeBuyCardOrderExcelVo> getExcelList(String startTime,String endTime, String state, Long userId,String phone, String type, String cardNo, String traceNo, String orderNo);

    @Select({
            "<script> select  @rownum := @rownum + 1 AS rowNum,o.order_code,o.amount,od.amount as detail_amount,od.state," +
                    "o.comments,o.create_at,o.user_id,od.production_code,o.create_at,od.quantity from card_orders o " +
                    "left join card_order_details od on o.order_code = od.order_code where 1=1 " +
                    "<if test='orderNo != null and orderNo != &quot;&quot;'>" +
                    "and o.order_code = #{orderNo} " +
                    "</if>" +
                    "<if test='startTime != null'>"+
                    "and o.create_at &gt;= #{startTime} " +
                    "</if>" +
                    "<if test='endTime != null'>"+
                    "and o.create_at &lt;= #{endTime} " +
                    "</if>" +
                    "<if test='state != null and state != &quot;&quot;'>"+
                    "and o.state = #{state} " +
                    "</if>" +
                    "<if test='type != null and type != &quot;&quot;'>"+
                    "and o.type = #{type} " +
                    "</if>" +
                    "<if test='phone != null and phone != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where user_flag = #{phone}) " +
                    "</if>" +
                    "<if test='cardNo != null and cardNo != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_details where LOCATE(#{cardNo},production_code)) " +
                    "</if>" +
                    "<if test='traceNo != null and traceNo != &quot;&quot;'>"+
                    "and o.order_code in (select order_code from card_order_pay_trace where pay_code = #{traceNo}) " +
                    "</if>" +
                    " ORDER BY create_at DESC"+"</script>"
    })
    List<PrimeBuyCardOrderExcelVo> getExcelListPhone(String startTime,String endTime, String state, String phone, String type, String cardNo, String traceNo, String orderNo);

    @Select("select * from card_orders where type = 'prime_buy_card' AND state <> 'un_paid' AND order_code in (SELECT order_code FROM `card_order_pay_trace` where user_flag = #{userId} or user_flag = #{phoneNum} ) ORDER BY create_at DESC")
    Page<CardOrders> selectUserOrderListPage(Page<CardOrders> page, @Param("phoneNum") String phoneNum, @Param("userId")Long userId);
}
