package com.ht.feignapi.yz.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WoWorkOrders implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 工单表头
     */
    private String woCode;

    /**
     * 工单名称
     */
    private String woName;

    /**
     * 内容简述
     */
    private String resume;

    /**
     * 详情
     */
    private String comments;

    /**
     * 保密等级：0：不保密，1及以上内容需要登陆访问
     */
    private Integer flagSecret;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private String state;

    /**
     * 权重
     */
    private String weight;

    /**
     * 工单分配人
     */
    private String assignee;

    /**
     * 工单分配时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date assignDate;

    /**
     * 创建人
     */
    private String createBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    /**
     * 附件1
     */
    private String fileUrl01;

    /**
     * 附件2
     */
    private String fileUrl02;

    private String fileUrl03;

    private String creator;

}
