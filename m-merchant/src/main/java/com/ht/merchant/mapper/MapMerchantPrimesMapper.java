package com.ht.merchant.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.merchant.entity.vo.MerchantPrimeVo;
import com.ht.merchant.vo.VipSearch;
import com.ht.merchant.vo.VipVo;
import com.ht.merchant.entity.MrcMapMerchantPrimes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商户-会员对应表 Mapper 接口
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
public interface MapMerchantPrimesMapper extends BaseMapper<MrcMapMerchantPrimes> {


    /**
     * 根据userId查询商户关联
     *
     * @param userId
     * @return
     */
    @Select("SELECT * FROM mrc_map_merchant_primes WHERE user_id = #{userId}")
    MrcMapMerchantPrimes selectByUserId(@Param("userId") Long userId);

    /**
     * 获取多个商户号
     * @param merchantCode 商户卡号
     * @param vipSearch 搜索条件
     * @param page 页码
     * @return
     */
    @Select({"<script>",
            "SELECT mp.user_id as userId,mp.id as vipId,mp.merchant_code as code,mp.open_id,mp.create_at as createTime,mp.state,m.merchant_name as registerOrigin,mp.type as vipLevel  FROM `mrc_map_merchant_primes` mp\n",
            "left join mrc_merchants m on mp.merchant_code = m.merchant_code where mp.merchant_code=#{merchantCode} ",
            "<if test='vipSearch.tel != null and vipSearch.tel != &quot;&quot;'>",
            " AND u.tel=#{vipSearch.tel}",
            "</if>",
            "<if test='vipSearch.vipLevel != null and vipSearch.vipLevel != &quot;&quot;'>",
            " AND mp.type=#{vipSearch.vipLevel}",
            "</if>",
            "<if test='vipSearch.state != null and vipSearch.state != &quot;&quot;'>",
            " AND mp.state=#{vipSearch.state}",
            "</if>",
            "<if test='vipSearch.timeStart != null and vipSearch.timeStart != &quot;&quot;'>",
            " AND mp.create_at &gt; #{vipSearch.timeStart}",
            "</if>",
            "<if test='vipSearch.timeEnd != null and vipSearch.timeEnd != &quot;&quot;'>",
            " AND mp.create_at &lt; #{vipSearch.timeEnd}",
            "</if>",
            "</script>"})
    List<VipVo> getVipList(@Param("merchantCode") String merchantCode, @Param("vipSearch") VipSearch vipSearch, IPage<VipVo> page);

    /**
     * 根据id 扣除使用积分
     * @param id
     * @param usePoints
     */
    @Update("UPDATE mrc_map_merchant_primes SET prime_points = prime_points - #{usePoints} WHERE id = #{id}")
    void deductPointsById(@Param("id") String id,@Param("usePoints") Integer usePoints);

    /**
     * 获取时间段内，按照主体统计的会员总数
     * @param begin
     * @param end
     * @param merchantCode
     * @return
     */
    @Select("SELECT\n" +
            "\tcount(mp.id) as count,m.merchant_name,mp.merchant_code\n" +
            "FROM\n" +
            "\tmrc_map_merchant_primes mp\n" +
            "\tLEFT JOIN mrc_merchants m ON mp.merchant_code = m.merchant_code\n" +
            "\twhere mp.create_at >=#{begin} and mp.create_at<=#{end} and mp.merchant_code = #{merchantCode}\n" +
            "\tGROUP BY m.merchant_name,mp.merchant_code")
    MerchantPrimeVo getPrimeMonthlyIncrements(Date begin, Date end, String merchantCode);

    /**
     * 获取时间段之前，按照主体统计的会员总数
     * @param begin
     * @param end
     * @param merchantCode
     * @return
     */
    @Select("SELECT\n" +
            "\tcount(mp.id) as count,m.merchant_name,mp.merchant_code\n" +
            "FROM\n" +
            "\tmrc_map_merchant_primes mp\n" +
            "\tLEFT JOIN mrc_merchants m ON mp.merchant_code = m.merchant_code\n" +
            "\twhere mp.create_at<=#{deadline} and mp.merchant_code = #{merchantCode}\n" +
            "\tGROUP BY m.merchant_name,mp.merchant_code")
    MerchantPrimeVo getPrimeTotalAmount(Date deadline, String merchantCode);
}
