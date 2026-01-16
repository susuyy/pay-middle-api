package com.ht.feignapi.auth.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAndTokenData implements Serializable {

    /**
     * 用户信息
     */
    private UserVO userVO;

    /**
     * token信息
     */
    private AuthTokenData authTokenData;
}
