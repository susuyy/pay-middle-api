package com.ht.feignapi.quiz.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class ExamRoomMapCandidateNewVo {

    private Long id;

    /**
     * 分配组编码yyyyMMddHHmmss
     */
    private Long assignGroupCode;

    /**
     * 考试编码
     */
    private String examBatchCode;

    /**
     * 考试名称
     */
    private String examBatchName;

    /**
     * 考试时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date examAt;

    /**
     * 考试截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endExamAt;
    /**
     * 考场对象ID
     */
    @TableField(value = "exam_room_id")
    private Long examRoomId;
    private String examRoomName;

    /**
     * 座位号
     */
    private String seatNumber;


    private Long examCandidateRegistrationId;
    private String candidateName;
    private String idCardNum;
    private String tel;


    /**
     * 准考证号
     */
    private String admissionTicketNumber;

    /**
     * 下发通知状态是 0=未下发消息 1=已下发消息
     */
    private Integer noticeStatus=0;

    private Integer flag=0;//1=参加考试

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    /**
     * 考场地址
     */
    private String address;

    /**
     * 地址详情
     */
    private String addressDetail;
}
