package com.ht.feignapi.mall.entity.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2021/1/7 14:45
 */
@Data
public class ObjectIncomeSearch {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private List<String> merchantCodes;
}
