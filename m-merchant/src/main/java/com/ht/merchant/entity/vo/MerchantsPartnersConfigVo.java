package com.ht.merchant.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  商家渠道合作机构关系vo
 * </p>
 *
 * @author ${author}
 * @since 2021-07-22
 */
@Data
public class MerchantsPartnersConfigVo implements Serializable {


    /**
     * 合作机构编码
     */
    private String merchantCode;


    /**
     * 合作机构名称
     */
    private String merchantName;

    /**
     * 机构渠道id
     */
    private String channelId;

    /**
     * 机构渠道名称
     */
    private String channelName;

    /**
     * 渠道合作商编码
     */
    private String channelPartnerCode;

    /**
     * 渠道合作商名称
     */
    private String channelPartnerName;



}
