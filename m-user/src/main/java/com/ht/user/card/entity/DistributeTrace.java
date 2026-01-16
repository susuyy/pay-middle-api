package com.ht.user.card.entity;

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
 * @since 2020-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_distribute_trace")
public class DistributeTrace implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 门店编码
     */
    private String merchantCode;

    /**
     * 销售员
     */
    private String salse;

    /**
     * 卡号
     */
    private String cardCode;

    @TableField(exist = false)
    private String cardName;

    @TableField(exist = false)
    private String cardType;

    /**
     * 发卡数量
     */
    private Integer amount;

    /**
     * 备注
     */
    private String comments;

    /**
     * 卡券来源
     */
    private String source;

    /**
     * 活动编号
     */
    private String activityCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createAt;

    private Date updateAt;

    private String batchCode;

}
