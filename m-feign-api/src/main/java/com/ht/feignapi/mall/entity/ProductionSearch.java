package com.ht.feignapi.mall.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: zheng weiguang
 * @Date: 2020/12/7 10:49
 */
@Data
public class ProductionSearch {
    @NotBlank
    private String type;
    private String productionName;
    private String productionCode;

    /**
     * 上下架状态：上架：Y，下架N
     */
    @NotBlank
    private String onSaleState;

    private String showCategoryCode;

    private Long pageNo;
    private Long pageSize;
}
