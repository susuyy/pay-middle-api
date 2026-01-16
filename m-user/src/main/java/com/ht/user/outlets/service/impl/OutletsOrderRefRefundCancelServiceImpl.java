package com.ht.user.outlets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.user.outlets.entity.OutletsOrderRefRefundCancel;
import com.ht.user.outlets.entity.OutletsOrderRefundCancel;
import com.ht.user.outlets.mapper.OutletsOrderRefRefundCancelMapper;
import com.ht.user.outlets.service.IOutletsOrderRefRefundCancelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-17
 */
@Service
public class OutletsOrderRefRefundCancelServiceImpl extends ServiceImpl<OutletsOrderRefRefundCancelMapper, OutletsOrderRefRefundCancel> implements IOutletsOrderRefRefundCancelService {

    @Override
    public List<Map<String, Object>> countRefundFeeByServiceType(String startCreateAt, String endCreateAt) {

        QueryWrapper<OutletsOrderRefRefundCancel> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("service_type as 'serviceType',IFNULL(sum(fee),0) as 'refundFeeSum'")
                .between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt),"create_at", startCreateAt, endCreateAt)
                .groupBy("service_type");

        return this.listMaps(queryWrapper);

    }

    @Override
    public List<Map<String, Object>> countRefundFeeByTrxcode(String startCreateAt, String endCreateAt) {

//        QueryWrapper<OutletsOrderRefRefundCancel> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("trxcode as 'trxcode',IFNULL(sum(fee),0) as 'refundFeeSum'")
//                .between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt),"create_at", startCreateAt, endCreateAt)
//                .groupBy("trxcode");
//
//        return this.listMaps(queryWrapper);
        return this.baseMapper.countRefundFeeByTrxcode(startCreateAt,endCreateAt);
    }

    @Override
    public List<OutletsOrderRefRefundCancel> queryFeeNullPosTypeData() {
        QueryWrapper<OutletsOrderRefRefundCancel> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("trxstatus","0000");
        queryWrapper.eq("service_type","pos");
        queryWrapper.isNull("fee");
        return list(queryWrapper);
    }

    @Override
    public OutletsOrderRefRefundCancel getByReqsn(String reqsn) {
        return this.baseMapper.getByReqsn(reqsn);
    }
}
