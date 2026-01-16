package com.ht.feignapi.pay.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2020-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PayConfig implements Serializable {

    private static final long serialVersionUID=1L;

    private String merchantCode;

    @TableField(value = "`key`")
    private String key;

    @TableField(value = "`value`")
    private String value;

    private String groupCode;

    private Date createAt;

    private Date updateAt;


}
