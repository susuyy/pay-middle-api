package com.ht.user.admin.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/7 17:22
 */
@Data
public class PosCardVo {
    @NotNull
    private String name;

    @NotNull
    private String adminMerchantCode;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date onSaleDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date haltSaleDate;

    @NotNull
    private Integer inventory;

    @NotNull
    private String cardCode;

    @NotNull
    private List<String> memberLevels;

    private Integer dailyLimit;

    private Integer totalLimit;
}
