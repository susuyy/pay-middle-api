package com.ht.merchant.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/19 18:23
 */
@Data
public class VipVo implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 会员id
     */
    private Long vipId;

    /**
     * 编号
     */
    private String code;
    /**
     * 手机号
     */
    private String tel;
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别
     */
    private String gender;
    /**
     * 会员等级
     */
    private String vipLevel;
    /**
     * 钱包余额
     */
    private String account;
    /**
     * 积分
     */
    private String point;
    /**
     * 累计消费额
     */
    private Double payTotalAmount;

    /**
     * 会员卡卡号
     */
    private String cardCode;

    /**
     * 会员昵称
     */
    private String nickName;

    /**
     * 微信openId
     */
    private String openId;

    /**
     * 注册日期
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 注册来源
     */
    private String registerOrigin;

    /**
     * 会员状态
     */
    private String state;
}
