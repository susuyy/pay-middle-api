package com.ht.user.outlets.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2021/11/22 15:42
 */
@Data
public class QueryCountSumVO implements Serializable {

    /**
     * 开始日期
     */
    private String startCreateAt;

    /**
     * 结束日期
     */
    private String endCreateAt;

}
