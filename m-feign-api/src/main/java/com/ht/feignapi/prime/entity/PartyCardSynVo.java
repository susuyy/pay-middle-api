package com.ht.feignapi.prime.entity;

import lombok.Data;


/**
 * <p>
 *  平台 卡同步数据
 * </p>
 *
 * @author hy.wang
 * @since 2021-08-05
 */
@Data
public class PartyCardSynVo {

    /**
     * 平台 卡批次vo
     */
    private PartyCardBatchVo partyCardBatchVo;
    /**
     * 平台 电子卡vo
     */
    private PartyCardElectronicVo partyCardElectronicVo;
    /**
     * 平台 卡池vo
     */
    private PartyCardPoolVo partyCardPoolVo;


}
