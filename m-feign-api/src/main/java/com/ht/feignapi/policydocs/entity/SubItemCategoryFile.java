package com.ht.feignapi.policydocs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 子类目项文件
 * </p>
 *
 * @author ${author}
 * @since 2020-12-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SubItemCategoryFile implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;


    /**
     * 类目编码
     */
    private String categoryCode;

    /**
     * 类目名称
     */
    private String categoryTitle;


    /**
     * 子类目编码
     */
    private String subCategoryCode;


    /**
     * 子类目标题
     */
    private String subCategoryTitle;


    /**
     * 子类目项目编码
     */
    private String subItemCategoryCode;

    /**
     * 子类目项标题
     */
    private String subItemCategoryTitle;


    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件目录
     */
    private String fileDirectory;

    /**
     * 文件内容地址
     */
    private String fileValue;


    /**
     * 是否同步到es标识
     */
    private Boolean esFlag=false;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;




}
