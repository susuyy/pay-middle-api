package com.ht.feignapi.prime.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 *  平台机构渠道 用户领取电子卡数据vo
 * </p>
 *
 * @author hy.wang
 * @since 2021-07-23
 */
@Data
public class PartyCardReceiveUserVo {


    private Long id;

    private String vipUserOpenid;

    private Long userId;

    private String userPhone;

    private String cardNo;

    /**
     * 卡状态
     * 0-正常
     * 1-挂失
     * 2-冻结
     * 3-作废
     */
    private String cardSta;

    private String cardType;

    /**
     * 有效期 开始时间
     */
    private Date validFrom;

    /**
     * 有效期 结束时间
     */
    private Date validityDate;

    /**
     * 操作员id
     */
    private Long operatorId;

    /**
     * 操作员账号
     */
    private String operatorAccount;

    /**
     * 外联合作机构号
     */
    private String refBrhId;

    /**
     * 储值渠道方
     */
    private String channelId;

    /**
     * 渠道合作商编码
     */
    private String channelPartnerCode;

    /**
     * 批次号
     */
    private String batchCode;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
//    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
//    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
////    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
//    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date updateAt;

    /**
     * 原始开卡总额
     */
    @TableField(exist = false)
    private Integer amount;

    /**
     * 卡余额
     */
    @TableField(exist = false)
    private Integer accountBalance;

    /**
     * 实体卡类别
     */
    @TableField(exist = false)
    private String cardProductName;

    /**
     * 卡图片
     */
    private String backGround;

    /**
     * 字号颜色
     */
    private String color;

    /**
     * 所属主体
     */
    @TableField(exist = false)
    private String objMerchantCode;

    /**
     * 收款/支付方式
     */
    @TableField(exist = false)
    private String payType;

    /**
     * 录入实际收入金额
     */
    @TableField(exist = false)
    private String payAmount;

    /**
     * 备注
     */
    @TableField(exist = false)
    private String message;

}
