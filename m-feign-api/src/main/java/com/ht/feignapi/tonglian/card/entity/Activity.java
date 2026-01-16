package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Activity implements Serializable {

    /**
     * 可领取会员类型
     */
    private String userVipType;

    /**
     * 领取限制
     */
    private String limitGet;

    /**
     * 活动时段
     */
    private String activityTimeScope;

    /**
     * 领取日期
     */
    private String getDay;

    /**
     * 周几领取
     */
    private String getWeek;

    /**
     * 每日领取上限
     */
    private String getDaily;

    /**
     * 总领取上限
     */
    private String getTotal;


    /**
     * 领取时段
     */
    private String getTimeScope;

    /**
     * 活动须知
     */
    private String activityNotice;
}
