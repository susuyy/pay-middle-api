package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;


/**
 * 前端提交 绑定虚拟卡的所需参数
 *
 * @author suyangyu
 * @since 2020-06-09
 */
@Data
public class BindCardData implements Serializable {

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 实体卡卡号
     */
    private String cardId;

    /**
     * 卡类别
     */
    private String cardType;

    /**
     * 手机收到的验证吗
     */
    private String code;

    /**
     * 用户对应微信的openid
     */
    private String openId;

    /**
     * 密码
     */
    private String password;

}
