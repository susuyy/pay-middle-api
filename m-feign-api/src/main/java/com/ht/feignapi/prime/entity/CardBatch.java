package com.ht.feignapi.prime.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-01-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CardBatch implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡面值
     */
    private Long cardFaceValue;

    /**
     * 批次,总大小
     */
    private Long batchSize;

    /**
     * 主体merchantCode
     */
    private String merchantCode;

    /**
     * 外部合作机构merchantCode;
     */
    private String refMerchantCode;

    private String sellAmount;

    /**
     * 卡分配类型
     */
    private String type;

    /**
     * 卡类型
     */
    private String cardType;

    /**
     * 卡图片
     */
    private String backGround;

    private Date createAt;

    private Date updateAt;


}
