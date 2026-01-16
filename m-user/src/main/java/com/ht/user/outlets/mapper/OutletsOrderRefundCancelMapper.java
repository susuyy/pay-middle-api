package com.ht.user.outlets.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ht.user.outlets.entity.OutletsOrderRefundCancel;
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
public interface OutletsOrderRefundCancelMapper extends BaseMapper<OutletsOrderRefundCancel> {

    @Select("select IFNULL(sum(oorc.amount),0) as 'refundAmountSum',IFNULL(sum(fee),0) as 'refundFeeSum' from outlets_order_refund_cancel as oorc\n" +
            "left join outlets_order_ref_refund_cancel as oorrc on oorc.refund_cancel_code = oorrc.reqsn\n" +
            "${ew.customSqlSegment}")
    Map<String, Object> countRefundAmount(@Param(Constants.WRAPPER) Wrapper<OutletsOrderRefundCancel> wrapper);

}
