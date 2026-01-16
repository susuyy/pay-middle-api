package com.ht.user.card.vo;

import com.ht.user.card.entity.CardLimits;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserCardVO implements Serializable {

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
     * 卡券类型,计次券,金额券,折扣券等等
     */
    private String cardCardsType;

    /**
     * 有效开始时间
     */
    private Date validFrom;

    /**
     * 有效结束时间
     */
    private Date validTo;

    /**
     * 卡类型
     */
    private String type;

    /**
     * 卡编号
     */
    private String cardNo;

    /**
     * 实体卡卡号
     */
    private String icCardId;

    /**
     * 使用须知
     */
    private String notice;

    //前端展示时间段
    private String showTimeScope;

    /**
     * 创建时间
     */
    private Date createAt;
}
