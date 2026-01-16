package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *  平台 查询卡同步数据vo
 * </p>
 *
 * @author hy.wang
 * @since 2021-08-05
 */
@Data
public class PartyCardQueryData {


    private List<String> cardNos = new ArrayList<>();


}
