package com.ht.user.admin.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/21 11:20
 */
@Data
public class CardEditVo implements Serializable {

    @NotNull
    private String type;

    @NotNull
    private List<String> merchantCodes;

    @NotNull
    private String cardName;

    @NotNull
    private Integer faceValue;
    private String validType;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date validFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date validTo;

    private Boolean flagTransfer;
    /**
     * 次数卡，卡的次数
     */
    private Integer batchTimes;

    /**
     * 次数卡，单位
     */
    private String unit;
    /**
     * 获取后几天生效
     */
    private Integer activeFromGet;

    /**
     * 生效后有效天数
     */
    private Integer effectFromActive;

    private List<String> limitDay;
    private List<String> limitWeek;
    private List<String> limitHour;

    private List<String> profiles;

    private String cardPicUrl;

    @NotNull
    private String notice;
    /**
     * 每次限用
     */
    private Integer limitUnit;
    /**
     * 门槛
     */
    private Integer limitTotal;
}

