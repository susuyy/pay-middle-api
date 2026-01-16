package com.ht.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.merchant.entity.vo.MerchantPrimeVo;
import com.ht.merchant.vo.VipSearch;
import com.ht.merchant.vo.VipVo;
import com.ht.merchant.entity.MrcMapMerchantPrimes;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商户-会员对应表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-15
 */
public interface MrcMapMerchantPrimesService extends IService<MrcMapMerchantPrimes> {

    /**
     * 根据userId查询商户关联
     *
     * @param userId
     * @return
     */
    MrcMapMerchantPrimes queryByUserId(Long userId);


    /**
     * 根据userId 和 商户码查询商户关联
     *
     * @param userId
     * @param merchantCode
     * @return
     */
    MrcMapMerchantPrimes queryByUserIdAndMerchantCode(Long userId, String merchantCode);

    /**
     * 建立用户和商户关联
     *  @param userId
     * @param merchantCode
     * @param state
     * @param type
     * @param openId
     */
    void add(Long userId, String merchantCode, String state, String type, String openId);

    /**
     * 通过merchantCode获取该商户所有的Vip（包括子商户vip）
     *
     * @param merchantCode 商户号
     * @param vipSearch 搜索条件
     * @param page
     * @return 商户vip列表信息
     */
    List<VipVo> getVipListByMerchantCodes(String merchantCode, VipSearch vipSearch, IPage<VipVo> page);

    /**
     * 获取商户下的某一类会员的用户
     * @param merchantCode
     * @param memberType
     * @return
     */
    List<MrcMapMerchantPrimes> getUserByMemberType(String merchantCode, String memberType);

    /**
     * 获取商户下是否有这个会员id
     * @param merchantCode
     * @param userId
     * @return
     */
    boolean checkUserExist(String merchantCode, Long userId);

    /**
     * 通过openId获取商户信息
     * @param openId
     * @param merchantCode
     * @return
     */
    MrcMapMerchantPrimes getPrimeByOpenId(String openId, String merchantCode);

    /**
     * 查询用户在某个商户下的积分
     * @param userId
     * @param openId
     * @param merchantCode
     * @return
     */
    Integer queryUserPoint(Long userId, String openId, String merchantCode);

    /**
     * 查询用户总积分
     * @param userId
     * @param openId
     * @return
     */
    Integer queryMyTotalPoint(String userId, String openId);

    /**
     * 查询 用户 MrcMapMerchantPrimes数据
     * @param userId
     * @param openId
     * @param merchantCode
     * @return
     */
    MrcMapMerchantPrimes queryMyMrcMapMerchantPrimes(Long userId, String openId, String merchantCode);

    /**
     * 根据id 扣除使用积分
     * @param id
     * @param usePoints
     */
    void deductPointsById(String id, Integer usePoints);

    MrcMapMerchantPrimes queryByTelAndMerchantCode(String tel, String merchantCode);

    /**
     * 获取会员月新增的量
     * @param begin
     * @param end
     * @param merchantCodes
     * @return
     */
    List<MerchantPrimeVo> getPrimeMonthlyIncrements(Date begin, Date end, List<String> merchantCodes);

    /**
     * 获取会员月总量
     * @param begin
     * @param end
     * @param merchantCodes
     * @return
     */
    List<MerchantPrimeVo> getPrimeTotalAmount(Date begin, Date end, List<String> merchantCodes);

    /**
     * 始终 查询主体下的会员
     * @param tel
     * @param merchantCode
     * @return
     */
    MrcMapMerchantPrimes queryByTelChangeObjectCode(String tel, String merchantCode);

    /**
     * 创建主体会员信息
     * @param mrcMapMerchantPrimes
     */
    void addAndRegisterTsyMember(MrcMapMerchantPrimes mrcMapMerchantPrimes);

}
