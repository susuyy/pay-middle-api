package com.ht.merchant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.merchant.entity.MerchantsConfig;
import com.ht.merchant.entity.vo.MerchantsPartnersConfigVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
public interface MerchantsConfigService extends IService<MerchantsConfig> {

    /**
     * 通过商户号和groupCode获取会员等级列表
     * @param merchantCode 商户号
     * @param groupCode 会员常量
     * @return 列表
     */
    List<MerchantsConfig> getListByGroupCode(String merchantCode, String groupCode);

    List<MerchantsConfig> getImgShowListExt2Asc(String merchantCode, String groupCode);

    /**
     * 获取商户扫码支付参数
     * @param merchantCode
     */
    List<MerchantsConfig> queryByMerchantCode(String merchantCode);

    MerchantsConfig getListByKey(String merchantCode, String key);

    /**
     * 获取商户组合支付码
     * @param merchantCode
     * @return
     */
    String queryPayQrCode(String merchantCode,Integer height,Integer width);

    Boolean saveMerchantQrCode(String merchantCode);

    /**
     * 获取商家渠道对应的合作机构
     * @param merchantCode
     * @param groupCode
     * @return
     */
    List<MerchantsPartnersConfigVo> getMerchantsPartnersVo(String merchantCode, String groupCode);

}
