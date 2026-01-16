package com.ht.auth2.entity;

import lombok.Data;

import java.io.Serializable;

/****
 * @Author: suyangyu
 * @Date:2020/07/13 14:52
 * @Description:用户令牌封装
 *****/
@Data
public class AuthToken implements Serializable{

    /**
     * token信息
     */
    String accessToken;


    /**
     * token类型
     */
    String tokenType;

    /**
     * 过期时间
     */
    Integer expiresIn;

    /**
     * 作用范围
     */
    String scope;

    /**
     * 自定义字段
     */
    String enhance;

    /**
     * jwt短令牌
     */
    String jti;


}