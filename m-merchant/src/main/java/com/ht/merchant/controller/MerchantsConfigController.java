package com.ht.merchant.controller;


import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.ht.merchant.entity.DbConstantGroupConfig;
import com.ht.merchant.entity.MerchantsConfig;
import com.ht.merchant.entity.vo.MerchantsConfigVO;
import com.ht.merchant.entity.vo.MerchantsPartnersConfigVo;
import com.ht.merchant.entity.vo.VipLevelVo;
import com.ht.merchant.result.SortExistException;
import com.ht.merchant.service.MerchantsConfigService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
@RestController
@RequestMapping(value = "/merchants-config",produces = "application/json;charset=UTF-8")
public class MerchantsConfigController {

    @Autowired
    private MerchantsConfigService merchantsConfigService;

    /**
     * 获取商家下的会员等级
     * @param merchantCode 商户code
     * @return 会员等级信息
     */
    @GetMapping("/vipLevel/{merchantCode}")
    public List<VipLevelVo> getVipLevel(@PathVariable("merchantCode") String merchantCode) {
        List<MerchantsConfig> vipConfigs = merchantsConfigService.getListByGroupCode(merchantCode, DbConstantGroupConfig.VIP_LEVEL);
        List<VipLevelVo> list = new ArrayList<>();
        vipConfigs.forEach(e->{
            VipLevelVo vo = new VipLevelVo();
            vo.setLevelName(e.getValue());
            vo.setLevelType(e.getType());
            list.add(vo);
        });
        return list;
    }

    /**
     * 获取商户支付 配置参数
     * @param merchantCode
     * @return
     */
    @GetMapping("/getPayData/{merchantCode}")
    public List<MerchantsConfigVO> getPayData(@PathVariable("merchantCode")String merchantCode){
        List<MerchantsConfig> merchantsConfigs = merchantsConfigService.queryByMerchantCode(merchantCode);
        System.out.println(merchantsConfigs);
        List<MerchantsConfigVO> merchantsConfigVOs = new ArrayList<>();
        for (MerchantsConfig merchantsConfig : merchantsConfigs) {
            MerchantsConfigVO merchantsConfigVO = new MerchantsConfigVO();
            BeanUtils.copyProperties(merchantsConfig,merchantsConfigVO);
            merchantsConfigVOs.add(merchantsConfigVO);
        }
        System.out.println(merchantsConfigVOs);
        return merchantsConfigVOs;
    }

    /**
     * 获取商户下某个固定key的配置
     * @param merchantCode
     * @param key
     * @return
     */
    @GetMapping("/{merchantCode}/key/{key}")
    public String getListByKey(@PathVariable("merchantCode") String merchantCode,@PathVariable("key") String key){
        MerchantsConfig config = merchantsConfigService.getListByKey(merchantCode,key);
        Assert.notNull(config,"非法key值");
        return config.getValue();
    }

    /**
     * 获取商户下某个group的配置list
     * @param merchantCode
     * @param groupCode
     * @return
     */
    @GetMapping("/{merchantCode}/group/{groupCode}")
    public List<MerchantsConfig> getListByGroupCode(@PathVariable("merchantCode") String merchantCode,@PathVariable("groupCode") String groupCode){
        return merchantsConfigService.getListByGroupCode(merchantCode,groupCode);
    }


    /**
     * 获取商户 组合支付 二维码
     * @param merchantCode
     * @param height
     * @param width
     * @return
     */
    @GetMapping("/getPayQrCode")
    public String getPayQrCode(@RequestParam("merchantCode")String merchantCode,@RequestParam("height")Integer height,@RequestParam("width")Integer width){
        String qrCode=merchantsConfigService.queryPayQrCode(merchantCode,height,width);
        return qrCode;
    }

    /**
     * 获取pos打印信息
     * @param merchantCode
     * @return
     */
    @GetMapping("/posPrintConfig/{merchantCode}")
    public Integer getPrintConfig(@PathVariable("merchantCode") String merchantCode){
        Integer printNum = Integer.parseInt(merchantsConfigService.getListByKey(merchantCode,"POS_PRINT_CONFIG").getValue());
        return printNum;
    }

