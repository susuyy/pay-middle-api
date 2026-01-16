package com.ht.auth2.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SearchData implements Serializable {

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 应用编码
     */
    private String appCode;

    private Date startTime;

    private Date endTime;

    private Integer pageNo;

    private Integer pageSize;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 账号
     */
    private String account;

    /**
     * 昵称
     */
    private String nickName;
}
