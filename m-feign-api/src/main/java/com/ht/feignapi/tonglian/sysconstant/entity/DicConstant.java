package com.ht.feignapi.tonglian.sysconstant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DicConstant implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * key
     */
    @TableField(value = "`key`")
    private String key;

    /**
     * value
     */
    @TableField(value = "`value`")
    private String value;

    /**
     * 备注
     */
    private String comments;

    /**
     * 分组
     */
    private String groupCode;

    /**
     * 业务模型
     */
    private String model;

    /**
     * 应用
     */
    private String application;

    private Date createAt;

    private Date updateAt;


}
