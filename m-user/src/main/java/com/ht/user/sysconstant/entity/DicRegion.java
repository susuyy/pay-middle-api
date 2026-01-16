package com.ht.user.sysconstant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DicRegion implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId
    private Long id;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 区域上级标识
     */
    private Integer pid;

    /**
     * 地名简称
     */
    private String sname;

    /**
     * 区域等级
     */
    private Integer level;

    /**
     * 区域编码
     */
    private String citycode;

    /**
     * 邮政编码
     */
    private String yzcode;

    /**
     * 组合名称
     */
    private String mername;

    private Float longitude;

    private Float latitude;

    private String pinyin;


}
