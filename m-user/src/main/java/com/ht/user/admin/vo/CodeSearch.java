package com.ht.user.admin.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/28 11:34
 */
@Data
public class CodeSearch implements Serializable {
    private String cardName;
    private String cardType;
    private String cardState;
}
