package com.ht.user.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 用户，卡绑定关系
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_map_user_cards")
public class CardMapUserCards implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 具体卡号
     */
    private String cardNo;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡分类编码
     */
    private String categoryCode;

    /**
     * 卡分类名称
     */
    private String categoryName;

    private String batchCode;

    /**
     * 来源标识
     */
    private String refSourceKey;

    /**
     * 卡面值
     */
    private String faceValue;

    /**
     * 卡分配状态
     */
    private String state;

    /**
     * 卡分配类型
     */
    private String type;

    /**
     * 源卡类型  计次券 金额券 满减券 折扣券
     */
    private String cardType;

    private Date createAt;

    private Date updateAt;

    private String icCardId;

    @TableField(exist = false)
    private String validityType;

    @TableField(exist = false)
    private Date validFrom;

    @TableField(exist = false)
    private Date validTo;

    @TableField(exist = false)
    private Integer periodOfValidity;

    @TableField(exist = false)
    private Integer validGapAfterApplied;

    @TableField(exist = false)
    private String cardPicUrl;
}
