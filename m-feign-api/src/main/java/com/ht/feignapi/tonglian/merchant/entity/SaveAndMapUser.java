package com.ht.feignapi.tonglian.merchant.entity;

import com.ht.feignapi.auth.entity.UserUsers;
import lombok.Data;

import java.io.Serializable;

@Data
public class SaveAndMapUser implements Serializable {

    /**
     * 合作机构
     */
    private Merchants merchants;

    /**
     * 用户
     */
    private UserUsers userUsers;
}
