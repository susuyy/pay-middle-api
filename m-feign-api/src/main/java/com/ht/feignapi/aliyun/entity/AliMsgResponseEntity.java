package com.ht.feignapi.aliyun.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zheng weiguang
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AliMsgResponseEntity {
    private String message;
    private String requestId;
    private String bizId;
    private String code;
}
