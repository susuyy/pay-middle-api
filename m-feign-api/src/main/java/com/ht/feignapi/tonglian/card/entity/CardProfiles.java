package com.ht.feignapi.tonglian.card.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2020-07-28
 */
@Data
public class CardProfiles implements Serializable {

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 属性key
     */
    private String key;

    /**
     * 属性值
     */
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
