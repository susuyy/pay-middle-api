package com.ht.user.card.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_card_profiles")
public class CardProfiles implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 属性key
     */
    @TableField(value = "`key`")
    private String key;

    /**
     * 属性值
     */
    @TableField(value = "`value`")
    private String value;

    /**
     * 属性分组
     */
    private String groupCode;

    /**
     * 状态
     */
    private String state;

    /**
     * 类型
     */
    private String type;

    private Date createAt;

    private Date updateAt;


}
