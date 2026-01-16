package com.ht.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户规则明细
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mrc_rule_details")
public class RuleDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 规则主表code
     */
    private String ruleCode;

    /**
     * 规则内容code
     */
    private String deitailCode;

    /**
     * 规则内容名称
     */
    private String detailName;

    /**
     * 规则key
     */
    private String key;

    /**
     * 规则实例01
     */
    private String reg01;

    /**
     * 规则实例02
     */
    private String reg02;

    /**
     * 规则实例03
     */
    private String reg03;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private String state;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
