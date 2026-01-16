package com.ht.feignapi.auth.entity;

import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class SearchData implements Serializable {

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 应用编码
     */
    @NotNull(message = "appCode参数不能为空")
    private String appCode;

    private Date startTime;

    private Date endTime;

    private Integer pageNo;

    private Integer pageSize;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 账号
     */
    private String account;

    /**
     * 昵称
     */
    private String nickName;
}
