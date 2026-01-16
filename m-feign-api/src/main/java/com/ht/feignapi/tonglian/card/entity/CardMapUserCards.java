package com.ht.feignapi.tonglian.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ht.feignapi.appshow.entity.MallCoupon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

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
     * 源卡类型 计次券 金额券 折扣券 满减券
     */
    private String cardType;

    private Date createAt;

    private Date updateAt;

    private String icCardId;

    private MallCoupon mallCoupon;

    private String validityType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date validFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date validTo;

    private Integer periodOfValidity;

    private Integer validGapAfterApplied;

    private String cardPicUrl;
}
