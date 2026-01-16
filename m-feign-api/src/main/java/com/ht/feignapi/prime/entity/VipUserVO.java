package com.ht.feignapi.prime.entity;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 响应前端的用户会员数据实体类
 *
 * @author suyangyu
 * @since 2020-06-09
 */
@Data
public class VipUserVO implements Serializable {


    private static final long serialVersionUID = 1L;


    private Long id;

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 会员等级
     */
    private Integer vipLevel;

    /**
     * 积分
     */
    private Integer point;

    /**
     * 账户余额
     */
    private BigDecimal accountBalance;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 卡卷数量
     */
    private Integer cardNum;
}
