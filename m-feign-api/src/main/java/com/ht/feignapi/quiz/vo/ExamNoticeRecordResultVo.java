package com.ht.feignapi.quiz.vo;

import lombok.Data;


@Data
public class ExamNoticeRecordResultVo {

    private Long id;

    private String smsResultRemark;

    /**
     * 下发通知状态是 0=未下发消息 1=已下发消息
     */
    private Integer noticeStatus=0;

}
