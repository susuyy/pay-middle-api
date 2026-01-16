package com.ht.user.card.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.card.entity.DistributeTrace;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-07-09
 */
public interface DistributeTraceMapper extends BaseMapper<DistributeTrace> {

    @Select("SELECT t.merchant_code,t.amount,t.create_at,t.source,c.card_name,c.type AS cardType \n" +
            " FROM  card_distribute_trace t  LEFT JOIN card_cards c ON t.card_code = c.card_code " +
            " where t.source = #{merchantCode} and t.comments like CONCAT('%',#{traceType},'%')")
    List<DistributeTrace> getList(String traceType, String merchantCode, IPage<DistributeTrace> page);
}
