package com.ht.feignapi.shoppingmall.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShoppingMallReqUserData implements Serializable {


    private String userInfo;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private String gender;

    private String province;

    private String city;

    private String country;

    /**
     * 敏感数据加密字符串
     */
    private String encryptedData;

    /**
     * 加密向量
     */
    private String iv;

    /**
     * 小程序上传的交互code
     */
    private String code;


}
