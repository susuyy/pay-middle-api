package com.ht.merchant.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class MerchantsConfigVO implements Serializable {


    /**
     * 商家编码
     */
    private String merchantCode;

    /**
     * 商家配置
     */
    @TableField(value = "`key`")
    private String key;

    /**
     * 商家配置值
     */
    @TableField(value = "`value`")
    private String value;



}
