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
@TableName("card_activity")
public class Activity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private static final long serialVersionUID=1L;

    /**
     * 活动code
     */
    private String activityCode;

    /**
     * 主体号，目前只有主体可以开活动
     */
    private String merchantCode;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动类型
     */
    private String type;

    /**
     * 活动状态:enable可用，disable不可用
     */
    private String state;

    /**
     * 活动开始时间
     */
    private Date validFrom;

    /**
     * 活动结束时间
     */
    private Date validTo;

    private Date createAt;

    private Date updateAt;

    /**
     * 优先级字段，数值越小，优先级越高
     */
    private Integer priority;

    /**
     * 活动规则
     */
    @TableField(exist = false)
    private List<ActivityLimit> limitList;
}
