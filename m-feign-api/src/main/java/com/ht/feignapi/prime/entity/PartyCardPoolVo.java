package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 *  平台 卡池信息vo
 * </p>
 *
 * @author hy.wang
 * @since 2021-07-14
 */
@Data
public class PartyCardPoolVo {


    private Long id;

    private String cardNo;

    /**
     * 有效期
     */
    private Date validityDate;

    /**
     * 卡状态 0-正常 , 1-挂失,2-冻结,3-作废,4-已转移,5已领取
     */
    private String cardSta;

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

    private Date createAt;

    private Date updateAt;

}
