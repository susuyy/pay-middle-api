package com.ht.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.merchant.entity.*;
import com.ht.merchant.entity.vo.MerchantCountVo;
import com.ht.merchant.vo.VipSearch;
import com.ht.merchant.vo.VipVo;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商户表 服务类
 * </p>
 *
 * @author ${zheng weiguang}
 * @since 2020-06-18
 */
public interface MerchantsService extends IService<Merchants> {

    /**
     * 通过userId 获取商户
     *
     * @param userId 用户id
     * @return 商户信息
     */
    Merchants getByUserId(Long userId);

    /**
     * 通过商户码，获取该商户的子商户
     *
     * @param code
     * @return
     */
    List<Merchants> getSubMerchants(String code);

    /**
     * 获取商户下所有会员信息
     *
     * @param code
     * @return 商户会员关联信息
     */
    IPage<MrcMapMerchantPrimes> getMapMerchantPrimes(String code);

    /**
     * 通过商户码，获取商户以及子商户下的所有会员信息
     *
     * @param merchantCode 商户码
     * @param vipSearch 搜索条件
     * @param page
     * @return 返回所有会员
     */
    List<VipVo> getMerchantAllVipUsers(String merchantCode, VipSearch vipSearch, IPage<VipVo> page);

    /**
     * 根据商户编码 查询商户主体全部信息
     * @param merchantCode
     * @return
     */
    Merchants getMerchantByCode(String merchantCode);

//    UsrUsers getMerchantAdminUser(String merchantCode);
//
//    Boolean checkMerchantPassword(Merchants merchant, String password);

    /**
     * 通过商户码查询 父类商户码
     * @param merchantCode
     * @return
     */
    String queryObjectMerchantCode(String merchantCode);

    /**
     * 获取所有的主体列表
     * @param pageNo
     * @param pageSize
     * @param merchantName
     * @return
     */
    IPage<Merchants> getObjectMerchants(Long pageNo, Long pageSize, String merchantName);

    @Override
    boolean save(Merchants merchants);

    @Override
    boolean saveOrUpdate(Merchants merchants);

    /**
     * 获取整个系统,某一时间段之前的merchant数量统计。商户总量，不是增量
     * @param end
     * @return
     */
    Integer getMerchantsCountBeforeDate(Date end);

    /**
     * 获取时间段内，商户数目
     * @param beginDate
     * @param endDate
     * @return
     */
    List<MerchantCountVo> getMerchantCount(String beginDate, String endDate);

    /**
     * 机构入驻和关联用户
     * @param saveAndMapUser
     */
    String saveAndMapUser(SaveAndMapUser saveAndMapUser);

    /**
     * 分页 获取 subMerchants 包含主体(平台),自身
     * @param searchSubMerchantsData
     * @return
     */
    Page<Merchants> searchSubMerchants(SearchSubMerchantsData searchSubMerchantsData);

    /**
     * 分页获取所有的 机构列表
     * @param searchSubMerchantsData
     * @return
     */
    Page<Merchants> searchAllMerchants(SearchSubMerchantsData searchSubMerchantsData);

    /**
     * 获取所有的商户号
     * @return
     */
    List<String> getObjectMerchantCodes();

    /**
     * 获取所有的商户号
     * @return
     */
    List<Merchants> getObjectMerchant();

    /**
     * 解除合作机构
     * @param brhMerchantCode
     */
    void removeBrhMerchant(String brhMerchantCode);

    List<String> getObjMerchantCodes();

    /**
     * 查询用户和商户的通商云 bizUserId
     * @param merchantCode
     * @param userId
     * @return
     */
    BizMerchantUserData queryMerchantBizUserIdAndPayerBizUserId(String merchantCode, Long userId);

    /**
     * 获取所有的主体商户
     * @return
     */
    List<Merchants> getObjectMerchantCodesData(String subMerchantCode);


}
