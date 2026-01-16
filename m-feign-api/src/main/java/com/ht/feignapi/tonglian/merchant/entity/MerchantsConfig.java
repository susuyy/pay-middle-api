package com.ht.feignapi.tonglian.merchant.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class MerchantsConfig implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 商家编码
     */
    private String merchantCode;

    /**
     * 商家配置
     */
    private String key;

    /**
     * 商家配置值
     */
    private String value;

    /**
     * 分组
     */
    private String groupCode;

    /**
     * 分组类型
     */
    private String type;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
