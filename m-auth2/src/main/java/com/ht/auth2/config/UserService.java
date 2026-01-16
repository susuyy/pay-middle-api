package com.ht.auth2.config;

import com.ht.auth2.entity.UserUsers;
import com.ht.auth2.service.UserMapGroupRoleService;
import com.ht.auth2.service.UserRolesService;
import com.ht.auth2.service.UserUsersService;
import com.ht.auth2.utils.UserJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private UserMapGroupRoleService userMapGroupRoleService;

    @Autowired
    private UserRolesService userRolesService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        //根据用户名查询用户信息
        UserUsers userUsers = userUsersService.queryByAccountNoCode(username);
        Set<String> set = userMapGroupRoleService.queryRoleCodeSet(userUsers.getAccount());
        Set<String> typeSet = userRolesService.querySetType(set);

        ArrayList<String> roleTypeList = new ArrayList<>(typeSet);
        String permissions = roleTypeList.get(0)+",";
        for (int i = 1; i < roleTypeList.size(); i++) {
            if (i ==  roleTypeList.size()) {
                permissions = permissions + roleTypeList.get(i);
            }else {
                permissions = permissions + roleTypeList.get(i) + ",";
            }
        }
        UserJwt userDetails = new UserJwt(username, userUsers.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        return userDetails;
    }

}
