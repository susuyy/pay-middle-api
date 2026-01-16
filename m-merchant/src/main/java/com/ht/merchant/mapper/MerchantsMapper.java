package com.ht.merchant.mapper;

import com.ht.merchant.entity.Merchants;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.merchant.entity.vo.MerchantCountVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商户表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
public interface MerchantsMapper extends BaseMapper<Merchants> {

    /**
     * 通过商户码，获取该商户的子用户
     *
     * @param code 商户码
     * @return 返回商户信息
     */
    @Select("SELECT id,merchant_code,merchant_name,user_id,type,business_subjects,business_type" +
            ",location,state,merchant_pic_url,merchant_contact FROM mrc_merchants where business_subjects = #{code}")
    List<Merchants> getSubMerchants(@Param("code") String code);

    @Select("SELECT m.id,m.merchant_code,m.merchant_name,m.user_id,m.type,m.business_subjects,m.business_type"+
            ",m.location,m.state,m.merchant_pic_url,m.merchant_contact from mrc_merchants m left join mrc_map_merchant_user mu on m.merchant_code = mu.merchant_code" +
            " where mu.user_id=#{userId}")
    Merchants getByUserId(@Param("userId") Long userId);
}
