package com.ht.user.outlets.mapper;

import com.ht.user.outlets.entity.OutletsOrderRefRefundCancel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2021-11-17
 */
public interface OutletsOrderRefRefundCancelMapper extends BaseMapper<OutletsOrderRefRefundCancel> {

    @Select("select oorc.pay_trxcode as trxcode,IFNULL(sum(oorrc.fee),0) as refundFeeSum from outlets_order_refund_cancel oorc left join outlets_order_ref_refund_cancel oorrc on oorc.refund_cancel_code = oorrc.reqsn  where oorc.create_at >= #{startCreateAt} and oorc.create_at <= #{endCreateAt} GROUP BY oorc.pay_trxcode")
    List<Map<String, Object>> countRefundFeeByTrxcode(@Param("startCreateAt") String startCreateAt,@Param("endCreateAt") String endCreateAt);

    @Select("select * from outlets_order_ref_refund_cancel where reqsn = #{reqsn}")
    OutletsOrderRefRefundCancel getByReqsn(@Param("reqsn") String reqsn);
}
