package com.ht.feignapi.mall.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 标签表
 * </p>
 *
 * @author ${author}
 * @since 2020-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DicLable implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 商场code
     */
    private String mallCode;

    /**
     * 标签类型
PRODUCTION：商品
SHOP：商家
     */
    private String lableType;

    /**
     * 标签属于
例如：标签属于商品id
标签属于商家id
     */
    private Integer lableBelong;

    /**
     * 标签的值
     */
    private String lableValue;

    /**
     * 排序
     */
    private String sortNum;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;


}
