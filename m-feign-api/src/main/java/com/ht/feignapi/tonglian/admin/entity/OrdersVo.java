package com.ht.feignapi.tonglian.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/29 17:49
 */
@Data
public class OrdersVo {
    private Long id;
    private String amount;
    private String type;
    private String merchantCode;
    private String orderCode;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createAt;
    private Long userId;
    private Long saleId;
    private String nickName;
    private String tel;
    private String gender;
    private String originFrom;
    private String memberType;
    private String adminName;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateAt;
}
