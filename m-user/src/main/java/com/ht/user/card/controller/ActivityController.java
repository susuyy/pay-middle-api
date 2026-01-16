package com.ht.user.card.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.Activity;
import com.ht.user.card.service.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-12-28
 */
@RestController
@RequestMapping("/card/activity")
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    /**
     * 保存送券活动
     * @param activity
     * @return
     */
    @PostMapping
    public Boolean save(@RequestBody Activity activity){
        return activityService.save(activity);
    }

    /**
     * 根据id删除活动
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable("id") Long id){
        return activityService.removeById(id);
    }

    /**
     * 获取主体活动列表
     * @param objMerchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/{objMerchantCode}/list")
    public Page<Activity> list(
            @PathVariable("objMerchantCode") String objMerchantCode,
            @RequestParam(value = "pageNo",required = false,defaultValue = "0") Long pageNo,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10")Long pageSize){
        Page<Activity> page = new Page<>(pageNo,pageSize);
        return activityService.getList(page,objMerchantCode);
    }

    /**
     * 获取活动详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Activity info(@PathVariable("id") Long id){
        return activityService.getById(id);
    }

}

