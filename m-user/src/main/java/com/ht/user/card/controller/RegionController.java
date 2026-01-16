package com.ht.user.card.controller;

import com.ht.user.sysconstant.entity.DicRegion;
import com.ht.user.sysconstant.service.DicRegionService;
import com.ht.user.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/18 9:51
 */
@RestController
@RequestMapping("/card-region")
public class RegionController {

    @Autowired
    private DicRegionService regionService;

    /**
     * 通过regionId获取region子列表
     * @param regionId
     * @return
     */
    @GetMapping("/{regionId}")
    public List<DicRegion> getRegionList(@PathVariable("regionId") Integer regionId){
        return regionService.getProvinces(regionId);
    }
}
