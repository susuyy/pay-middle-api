package com.ht.feignapi.tonglian.admin.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/21 16:54
 */
@Data
public class VipLevelVo implements Serializable {
    private String levelName;
    private String vipLevelImgUrl;
    private String levelType;
}
