package com.ht.user.card.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 卡定义
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CardCardsVO implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡面值
     */
    private Integer faceValue;

    /**
     * 购卡价格
     */
    private Integer price;

    /**
     * 库存
     */
    private Integer inventory;

    /**
     * 开始时间
     */
    private Date validFrom;

    /**
     * 结束时间
     */
    private Date validTo;

    /**
     * 优惠券图片
     */
    private String cardPicUrl;

    /**
     * 详细说明
     */
    private String desc;

    /**
     * 商家图片
     */
    private String merchantsName;

    /**
     * 商家图片
     */
    private String merchantsPic;

    /**
     * 商家地址
     */
    private String merchantsAddress;

    /**
     * 商家手机号
     */
    private String merchantsPhone;

    /**
     * 限制支付类型
     */
    private String limitPayType;
}
