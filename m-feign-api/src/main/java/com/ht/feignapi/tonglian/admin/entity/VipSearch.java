package com.ht.feignapi.tonglian.admin.entity;

import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/23 11:52
 */
@Data
public class VipSearch {
    private String tel ;
    private String nickName;
    private String vipLevel;
    private String cardCode;
    private String state;
    private String registerOrigin;
    private String timeStart;
    private String merchantCode;
    private Long pageNo;
    private Long pageSize;
    private String timeEnd;
}
