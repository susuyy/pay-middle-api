package com.ht.merchant.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2021/1/6 15:24
 */
@Data
public class MerchantCountVo {
    private Integer count;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GTM+8")
    private Date date;
}
