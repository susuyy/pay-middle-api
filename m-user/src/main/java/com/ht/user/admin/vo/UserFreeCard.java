package com.ht.user.admin.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/9 16:02
 */
@Data
public class UserFreeCard {

    @NotNull
    private String cardCode;

    @NotNull
    private String adminMerchantCode;

    @NotNull(message = "请填写会员等级")
    private List<String> memberLevelsLimit;

    @NotBlank(message = "请填写每人每日限购")
    private String dailyLimit;

    @NotNull(message = "请填写每人总限购")
    private String totalLimit;

    @NotNull(message = "请填写可领取时间")
    private String durationLimit;
    private List<String> dayLimit;
    private List<String> weekLimit;
    private List<String> hourLimit;

    @NotNull
    private Integer inventory;

    private String batchCode;
}
