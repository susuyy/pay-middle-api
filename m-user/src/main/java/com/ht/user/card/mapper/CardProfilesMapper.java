package com.ht.user.card.mapper;

import com.ht.user.card.entity.CardProfiles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-07-28
 */
public interface CardProfilesMapper extends BaseMapper<CardProfiles> {

    @Select("select `value` as tag from card_card_profiles where card_code = #{cardCode} and group_code='card_use_rule'")
    String getUseRulesCardCode(@Param("cardCode") String cardCode);
}
