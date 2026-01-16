package com.ht.user.outlets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.user.outlets.config.TrxCodeDescribeEnum;
import com.ht.user.outlets.entity.OutletsOrderPosRefTrace;
import com.ht.user.outlets.mapper.OutletsOrderPosRefTraceMapper;
import com.ht.user.outlets.service.IOutletsOrderPosRefTraceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.outlets.util.MoneyUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-22
 */
@Service
public class OutletsOrderPosRefTraceServiceImpl extends ServiceImpl<OutletsOrderPosRefTraceMapper, OutletsOrderPosRefTrace> implements IOutletsOrderPosRefTraceService {

    @Override
    public List<Map<String, Object>> countLastSevenDaysAmount() {

        List<Map<String, Object>> newMapList = new ArrayList<>();

        //获得近七天的日期和对应日期的 日交易额
        List<Map<String, Object>> mapList = this.baseMapper.selectLastSevenDaysAmount();

        //处理 日交易额格式 分转元
        for (Map<String, Object> map : mapList) {
            Double dayAmount = (Double) map.get("dayAmount");
            Integer intAmount = dayAmount.intValue();
            map.put("dayAmount", MoneyUtils.changeF2YBigDecimal(intAmount));
        }

        //此处为了处理数据缺失导致的问题，比如某一天没有订单创建，那么sql语句中按照日期分组后就没有该日期这一组，自然也会缺失这一天的日交易额
        for (int i = 6; i >= 0; i--) {
            HashMap<String, Object> newMap = new HashMap<>();

            //获得近七天的日期date
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE,-i);
            String date= sdf.format(calendar.getTime());

            BigDecimal dayAmount = new BigDecimal("0.0");
            //将获得的日期和从数据库查询的日期作比较，若存才则将数据库得到的销售额赋值，若不存在则默认该天销售额为0.0
            for (Map<String, Object> map : mapList) {
                if (date.equals(map.get("date"))) {
                    dayAmount = (BigDecimal) map.get("dayAmount");
                }
            }
            newMap.put("date", date);
            newMap.put("日交易额", dayAmount);

            newMapList.add(newMap);

        }

        return newMapList;
    }

    @Override
    public OutletsOrderPosRefTrace queryByOrderCode(String orderCode) {
        QueryWrapper<OutletsOrderPosRefTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return getOne(queryWrapper,false);
    }
}
