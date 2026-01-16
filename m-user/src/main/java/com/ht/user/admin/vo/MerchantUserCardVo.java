package com.ht.user.admin.vo;

import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/7 11:01
 */
@Data
public class MerchantUserCardVo {
    private String cardName;
    private String cardType;
    private String realName;
    private Long userId;
    private String tel;
    private String nickName;
    private String faceValue;
    private String state;
    private String cardCode;
    private String cardId;
}
