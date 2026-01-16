package com.ht.user.outlets.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2021/12/14 16:20
 */
@Data
public class UpdateRefundPasswordVO implements Serializable {

    /**
     * 原密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

}
