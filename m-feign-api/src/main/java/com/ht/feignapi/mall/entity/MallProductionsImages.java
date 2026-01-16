package com.ht.feignapi.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/11/18 10:34
 */
@Data
public class MallProductionsImages implements Serializable {
    private Long id;

    private String imageUrl;

    private String mallCode;

    private String mallProductionCode;

    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateAt;
}
