package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *  平台 卡同步数据
 * </p>
 *
 * @author hy.wang
 * @since 2021-08-05
 */
@Data
public class PartyCardSynDataVo {

    private List<PartyCardSynVo> partyCardSynVos = new ArrayList<>();

}
