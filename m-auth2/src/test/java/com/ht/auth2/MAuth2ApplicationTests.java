package com.ht.auth2;

import com.ht.auth2.config.JWTKeyConfig;
import com.ht.auth2.entity.UserGroupsTree;
import com.ht.auth2.mapper.UserGroupsMapper;
import com.ht.auth2.service.UserMapGroupRoleService;
import com.ht.auth2.service.UserMapUserGroupService;
import com.ht.auth2.service.UserRolesService;
import com.ht.auth2.service.UserUsersService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
class MAuth2ApplicationTests {


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private UserMapUserGroupService userMapUserGroupService;

    @Autowired
    private UserMapGroupRoleService userMapGroupRoleService;

    @Autowired
    private UserRolesService userRolesService;

    /**
     * 密码加密
     */
    @Test
    public void passwordEncode() {
        System.out.println(passwordEncoder.encode("123456"));
    }


    /**
     * 查询用户角色编码
     */
    @Test
    public void queryRoleCodeSet() {
        Set set=userMapGroupRoleService.queryRoleCodeSet("account");
    }

    /**
     * 解析jwt
     */
    @Test
    public void pasJwt(){
        Claims body = Jwts.parser()
                .setSigningKey(JWTKeyConfig.JWT_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbImFsbCJdLCJleHAiOjE1OTU0OTI0NzMsImF1dGhvcml0aWVzIjpbIjIwMDEiLCIyMDAzIiwiMjAwMiJdLCJqdGkiOiJmMjQ0Yzg4Yy03YzM0LTRhNWEtOTdjZC1jMmE4OWZmY2EwMDUiLCJjbGllbnRfaWQiOiJodWFsaWFudGlhbmFkbWluIiwiZW5oYW5jZSI6ImVuaGFuY2UgaW5mbyJ9.CngVkhEM3he_m86-fJMwrrImh0qvroSPpfLyfZIpF_g")
                .getBody();
        System.out.println(body);
    }

//    @Autowired
//    private UserGroupsMapper userGroupsMapper;
//
//    /**
//     * 角色分组
//     */
//    @Test
//    public void queryGroupTree(){
//        List<UserGroupsTree> userGroupsTrees = userGroupsMapper.queryUserGroupsTree();
//        for (UserGroupsTree userGroupsTree : userGroupsTrees) {
//            System.out.println(userGroupsTree);
//            List<UserGroupsTree> children = userGroupsTree.getChildren();
//            System.out.println(children);
//        }
//    }


}
