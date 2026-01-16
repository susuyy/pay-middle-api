package com.ht.feignapi.tonglian.admin.entity;

import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/7 11:01
 */
@Data
public class MerchantUserCardVo {
    private String cardName;
    private String cardType;
    private Long userId;
    private String realName;
    private String tel;
    private String nickName;
    private String faceValue;
    private String state;
    private String cardCode;
    private String cardId;
}
