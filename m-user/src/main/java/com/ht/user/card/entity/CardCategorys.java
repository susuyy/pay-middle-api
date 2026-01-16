package com.ht.user.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-06-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_categorys")
public class CardCategorys implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Long id;

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

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
