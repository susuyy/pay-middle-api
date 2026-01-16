package com.ht.user.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/21 16:44
 */
@Data
public class RechargeListVo implements Serializable {
    /**
     * 批次号
     */
    private String orderNo;

    /**
     * 充值账户，钱包
     */
    private String type;

    /**
     * 充值额度
     */
    private Integer amount;

    /**
     * 充值笔数
     */
    private Integer count;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date rechargeDate;
    /**
     * 操作员
     */
    private String operator;
    /**
     * 状态
     */
    private String state;
}
