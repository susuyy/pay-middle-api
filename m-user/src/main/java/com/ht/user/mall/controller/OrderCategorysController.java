package com.ht.user.mall.controller;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.user.mall.entity.OrderCategorys;
import com.ht.user.mall.service.OrderCategorysService;
import com.ht.user.sysconstant.DbConstantGroupConfig;
import com.ht.user.sysconstant.entity.DicConstant;
import com.ht.user.sysconstant.service.DicConstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-16
 */
@RestController
@RequestMapping("/mall/orderCategorys")
public class OrderCategorysController {


    @Autowired
    private OrderCategorysService orderCategorysService;

    @Autowired
    private DicConstantService dicConstantService;

    /**
     * 查询顶级编码
     * @param merchantCode
     * @param categoryThreeCode
     * @return
     */
    @GetMapping("/queryLevelOneCode")
    public OrderCategorys queryLevelOneCode(@RequestParam("categoryCode")String categoryThreeCode,
                                     @RequestParam("storeMerchantCode")String merchantCode){
        OrderCategorys orderCategorys = orderCategorysService.queryLevelOneCode(categoryThreeCode, merchantCode);
        return orderCategorys;
    }

    /**
     * 保存分类信息
     * @param orderCategorys
     */
    @PostMapping
    public void saveCategories(@RequestBody OrderCategorys orderCategorys){
        Assert.isTrue(!StringUtils.isEmpty(orderCategorys.getCategoryLevel01Code()),"请选择一级品类");
        if (StringUtils.isEmpty(orderCategorys.getCategoryLevel02Code())){
            orderCategorys.setCategoryLevel02Code(orderCategorys.getCategoryLevel01Code()+ IdWorker.getIdStr());
        } else {
            orderCategorys.setCategoryLevel02Name(orderCategorysService.getCategory02NameByCode(orderCategorys.getCategoryLevel02Code()));
        }
        orderCategorys.setCategoryLevel03Code(orderCategorys.getCategoryLevel02Code() + IdWorker.getIdStr());
        orderCategorysService.save(orderCategorys);
    }

    /**
     * 获取所有一级分类
     * @return
     */
    @GetMapping("/firstLevel")
    public List<Map<String,String>> getFirstLevelCategories(){
        List<Map<String,String>> list = new ArrayList<>();
        List<DicConstant> dicConstantList = dicConstantService.getListByGroupCode(DbConstantGroupConfig.CATEGORY_FIRST_LEVEL);
        for (DicConstant dicConstant: dicConstantList) {
            Map<String,String> map = new HashMap<>();
            map.put("key",dicConstant.getKey());
            map.put("value",dicConstant.getValue());
            list.add(map);
        }
        return list;
    }

    /**
     * 获取二级分类下拉框
     * @return
     */
    @GetMapping("/{firstLevelCode}/secondLevel/{merchantCode}")
    public List<Map<String,String>> getSecondLevelCategories(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("firstLevelCode") String firstLevelCode){
        return orderCategorysService.getSecondLevel(merchantCode,firstLevelCode);
    }

    /**
     * 获取三级分类下拉框
     * @param
     * @return
     */
    @GetMapping("/{secondLevelCode}/thirdLevel/{merchantCode}")
    public List<Map<String,String>> getThirdLevelCategories(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("secondLevelCode") String secondLevelCode){
        return orderCategorysService.getThirdLevel(merchantCode,secondLevelCode);
    }
}

