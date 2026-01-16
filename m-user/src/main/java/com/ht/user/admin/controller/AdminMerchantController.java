package com.ht.user.admin.controller;

import com.ht.user.common.Result;
import com.ht.user.sysconstant.DbConstantGroupConfig;
import com.ht.user.sysconstant.entity.DicConstant;
import com.ht.user.sysconstant.entity.DicRegion;
import com.ht.user.sysconstant.service.DicConstantService;
import com.ht.user.sysconstant.service.DicRegionService;
import com.ht.user.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/27 14:54
 */
@RequestMapping("/admin/merchant")
@RestController
public class AdminMerchantController {

    @Autowired
    private DicConstantService dicConstantService;

    @Autowired
    private DicRegionService regionService;


    /**
     * 店铺管理--公众号管理--公众号菜单管理
     * @param merchantCode
     * @return
     */
    @GetMapping("/miniProgramUrl/{merchantCode}")
    public String getMiniProgramUrl(@PathVariable("merchantCode") String merchantCode){
        List<DicConstant> list = dicConstantService.getKeyValue(DbConstantGroupConfig.MINI_PROGRAM_URL);
        Assert.isTrue(!CollectionUtils.isEmpty(list),"参数有误");
        return list.get(0).getValue()+"?merchantCode="+merchantCode;
    }


    /**
     * 获取省市区下拉框，省直接传入regionId=10000
     * @param regionId
     * @return
     */
    @GetMapping("/regions/{regionId}")
    public List<DicRegion> getProvince(@PathVariable("regionId") Integer regionId){
        List<DicRegion> provinces = regionService.getProvinces(regionId);
        return provinces;
    }

    /**
     * 获取收银下拉框
     * @return contants
     */
    @GetMapping("/chargeType")
    public List<DicConstant> getChargeType(){
        List<DicConstant> constants = dicConstantService.getListByGroupCode(DbConstantGroupConfig.MRC_CHARGE_TYPE);
        return constants;
    }
}
