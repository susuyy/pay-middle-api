package com.ht.feignapi.quiz.service;


import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.quiz.vo.ExamCandidateRegistrationVo;
import com.ht.feignapi.quiz.vo.ExamNoticeVo;
import com.ht.feignapi.result.Result;
import com.ht.quiz.examroom.vo.ExamMakeUpVo;

import java.security.Principal;

public interface QuizService {



    /**
     * m-auth2 考试创建用户
     * @param userUsers
     * @return
     */
    Result register(UserUsers userUsers);


    /**
     * auth2 考试用户信息
     * @param user
     * @return
     */
    Result user(Principal user);

    /**
     * 审核报名状态
     * @param examRegistrationVo
     * @return
     */
    Result modifyStatus(ExamCandidateRegistrationVo examRegistrationVo);


    /**
     * 考试下发通知
     * @param examNoticeVo
     * @return
     */
    Result noticeExamMsg(ExamNoticeVo examNoticeVo);

    /**
     * 审核补考状态
     * @param examMakeUpVo
     * @return
     */
    Result setExamMakeUpStatus(ExamMakeUpVo examMakeUpVo);

    /**
     * 领证下发通知
     * @param examNoticeVo
     * @return
     */
    Result noticeExamCertificateMsg(ExamNoticeVo examNoticeVo);




}