    /**
     * 获取pos打印信息
     * @param merchantCode
     * @param value
     * @return
     */
    @PostMapping("/posPrintConfig/{merchantCode}/{value}")
    public String savePrintConfig(@PathVariable("merchantCode") String merchantCode,@PathVariable("value") Integer value){
        MerchantsConfig printConfig = merchantsConfigService.getListByKey(merchantCode,"POS_PRINT_CONFIG");
        printConfig.setValue(value.toString());
        merchantsConfigService.saveOrUpdate(printConfig);
        return "保存成功";
    }


    /**
     * 获取轮播图列表
     *
     * @param merchantCode
     * @return
     */
    @GetMapping("/imgShow/{merchantCode}")
    public List<MerchantsConfig> getSlideShow(@PathVariable("merchantCode") String merchantCode) {
        return merchantsConfigService.getImgShowListExt2Asc(merchantCode, "slide_show");
    }

    /**
     * 修改轮播图
     *
     * @param merchantsConfig
     * @return
     */
    @PostMapping("/updateImgShowById")
    public void updateById(@RequestBody MerchantsConfig merchantsConfig) {
        MerchantsConfig exist = merchantsConfigService.getById(merchantsConfig.getId());
        checkSortExist(merchantsConfig,exist.getMerchantCode());
        merchantsConfigService.updateById(merchantsConfig);
    }

    /**
     * 添加轮播图
     *
     * @param merchantsConfig
     * @return
     */
    @PostMapping("/addImgShow")
    public void addImgShow(@RequestBody MerchantsConfig merchantsConfig) {
        merchantsConfig.setKey("SHOW-IMG");
        merchantsConfig.setGroupCode("slide_show");
        checkSortExist(merchantsConfig,merchantsConfig.getMerchantCode());
        merchantsConfigService.save(merchantsConfig);
    }

    private void checkSortExist(MerchantsConfig merchantsConfig,String merchantCode) {
        List<MerchantsConfig> list = merchantsConfigService.getImgShowListExt2Asc(merchantCode, "slide_show");
        if (list.stream().anyMatch(e ->
                e.getExt2().equals(merchantsConfig.getExt2()) && !e.getId().equals(merchantsConfig.getId())
        )) {
            throw new SortExistException("序号：" + merchantsConfig.getExt2() + "已存在");
        }
    }

    /**
     * 删除轮播图
     *
     * @param id
     * @return
     */
    @PostMapping("/removeImgShow/{id}")
    public void removeImgShow(@PathVariable("id") Long id) {
        merchantsConfigService.removeById(id);
    }

    /**
     * 保存商户配置
     * @param merchantsConfig
     */
    @PostMapping
    public void saveMerchantConfig(@RequestBody MerchantsConfig merchantsConfig){
        merchantsConfigService.save(merchantsConfig);
    }

    /**
     * 根据id修改
     *
     * @param merchantsConfig
     * @return
     */
    @PostMapping("/updateById")
    public void updateConfigById(@RequestBody MerchantsConfig merchantsConfig) {
        merchantsConfigService.updateById(merchantsConfig);
    }

    /**
     * 获取C端 法律条款声明
     *
     * @param merchantCode
     * @return
     */
    @GetMapping("/getReadWord")
    public MerchantsConfig getReadWord(@RequestParam("merchantCode")String merchantCode) {
        return merchantsConfigService.getListByKey(merchantCode, "WORD");
    }

    /**
     * 获取渠道合作商家
     * @param merchantCode 商户code
     * @return 渠道合作商家信息
     */
    @GetMapping("/partners/{merchantCode}")
    public List<MerchantsPartnersConfigVo> getPartnerMerchants(@PathVariable("merchantCode") String merchantCode) {
        List<MerchantsPartnersConfigVo> vipConfigs = merchantsConfigService.getMerchantsPartnersVo(merchantCode, "merchant_partners");
        return vipConfigs;
    }
}

