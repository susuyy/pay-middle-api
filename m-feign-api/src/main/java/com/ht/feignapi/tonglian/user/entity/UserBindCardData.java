package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户绑卡前端提交参数
 *
 * @author suyangy
 * @since 2020-06-15
 */
@Data
public class UserBindCardData implements Serializable {

    /**
     * 手机号
     */
    @NotNull
    private String phoneNum;

    /**
     * 验证码
     */
    private String authCode;

    /**
     * 卡号
     */
    private String icCardId;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡分类编码
     */
    private String categoryCode;

    /**
     * 卡分类名称
     */
    private String categoryName;

    /**
     * 卡状态
     */
    private String state;

    /**
     * 卡类型
     */
    private String type;

    /**
     * 商家编码
     */
    private String merchantCode;

    /**
     * 虚拟卡号
     */
    @NotNull
    private String cardNo;
}
