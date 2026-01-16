package com.ht.user.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_activity_limit")
public class ActivityLimit implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private static final long serialVersionUID=1L;

    /**
     * 类型
     */
    private String type;

    /**
     * 活动code
     */
    private String activityCode;

    /**
     * 限制key
     */
    private String limitKey;

    /**
     * 状态
     */
    private String state;

    private Date createAt;

    private Date updateAt;

    /**
     * 优先级：数值越大，优先级越搞
     */
    private String priority;

    /**
     * 该门槛对应的卡券以及数目
     * 卡券code和数量的映射关系
     */
    @TableField(exist = false)
    private Map<String,Integer> cardCountMap;

    /**
     * 每个规则对应的卡券信息，不包含数目
     */
    @TableField(exist = false)
    private List<ActivityLimitMapCard> cardsList;

    @TableField(exist = false)
    private String merchantCode;
}
