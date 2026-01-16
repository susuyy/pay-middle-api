package com.ht.feignapi.mall.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2021/1/5 10:03
 */
@Data
public class SalesVolume {
    private String merchantCode;
    private Integer amount;
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date date;
    private String merchantName;
}
