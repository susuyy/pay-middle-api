package com.ht.merchant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("mrc_merchants_config")
public class MerchantsConfig implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;


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

    private String ext1;

    private String ext2;

    private String ext3;

    /**
     * 分组
     */
    private String groupCode;

    /**
     * 分组类型
     */
    private String type;

    private Date createAt;

    private Date updateAt;


}
