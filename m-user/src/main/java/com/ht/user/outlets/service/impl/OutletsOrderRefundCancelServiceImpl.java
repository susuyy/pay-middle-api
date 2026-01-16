package com.ht.user.outlets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.config.CardOrderPayTraceStateConfig;
import com.ht.user.config.CardOrderPayTraceTypeConfig;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.excel.OutletsOrderPayTraceExcelVO;
import com.ht.user.outlets.excel.OutletsOrderRefundCancelExcelVO;
import com.ht.user.outlets.mapper.OutletsOrderRefundCancelMapper;
import com.ht.user.outlets.service.IOutletsOrderRefRefundCancelService;
import com.ht.user.outlets.service.IOutletsOrderRefundCancelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.outlets.util.MoneyUtils;
import com.ht.user.outlets.util.StringGeneralUtil;
import com.ht.user.outlets.vo.OutletsOrderPayTraceVO;
import com.ht.user.outlets.vo.OutletsOrderRefundCancelVO;
import com.ht.user.outlets.vo.OutletsOrdersVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
public class OutletsOrderRefundCancelServiceImpl extends ServiceImpl<OutletsOrderRefundCancelMapper, OutletsOrderRefundCancel> implements IOutletsOrderRefundCancelService {

    @Autowired
    private IOutletsOrderRefRefundCancelService outletsOrderRefRefundCancelService;

    @Override
    public List<OutletsOrderRefundCancel> queryRefundCancelData(SearchTraceData searchTraceData) {
        LambdaQueryWrapper<OutletsOrderRefundCancel> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        if (StringGeneralUtil.checkNotNull(searchTraceData.getOrderCode())){
            lambdaQueryWrapper.eq(OutletsOrderRefundCancel::getOriOrderCode,searchTraceData.getOrderCode());
        }
        if (StringGeneralUtil.checkNotNull(searchTraceData.getStoreCode())){
            lambdaQueryWrapper.eq(OutletsOrderRefundCancel::getMerchantCode,searchTraceData.getStoreCode());
        }
        if (StringGeneralUtil.checkNotNull(searchTraceData.getRefundCancelCode())){
            lambdaQueryWrapper.eq(OutletsOrderRefundCancel::getRefundCancelCode,searchTraceData.getRefundCancelCode());
        }
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public IPage<OutletsOrderRefundCancelVO> findPage(IPage<OutletsOrderRefundCancel> page, Map<String, String> paramsMap) {

        String refundCancelCode = paramsMap.get("refundCancelCode");
        String oriOrderCode = paramsMap.get("oriOrderCode");
        String state = paramsMap.get("state");
        String startCreateAt = paramsMap.get("startCreateAt");
        String endCreateAt = paramsMap.get("endCreateAt");

        if (StringGeneralUtil.checkNotNull(startCreateAt)) {
            startCreateAt +=  " 00:00:01";
        }
        if (StringGeneralUtil.checkNotNull(endCreateAt)) {
            endCreateAt += " 23:59:59";
        }

        LambdaQueryWrapper<OutletsOrderRefundCancel> lambda = new QueryWrapper<OutletsOrderRefundCancel>().lambda();
        lambda.eq(!StringUtils.isEmpty(refundCancelCode), OutletsOrderRefundCancel::getRefundCancelCode, refundCancelCode);
        lambda.eq(!StringUtils.isEmpty(oriOrderCode), OutletsOrderRefundCancel::getOriOrderCode, oriOrderCode);
        lambda.eq(!StringUtils.isEmpty(state), OutletsOrderRefundCancel::getState, state);
        lambda.between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt), OutletsOrderRefundCancel::getCreateAt, startCreateAt, endCreateAt);
        lambda.orderByDesc(OutletsOrderRefundCancel::getId);

