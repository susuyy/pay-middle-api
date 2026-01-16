package com.ht.feignapi;

import com.aliyuncs.exceptions.ClientException;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author: zheng weiguang
 * @Date: 2021/3/1 11:14
 */
@SpringBootTest
public class MainTest {
    @Autowired
    private UserUsersService userUsersService;

    @Test
    public void contextLoads() throws Exception {
            //暂留阿甘电话
//        userUsersService.sendMsg15067089660("sendMsg15067089660","SMS_212275616");
    }

}
