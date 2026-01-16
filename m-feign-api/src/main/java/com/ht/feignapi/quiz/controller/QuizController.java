package com.ht.feignapi.quiz.controller;

import com.ht.feignapi.auth.entity.*;
import com.ht.feignapi.quiz.client.QuizClientService;
import com.ht.feignapi.quiz.service.QuizService;
import com.ht.feignapi.quiz.service.impl.QuizServiceImpl;
import com.ht.feignapi.quiz.vo.ExamCandidateRegistrationVo;
import com.ht.feignapi.quiz.vo.ExamNoticeVo;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.result.UserDefinedException;
import com.ht.quiz.examroom.vo.ExamMakeUpVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping(value = "/quiz",produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizClientService quizClientService;

    private static final Logger log = LoggerFactory.getLogger(QuizServiceImpl.class);

    /**
     * 用户注册 考试创建用户
     * @param userUsers
     */
    @PostMapping("/userUsers/register")
    public Result register(@RequestBody UserUsers userUsers) {
        return quizService.register(userUsers);
    }


    /**
     * 考试用户信息
     * @param user
     */
    @GetMapping("/userUsers/user")
    public Result user(Principal user) {
        return quizService.user(user);
    }


    /**
     * 根据微信openid 个人认证信息
     * @param openid 微信openid
     * @return Result
     */
    @GetMapping("/openid/{openid}")
    public Result getUserRealNameAuthByOpenid(@PathVariable(value = "openid", required = true) String openid) {
        return quizClientService.getUserRealNameAuthByOpenid(openid);
    }

    /**
     * 后台审核报名状态
     * @param candidateRegistrationVo
     */
    @PostMapping("/examCandidateRegistration/modify/status/new")
    public Result modifyStatus(@RequestBody ExamCandidateRegistrationVo candidateRegistrationVo) {
        log.info("modifyStatus candidateRegistrationVo={}",candidateRegistrationVo);
        return quizService.modifyStatus(candidateRegistrationVo);
    }


    /**
     * 后台考试下发通知
     * @param examNoticeVo
     */
    @PostMapping("/examCandidateNoticeMsg/exam/notice/new")
    public Result noticeExamCandidateRegistration(@RequestBody ExamNoticeVo examNoticeVo) {
        log.info("noticeExamCandidateRegistration examNoticeVo={}",examNoticeVo);
        return quizService.noticeExamMsg(examNoticeVo);
    }


    /**
     * 后台领证下发通知
     * @param examNoticeVo
     */
    @PostMapping("/examCandidateNoticeMsg/exam/certificate/notice/new")
    public Result noticeExamCandidateCertificate(@RequestBody ExamNoticeVo examNoticeVo) {
        log.info("noticeExamCandidateCertificate examNoticeVo={}",examNoticeVo);
        return quizService.noticeExamCertificateMsg(examNoticeVo);
    }


    /**
     * 后台补考审核报名状态
     * @param examMakeUpVo
     */
    @PostMapping("/examMakeUp/setExamMakeUpStatus")
    public Result setExamMakeUpStatus(@RequestBody ExamMakeUpVo examMakeUpVo) {
        log.info("setExamMakeUpStatus examMakeUpVo={}",examMakeUpVo);
        return quizService.setExamMakeUpStatus(examMakeUpVo);
    }


    /**
     * get测试
     * @return
     */
    @GetMapping("/test/aes/decode/get3")
    public Result testAesDecodeGet3() {
        try{
            ExamCandidateRegistrationVo candidateRegistrationVo = new ExamCandidateRegistrationVo();
            candidateRegistrationVo.setId(1L);
            candidateRegistrationVo.setStatus(1);
            log.info("modifyStatus testAesDecodeGet3 candidateRegistrationVo={}",candidateRegistrationVo);
            return quizService.modifyStatus(candidateRegistrationVo);
        }catch (Exception e){
            log.error("testAesDecodeGet3 error e={}",e.getMessage());
            throw new UserDefinedException(ResultTypeEnum.SERVICE_ERROR,e.getMessage());
        }
    }

}
