package com.ht.feignapi.appshow.entity;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * @author: zheng weiguang
 * @Date: 2020/10/21 15:24
 */
@Data
public class MallCouponSearch {
    private String merchantName;

    /**
     * 计次券：number；
     * 金额券：money,
     * 代金券：credit，
     * 满减券：coupon
     */
    private String couponType;

    @NotBlank
    private String mallCode;
}
