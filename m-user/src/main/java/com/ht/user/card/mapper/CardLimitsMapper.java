package com.ht.user.card.mapper;

import com.ht.user.card.entity.CardLimits;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-06-23
 */
@Mapper
public interface CardLimitsMapper extends BaseMapper<CardLimits> {

    @Select({"<script>",
            "select type,card_type,card_code,batch_code,limit_key from card_limits l ",
            " LEFT JOIN dic_constant c on l.type = c.`key` ",
            " where l.card_code = #{cardCode} and c.group_code = #{groupCode}",
            "<if test='batchCode!=null and batchCode != &quot;&quot;'>",
            " and l.batch_code = #{batchCode} ",
            "</if>",
            "</script>"})
    List<CardLimits> getLimits(String cardCode,String groupCode,String batchCode);

    @Select("select type,card_type,card_code,batch_code,limit_key from card_limits l "+
            " LEFT JOIN dic_constant c on l.type = c.`key` "+
            " where l.card_code = #{cardCode} and l.batch_code is null")
    List<CardLimits> getLimitsByCardCodeWithOutBatchCode(String cardCode);
}
