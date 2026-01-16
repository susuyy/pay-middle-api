package com.ht.user.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderCategorys implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主体编码
     */
    private String merchantCode;

    /**
     * 3级（末级）分类编码
     */
    private String categoryLevel03Code;

    /**
     * 3级（末级）分类名称
     */
    private String categoryLevel03Name;

    /**
     * 2级（中级）分类编码
     */
    private String categoryLevel02Code;

    /**
     * 2级（中级）分类名称
     */
    private String categoryLevel02Name;

    /**
     * 1级（顶级）分类编码
     */
    private String categoryLevel01Code;

    /**
     * 1级（顶级）分类名称
     */
    private String categoryLevel01Name;

    private Date createAt;

    private Date updateAt;


}
