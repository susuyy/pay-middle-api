package com.ht.feignapi.quiz.vo;

import lombok.Data;


@Data
public class ExamCandidateNoticeRecordResultVo {

    private Long id;

    private String smsResultRemark;

    /**
     * 下发通知状态是 0=未下发消息 1=已下发消息
     */
    private Integer noticeStatus=0;

    /**
     * 报名审核状态  0=未审核 1=审核通过 2=审核未通过
     */
    private Integer status=0;

}
