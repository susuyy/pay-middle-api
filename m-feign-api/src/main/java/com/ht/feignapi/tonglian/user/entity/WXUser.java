package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

@Data
public class WXUser {


    private String nickname;

    private Integer sex;

    private String pictureURL;

    private String openid;

    private String unionID;

    private String errCode;

    private String errMsg;

}
