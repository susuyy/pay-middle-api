package com.ht.user.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2020-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_activity_limit_map_card")
public class ActivityLimitMapCard implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private static final long serialVersionUID=1L;

    /**
     * 卡号
     */
    private String cardCode;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 活动号
     */
    private String activityCode;

    private String limitKey;

    /**
     * 数目
     */
    private Integer amount;

    private Date createAt;

    private Date updateAt;


}
