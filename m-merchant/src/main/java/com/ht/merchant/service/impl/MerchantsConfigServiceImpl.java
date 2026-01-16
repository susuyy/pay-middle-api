package com.ht.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.merchant.common.MerchantConstant;
import com.ht.merchant.config.MerchantConfigKeyConstant;
import com.ht.merchant.entity.vo.MerchantsPartnersConfigVo;
import com.ht.merchant.service.MerchantsConfigService;
import com.ht.merchant.config.MerchantsConfigGroupCode;
import com.ht.merchant.entity.MerchantsConfig;
import com.ht.merchant.mapper.MerchantsConfigMapper;
import com.ht.merchant.utils.QrCodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
@Service
public class MerchantsConfigServiceImpl extends ServiceImpl<MerchantsConfigMapper, MerchantsConfig> implements MerchantsConfigService {

    @Override
    public List<MerchantsConfig> getListByGroupCode(String merchantCode, String groupCode) {
        LambdaQueryWrapper<MerchantsConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantsConfig::getMerchantCode, merchantCode);
        wrapper.eq(MerchantsConfig::getGroupCode, groupCode);
        wrapper.orderByDesc(MerchantsConfig::getUpdateAt);
        return this.list(wrapper);
    }

    @Override
    public List<MerchantsConfig> getImgShowListExt2Asc(String merchantCode, String groupCode) {
        LambdaQueryWrapper<MerchantsConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantsConfig::getMerchantCode, merchantCode);
        wrapper.eq(MerchantsConfig::getGroupCode, groupCode);
        wrapper.orderByAsc(MerchantsConfig::getExt2);
        return this.list(wrapper);
    }

    /**
     * 获取商户扫码支付参数
     * @param merchantCode
     */
    @Override
    public List<MerchantsConfig> queryByMerchantCode(String merchantCode) {
        return this.baseMapper.selectByMerchantCode(merchantCode, MerchantsConfigGroupCode.PAY_DATA);
    }

    @Override
    public MerchantsConfig getListByKey(String merchantCode, String key) {
        LambdaQueryWrapper<MerchantsConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantsConfig::getMerchantCode, merchantCode);
        wrapper.eq(MerchantsConfig::getKey, key);
        return this.getOne(wrapper);
    }


    @Override
    public String queryPayQrCode(String merchantCode,Integer height,Integer width) {
        QueryWrapper queryWrapper =new QueryWrapper();
        queryWrapper.eq("merchant_code",merchantCode);
        queryWrapper.eq("`key`",MerchantConfigKeyConstant.QR_CODE_URL);
        MerchantsConfig merchantsConfig = this.baseMapper.selectOne(queryWrapper);
//        MerchantsConfig merchantsConfig = this.baseMapper.selectByCodeAndKey(merchantCode,MerchantConfigKeyConstant.QR_CODE_URL);
        String code = QrCodeUtils.creatRrCode(merchantsConfig.getValue(), height, width);
        String replace = code.replace("\r\n", "");
        String replaceOne = replace.replace("\n", "");
        String replaceTwo = replaceOne.replace("\r", "");
        return replaceTwo;
    }

    @Override
    public Boolean saveMerchantQrCode(String merchantCode) {
        MerchantsConfig merchantsConfig = new MerchantsConfig();
        merchantsConfig.setMerchantCode(merchantCode);
        merchantsConfig.setKey("QR_CODE_URL");
        merchantsConfig.setValue("https://allinpay.hualta.com/pay/getWXCode/" + merchantCode);
        merchantsConfig.setGroupCode("qr_code_url");
        return this.saveOrUpdate(merchantsConfig);
    }

    @Override
    public List<MerchantsPartnersConfigVo> getMerchantsPartnersVo(String merchantCode, String groupCode) {
        List<MerchantsConfig> configs = getListByGroupCode(merchantCode, groupCode);
        List<MerchantsPartnersConfigVo> list = new ArrayList<>();
        configs.forEach(config->{
            MerchantsPartnersConfigVo vo = new MerchantsPartnersConfigVo();
            vo.setMerchantCode(config.getKey());
            vo.setMerchantName(MerchantConstant.merchantNameMap.get(config.getKey()));
            vo.setChannelId(merchantCode);
            vo.setChannelName(MerchantConstant.merchantNameMap.get(merchantCode));

            String channelPartnerCode = config.getValue();
            vo.setChannelPartnerCode(channelPartnerCode);
            String channelPartnerName = MerchantConstant.merchantNameMap.get(channelPartnerCode);
            vo.setChannelPartnerName(StringUtils.isEmpty(channelPartnerName)?channelPartnerCode:channelPartnerName);
            list.add(vo);
        });
        return list;
    }

}
