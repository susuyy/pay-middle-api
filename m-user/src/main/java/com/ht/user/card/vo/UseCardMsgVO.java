package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UseCardMsgVO implements Serializable {

    /**
     * 使用卡券信息
     */
    private String useCardMessage;

    /**
     * 使用卡券标识
     */
    private Boolean useCardFlag;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 卡编号
     */
    private String cardNo;

    /**
     * 限制条件
     */
    private List LimitList;
}
