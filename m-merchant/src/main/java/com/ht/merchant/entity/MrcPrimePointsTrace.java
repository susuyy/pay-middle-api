package com.ht.merchant.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MrcPrimePointsTrace implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户对应公众号的openid
     */
    private String openId;

    /**
     * 贷出：用户消耗积分
     */
    private Integer debit;

    /**
     * 借入：用户积累积分
     */
    private Integer credit;

    /**
     * 状态
     */
    private String state;

    /**
     * 会员类型
     */
    private String type;

    /**
     * 关联单据类型
     */
    private String refType;

    /**
     * 关联单据号
     */
    private String refOrder;

    private Date createAt;

    private Date updateAt;


}
