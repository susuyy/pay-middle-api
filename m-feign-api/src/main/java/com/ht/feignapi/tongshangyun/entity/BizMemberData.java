package com.ht.feignapi.tongshangyun.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BizMemberData implements Serializable {

    /**
     * 通商云返回的userId
     */
    private String userId;

    /**
     * 通商云返回的bizUserId
     */
    private String bizUserId;
}
