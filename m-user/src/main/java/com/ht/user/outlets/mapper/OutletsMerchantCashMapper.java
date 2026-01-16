package com.ht.user.outlets.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.outlets.entity.OutletsMerchantCash;
import com.ht.user.outlets.entity.OutletsOrders;
import com.ht.user.outlets.entity.RetSummaryMerchantCashPayData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

/**
 * <p>
 * 收银台商户对应 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
public interface OutletsMerchantCashMapper extends BaseMapper<OutletsMerchantCash> {


    @Select("SELECT ms.cash_id,ms.merch_name,\n" +
            "COALESCE((select sum(opt.amount) \n" +
            "\tfrom outlets_order_pay_trace opt \n" +
            "\twhere opt.cash_id = ms.cash_id \n" +
            "\t\tand (opt.state = 'paid' or opt.state = 'refund' or opt.state = 'cancel') \n" +
            "\t\tand opt.create_at >= #{startTime} and opt.create_at <= #{endTime} ),0) total_pay_amount,\n" +
            "COALESCE((select sum(orc.amount) \n" +
            "\tfrom outlets_order_refund_cancel orc \n" +
            "\twhere orc.cash_id = ms.cash_id \n" +
            "\t\tand orc.state = 'success' \n" +
            "\t\tand orc.create_at >= #{startTime} and orc.create_at <= #{endTime} ),0) total_refund_amount,\n" +
            "COALESCE((select sum(opt.fee) \n" +
            "\tfrom outlets_order_pay_trace opt \n" +
            "\twhere opt.cash_id = ms.cash_id \n" +
            "\t\tand (opt.state = 'paid' or opt.state = 'refund' or opt.state = 'cancel') \n" +
            "\t\tand opt.create_at >= #{startTime} and opt.create_at <= #{endTime} ),0) total_pay_fee_amount,\n" +
            "COALESCE((select sum(orrc.fee) \n" +
            "\tfrom outlets_order_refund_cancel orc left join outlets_order_ref_refund_cancel orrc on orrc.reqsn = orc.refund_cancel_code\n" +
            "\twhere orc.cash_id = ms.cash_id \n" +
            "\t\tand orc.state = 'success' \n" +
            "\t\tand orc.create_at >= #{startTime} and orc.create_at <= #{endTime} ),0) total_refund_fee_amount\n" +
            "\t\t\n" +
            "FROM `outlets_merchant_cash` ms  ${ew.customSqlSegment} \n" +
            "order by total_pay_amount desc")
    Page<RetSummaryMerchantCashPayData> summaryMerchantCashPayDataNotName(IPage<RetSummaryMerchantCashPayData> page,
                                                                          @Param("startTime") String startTime,
                                                                          @Param("endTime") String endTime,
                                                                          @Param(Constants.WRAPPER)QueryWrapper<RetSummaryMerchantCashPayData> queryWrapper);

}
