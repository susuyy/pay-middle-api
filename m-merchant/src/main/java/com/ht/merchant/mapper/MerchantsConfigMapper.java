package com.ht.merchant.mapper;

import com.ht.merchant.entity.MerchantsConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
public interface MerchantsConfigMapper extends BaseMapper<MerchantsConfig> {

    @Select("SELECT * FROM mrc_merchants_config where merchant_code = #{merchantCode} AND group_code = #{groupCode} ")
    List<MerchantsConfig> selectByMerchantCode(@Param("merchantCode") String merchantCode, @Param("groupCode")String groupCode);

    @Select("SELECT * from mrc_merchants_config where merchant_code = #{merchantCode} AND key = #{key}")
    MerchantsConfig selectByCodeAndKey(@Param("merchantCode")String merchantCode, @Param("key")String key);
}
