package com.ht.feignapi.policydocs.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SubItemCategoryFileVo {


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
     * 文件内容
     */
    private String content;

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
