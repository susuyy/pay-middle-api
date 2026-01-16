package com.ht.merchant.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-11-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MrcMerchantCandidates implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 访问联系人
     */
    private String visitorName;

    /**
     * 访问人联系方式
     */
    private String visitorTel;

    /**
     * 访问人邮箱
     */
    private String visitorMail;

    /**
     * 访问人联系地址
     */
    private String visitorAddress;

    /**
     * 状态
     */
    private String state;

    /**
     * 类型
     */
    private String type;

    private String adminMerchantCode;

    @JsonFormat(pattern = "yyyy:MM:dd HH:mm:ss" , timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private Date createAt;

    @JsonFormat(pattern = "yyyy:MM:dd HH:mm:ss" , timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private Date updateAt;


}