        IPage<OutletsOrderRefundCancel> result = this.page(page, lambda);
        return transferToPageDataVo(result);
    }

    public IPage<OutletsOrderRefundCancelVO> transferToPageDataVo(IPage<OutletsOrderRefundCancel> resultPage) {

        IPage<OutletsOrderRefundCancelVO> outletsOrderRefundCancelIPage = new Page<>();
        List<OutletsOrderRefundCancelVO> recordVos = new ArrayList<>();
        outletsOrderRefundCancelIPage.setPages(resultPage.getPages());
        outletsOrderRefundCancelIPage.setTotal(resultPage.getTotal());
        outletsOrderRefundCancelIPage.setSize(resultPage.getSize());
        outletsOrderRefundCancelIPage.setCurrent(resultPage.getCurrent());
        outletsOrderRefundCancelIPage.setRecords(recordVos);
        if(null != resultPage && resultPage.getRecords().size()>0){
            List<OutletsOrderRefundCancel> records = resultPage.getRecords();
            for (int i = 0; i < records.size(); i++) {
                OutletsOrderRefundCancel record = records.get(i);
                OutletsOrderRefundCancelVO recordDataVo = new OutletsOrderRefundCancelVO();
                BeanUtils.copyProperties(record,recordDataVo);
                if (null != record.getAmount()) {
                    String amount = String.valueOf(record.getAmount());
                    recordDataVo.setAmount(MoneyUtils.changeF2YBigDecimal(Integer.valueOf(amount)));
                }
                recordVos.add(recordDataVo);
            }
        }
        return outletsOrderRefundCancelIPage;

    }

    @Override
    public List<OutletsOrderRefundCancelVO> findlist(Map<String, String> paramsMap) {

        String startCreateAt = paramsMap.get("startCreateAt");
        String endCreateAt = paramsMap.get("endCreateAt");
        String cashId = paramsMap.get("cashId");
        String startTime = paramsMap.get("startTime");
        String endTime = paramsMap.get("endTime");

        LambdaQueryWrapper<OutletsOrderRefundCancel> lambda = new QueryWrapper<OutletsOrderRefundCancel>().lambda();
        lambda.eq(!StringUtils.isEmpty(cashId), OutletsOrderRefundCancel::getCashId, cashId);
        lambda.between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt), OutletsOrderRefundCancel::getCreateAt, startCreateAt, endCreateAt);
        lambda.between(!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime), OutletsOrderRefundCancel::getCreateAt, startTime, endTime);
        lambda.orderByDesc(OutletsOrderRefundCancel::getId);

        List<OutletsOrderRefundCancel> result = this.list(lambda);
        return transferToListDataVo(result);
    }

    public List<OutletsOrderRefundCancelVO> transferToListDataVo(List<OutletsOrderRefundCancel> resultList) {

        List<OutletsOrderRefundCancelVO> recordVos = new ArrayList<>();

        for (OutletsOrderRefundCancel record : resultList) {
            OutletsOrderRefundCancelVO recordDataVo = new OutletsOrderRefundCancelVO();
            BeanUtils.copyProperties(record,recordDataVo);
            if (null != record.getAmount()) {
                String amount = String.valueOf(record.getAmount());
                recordDataVo.setAmount(MoneyUtils.changeF2YBigDecimal(Integer.valueOf(amount)));
            }
            recordVos.add(recordDataVo);
        }

        return recordVos;

    }

    @Override
    public List<OutletsOrderRefundCancelExcelVO> packageOutletsOrderRefundCancel(List<OutletsOrderRefundCancelVO> result) {
        List<OutletsOrderRefundCancelExcelVO> list = new ArrayList<>();

        for (OutletsOrderRefundCancelVO outletsOrderRefundCancelVO : result) {
            OutletsOrderRefundCancelExcelVO outletsOrderRefundCancelExcelVO = new OutletsOrderRefundCancelExcelVO();
            OutletsOrderRefRefundCancel outletsOrderRefRefundCancel = outletsOrderRefRefundCancelService.getByReqsn(outletsOrderRefundCancelVO.getRefundCancelCode());
            BeanUtils.copyProperties(outletsOrderRefundCancelVO, outletsOrderRefundCancelExcelVO);
            String state = "success".equals(outletsOrderRefundCancelVO.getState())?"成功":"失败";
            String serviceType = "";
            String sybTrxid = "";
            String outtrxid = "";
            String trxid = "";

            if (null != outletsOrderRefRefundCancel) {
                outtrxid = outletsOrderRefRefundCancel.getOuttrxid();
                trxid = outletsOrderRefRefundCancel.getTrxid();
            }

            if ("pos".equals(outletsOrderRefundCancelVO.getServiceType())) {
                serviceType = "pos机业务";
                sybTrxid = outtrxid;
            } else if ("qrCode".equals(outletsOrderRefundCancelVO.getServiceType())) {
                serviceType = "扫码业务";
                sybTrxid = trxid;
            }
            outletsOrderRefundCancelExcelVO.setState(state);
            outletsOrderRefundCancelExcelVO.setServiceType(serviceType);
            outletsOrderRefundCancelExcelVO.setSybTrxid(sybTrxid);
            switch (outletsOrderRefundCancelVO.getBusinessType()) {
                case CardOrderPayTraceStateConfig.REFUND:
                    outletsOrderRefundCancelExcelVO.setBusinessType("已退款");
                    break;
                case CardOrderPayTraceStateConfig.CANCEL:
                    outletsOrderRefundCancelExcelVO.setBusinessType("已撤销");
                    break;
                default:
                    outletsOrderRefundCancelExcelVO.setBusinessType("");
            }
            list.add(outletsOrderRefundCancelExcelVO);
        }

        return list;

    }

    @Override
    public List<Map<String, Object>> countRefundAmountByServiceType(String startCreateAt, String endCreateAt) {

        QueryWrapper<OutletsOrderRefundCancel> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("service_type as 'serviceType',IFNULL(sum(amount),0) as 'refundAmountSum'")
                .between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt),"create_at", startCreateAt, endCreateAt)
                .groupBy("service_type");

        return this.listMaps(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> countRefundAmountByPayTrxcode(String startCreateAt, String endCreateAt) {

        QueryWrapper<OutletsOrderRefundCancel> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("pay_trxcode as 'payTrxcode',IFNULL(sum(amount),0) as 'refundAmountSum'")
                .between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt),"create_at", startCreateAt, endCreateAt)
                .groupBy("pay_trxcode");

        return this.listMaps(queryWrapper);
    }

    @Override
    public Map<String, Object> countCardRefundAmountByPayTrxcodeAndCashId(Map<String, String> paramsMap) {

        String payTrxcode = paramsMap.get("payTrxcode");
        String cashId = paramsMap.get("cashId");
        String startCreateAt = paramsMap.get("startCreateAt");
        String endCreateAt = paramsMap.get("endCreateAt");

        QueryWrapper<OutletsOrderRefundCancel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isEmpty(payTrxcode), "pay_trxcode", payTrxcode)
                .eq(!StringUtils.isEmpty(cashId), "cash_id", cashId)
                .between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt),"oorc.create_at", startCreateAt, endCreateAt);

        return this.baseMapper.countRefundAmount(queryWrapper);
    }

    @Override
    public Map<String, Object> countCardRefundAmountByPayTrxcodeDescribeAndCashId(Map<String, String> paramsMap) {

        String payTrxcodeDescribe = paramsMap.get("payTrxcodeDescribe");
        String cashId = paramsMap.get("cashId");
        String startCreateAt = paramsMap.get("startCreateAt");
        String endCreateAt = paramsMap.get("endCreateAt");

        QueryWrapper<OutletsOrderRefundCancel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isEmpty(payTrxcodeDescribe), "pay_trxcode_describe", payTrxcodeDescribe)
                .eq(!StringUtils.isEmpty(cashId), "cash_id", cashId)
                .between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt),"oorc.create_at", startCreateAt, endCreateAt);

        return this.baseMapper.countRefundAmount(queryWrapper);
    }


}
