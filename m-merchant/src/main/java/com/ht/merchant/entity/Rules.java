package com.ht.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户规则主表
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mrc_rules")
public class Rules implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 优先级：数值越大，优先级越搞
     */
    private String priority;

    /**
     * 规则分类
     */
    private String type;

    /**
     * 规则状态
     */
    private String state;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
