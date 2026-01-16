package com.ht.user.admin.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/29 9:53
 */
@Data
public class Recharge {
    @NotNull
    private List<Long> userIds;

    @NotNull
    private Integer amount;

    /**
     * 操作员id
     */
    @NotNull
    private Long operatorId;

    @NotNull
    private String merchantCode;
}
