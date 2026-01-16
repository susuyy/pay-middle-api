package com.ht.feignapi.prime.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据库 actual_card 表对应实体类
 *
 * @author suyangyu
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ActualCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String vipUserOpenid;

    private Long userId;

    private String cardNo;

    /**
     * 卡状态
     * 0-正常
     * 1-挂失
     * 2-冻结
     * 3-作废
     */
    private String cardSta;

    /**
     * 有效期
     */
    private Date validityDate;

    private Date createTime;

    private Date updateTime;

    /**
     * 卡余额
     */
    @TableField(exist = false)
    private BigDecimal accountBalance;

}
