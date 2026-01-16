package com.ht.user.card.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PosUserCardVO {

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 卡分类编码
     */
    private String categoryCode;

    /**
     * 卡分类名称
     */
    private String categoryName;



    /**
     * 卡面值
     */
    private Integer faceValue;

    /**
     * 卡次数
     */
    private Integer batchTimes;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 有效开始时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date validFrom;

    /**
     * 有效结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date validTo;

    /**
     * 卡类型
     */
    private String type;

    /**
     * 卡状态
     */
    private String state;

    /**
     * 卡编号
     */
    private String cardNo;

    /**
     * 实体卡卡号
     */
    private String icCardId;

    /**
     * 标识默认选中
     */
    private Boolean defaultSelect = false;

    /**
     * 卡模板类型
     */
    private String cardCardsType;

    /**
     * 卡模板类型
     */
    private String cardCardsState;
}
