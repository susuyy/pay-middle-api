package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class CardMapUserCardsVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 卡编码
     */
    private String cardNo;

    /**
     * 实体卡卡号
     */
    private String icCardId;

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

    /**
     * 卡分配状态
     */
    private String state;

    /**
     * 卡分配类型
     */
    private String type;

    private Date createAt;

    private Date updateAt;

    private String cardFaceValue;

    private String price;

    private String onShelfState;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 卡模板类型
     */
    private String cardCardsType;
}
