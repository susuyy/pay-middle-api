package com.ht.feignapi.tonglian.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageData implements Serializable {

    /**
     * 消息内容
     */
    private String messageBody;

    /**
     * 队列名称
     */
    private String queueName;
}
