package com.ht.user.card.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.Activity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-12-28
 */
public interface IActivityService extends IService<Activity> {

    /**
     * 获取activity详情，附带limit和card信息
     * @param id
     * @return
     */
    @Override
    Activity getById(Serializable id);

    /**
     * 重写save方法，保存activityLimit和activityMapCard
     * @param activity 实例
     * @return 保存结果
     */
    @Override
    boolean save(Activity activity);

    /**
     * 获取list
     * @param page
     * @param objMerchantCode
     * @return
     */
    Page<Activity> getList(Page<Activity> page, String objMerchantCode);

    /**
     * 重写删除方法
     * @param id
     * @return 删除结果
     */
    @Override
    boolean removeById(Serializable id);

    /**
     * 获取当前主体可用，等级优先的活动
     * @param merchantCode
     * @return
     */
    Activity getActiveActivity(String merchantCode);
}
