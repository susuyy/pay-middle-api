package com.ht.merchant.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2021/1/6 10:20
 */
@Data
public class MerchantPrimeVo {
    @JsonFormat(pattern = "yyyy-MM")
    private Date date;
    private Integer count;
    private String merchantCode;
    private String merchantName;
}
