package com.ht.user.admin.vo;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/28 15:54
 */
@Data
@NoArgsConstructor
public class AdjustAccount {
    @NotNull
    private Integer amount;

    private String comments;

    /**
     * 操作员id
     */
    @NotNull
    private Long operatorId;

    @NotNull
    private String merchantCode;

    @NotNull
    private Long userId;

    public AdjustAccount(@NotNull Integer amount, String comments) {
        this.amount = amount;
        this.comments = comments;
    }
}
