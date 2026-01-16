package com.ht.feignapi.auth.controller;

import com.ht.feignapi.auth.entity.LoginData;
import com.ht.feignapi.auth.entity.UserAndTokenData;
import com.ht.feignapi.auth.entity.UserVO;
import com.ht.feignapi.auth.service.impl.BoWeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/bowei")
@CrossOrigin(allowCredentials = "true")
public class BoWeiDemoController {

    @Autowired
    private BoWeiService boWeiService;

    /**
     * 用户登录 (第三方授权)
     * @param loginData
     * @return
     * @throws Exception
     */
    @PostMapping("/login")
    public UserAndTokenData login(@RequestBody LoginData loginData) throws Exception {
        UserAndTokenData userAndTokenData = boWeiService.login(loginData);
        return userAndTokenData;
    }

    /**
     * 查询用户信息
     * @return
     * @throws Exception
     */
    @GetMapping("/getUserMsg")
    public UserVO queryUserMsg() throws Exception {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            //取出request
            HttpServletRequest request = attributes.getRequest();
            String jwtToken = request.getHeader("Blade-Auth");
            String parseJwt=jwtToken.split(" ")[1];
            UserVO userVO = boWeiService.queryUserMsg(parseJwt);
            return userVO;
        }
        return new UserVO();
    }
}
