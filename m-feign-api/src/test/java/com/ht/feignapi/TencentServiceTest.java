package com.ht.feignapi;

import com.aliyuncs.exceptions.ClientException;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tencent.service.TencentCosService;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.util.ParseResultUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/14 11:47
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TencentServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(TencentServiceTest.class);

    @Autowired
    private UserUsersService userUsersService;

    @Test
    public void contextLoads() throws Exception {
//        try {
//            //暂留阿甘电话
//            userUsersService.sendMsg("15067089660","SMS_212275766");
//        }catch (ClientException e){
//            logger.error("发送短信失败: " + e.toString());
//        }
    }


    @Test
    public void testJwt() throws Exception {
        Claims body = Jwts.parser()
                .parseClaimsJws("eyJ0eXAiOiJKc29uV2ViVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpc3N1c2VyIiwiYXVkIjoiYXVkaWVuY2UiLCJ0ZW5hbnRfaWQiOiIwMDAwMDAiLCJyb2xlX25hbWUiOiJhZG1pbmlzdHJhdG9yIiwicG9zdF9pZCI6IjExMjM1OTg4MTc3Mzg2NzUyMDEsMTEyMzU5ODgxNzczODY3NTIwMiIsInVzZXJfaWQiOiIxMjgzNTk5MjQ5NDAxODIzMjM0Iiwicm9sZV9pZCI6IjExMjM1OTg4MTY3Mzg2NzUyMDEiLCJ1c2VyX25hbWUiOiJobHRhbWFuYWdlciIsIm5pY2tfbmFtZSI6IueuoeeQhuS6uuWRmCIsInRva2VuX3R5cGUiOiJhY2Nlc3NfdG9rZW4iLCJkZXB0X2lkIjoiMTEyMzU5ODgxMzczODY3NTIwMSIsImFjY291bnQiOiJobHRhbWFuYWdlciIsImNsaWVudF9pZCI6IjAwMSIsImV4cCI6MTU5NDk2MTg4MCwibmJmIjoxNTk0OTUxMDgwfQ.z6RtxoMI1CYfVJnAnVmuPXASzIGuG_fsps59CYwEkks")
                .getBody();
        System.out.println(body);
    }

}
