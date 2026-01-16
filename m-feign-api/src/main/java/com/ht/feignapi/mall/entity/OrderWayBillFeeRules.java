package com.ht.feignapi.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderWayBillFeeRules implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 运费规则
     */
    private String wayBillRuleCode;

    /**
     * 默认运费. 单位：分
     */
    private Integer defaultFee;

    /**
     * 按照百分比计算运费
     */
    private Integer byPercent;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private String state;


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createAt;


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateAt;


}
