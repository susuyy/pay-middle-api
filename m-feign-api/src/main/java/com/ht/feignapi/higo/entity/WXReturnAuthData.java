package com.ht.feignapi.higo.entity;

import lombok.Data;

import javax.naming.directory.SearchResult;
import java.io.Serializable;

@Data
public class WXReturnAuthData implements Serializable {

    /**
     * 用户唯一标识
     */
    private String openid;

    /**
     * 会话密钥
     */
    private String session_key;

    /**
     * 用户在开放平台的唯一标识符
     */
    private String unionid;

    /**
     * 错误码
     */
    private int errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
