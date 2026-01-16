package com.ht.feignapi.yz.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

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
public class WoWorkOrderDetails implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 工单编号
     */
    private String woCode;

    /**
     * 创建人
     */
    private String creater;

    /**
     * 分配人
     */
    private String assignee;

    /**
     * 内容说明
     */
    private String comments;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private String state;

    /**
     * 附件1
     */
    private String fileUrl01;

    /**
     * 附件2
     */
    private String fileUrl02;

    private String fileUrl03;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
