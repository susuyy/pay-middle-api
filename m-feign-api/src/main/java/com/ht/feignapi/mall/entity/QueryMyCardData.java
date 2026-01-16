package com.ht.feignapi.mall.entity;

import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryMyCardData implements Serializable {

    private String openId;

    /**
     * 后台自己封装的数据
     */
    private String userId;

    private String merchantCode;

    private String state;

    private Integer pageNo;

    private Integer pageSize;

    /**
     * 后台自己封装的数据
     */
    private List<Merchants> merchantsList;
}
