package com.ht.user.card.mapper;

import com.ht.user.card.entity.CardCards;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

/**
 * <p>
 * 卡定义 Mapper 接口
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Mapper
public interface CardCardsMapper extends BaseMapper<CardCards> {

    /**
     * 根据卡号查询卡信息
     *
     * @param cardCode
     * @return
     */
    @Select("SELECT c.*,dc.`value` AS cardTypeStr \n" +
            " FROM card_cards c " +
            " LEFT JOIN dic_constant dc ON c.type = dc.`key` WHERE card_code = #{cardCode}")
    @Results({
            @Result(column = "card_code", property = "cardCode", jdbcType = JdbcType.VARCHAR),
            @Result(column = "card_code", property = "limits", many = @Many(select = "com.ht.user.card.mapper.CardLimitsMapper.getLimitsByCardCodeWithOutBatchCode")),
            @Result(column = "card_code",property = "profiles",many = @Many(select = "com.ht.user.card.mapper.CardProfilesMapper.getUseRulesCardCode"))
    })
    CardCards selectByCardCode(String cardCode);

}
