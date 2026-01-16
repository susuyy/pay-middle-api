package com.ht.merchant.entity;

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
