package com.ht.user.card.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.CardOrderPayTrace;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.user.card.excel.ConsumeCardOrderExcelVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单支付流水 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Mapper
public interface CardOrderPayTraceMapper extends BaseMapper<CardOrderPayTrace> {


    /**
     * 根据pos机串号查询消费流水记录
     * @param posSerialNum
     * @return
     */
    @Select("select * from card_order_pay_trace where pos_serial_num = #{posSerialNum}")
    List<CardOrderPayTrace> selectListByPosSerialNum(@Param("posSerialNum") String posSerialNum);

    /**
     * 修改 支付流水状态
     * @param orderCode
     * @param state
     * @param date
     * @param payCode
     */
    @Update("UPDATE card_order_pay_trace SET state = #{state} , update_at = #{date} , pay_code = #{payCode} WHERE order_code = #{orderCode} AND (pay_code is null OR pay_code = '')")
    void updateStateByOrderCode(@Param("orderCode") String orderCode, @Param("state") String state, @Param("date") Date date, @Param("payCode") String payCode);

    @Select("SELECT * FROM  card_order_pay_trace WHERE order_code = #{orderCode} and source_id = #{sourceId}")
    CardOrderPayTrace selectByOrderCodeAndSourceId(@Param("orderCode") String orderCode, @Param("sourceId") String sourceId);

    @Select("SELECT * FROM  card_order_pay_trace WHERE order_code = #{orderCode}")
    List<CardOrderPayTrace> getPayTraceByOrderCode(@Param("orderCode") String orderCode);

    @Update("UPDATE card_order_pay_trace SET state = #{state} , update_at = #{date} WHERE order_code = #{orderCode} ")
    void updateStateByOrderCodeNotPayCode(@Param("orderCode") String orderCode, @Param("state") String state, @Param("date") Date date);

    @Update("UPDATE card_order_pay_trace SET state = #{state} , update_at = #{date} , pay_code = #{payCode} WHERE order_code = #{orderCode} ")
    void updateStateAndPayCodeByOrderCode(@Param("orderCode") String orderCode, @Param("state") String state, @Param("date") Date date,@Param("payCode")String payCode);

    @Select("SELECT * FROM card_order_pay_trace WHERE ( (user_flag = #{userFlag} OR user_phone = #{userPhone}) AND type = #{type} AND amount > 0 AND (state = 'paid' OR state = 'refund' OR state = 'cancel') ) ORDER BY create_at DESC")
    Page<CardOrderPayTrace> selectUserPayTrace(Page<CardOrderPayTrace> page,@Param("userFlag") String userFlag,@Param("userPhone") String userPhone,@Param("type") String type);

    @Select("SELECT order_code,pay_code," +
            "(select amount/100 FROM card_orders where order_code = copt.order_code) orderTotalAmount," +
            "(amount/100) receiveAmount,(amount/100) detailAmount,state,create_at,source_id," +
            "(select face_value/100 FROM `m-prime`.card_electronic where card_no = copt.source_id) cardFaceValue, " +
            "ref_batch_code,ref_card_name,ref_card_type,user_phone,source,merch_id " +
            "FROM `card_order_pay_trace` copt where ref_batch_code = #{batchCode} ")
    List<ConsumeCardOrderExcelVo> batchConsumeExcel(@Param("batchCode") String batchCode);

}
