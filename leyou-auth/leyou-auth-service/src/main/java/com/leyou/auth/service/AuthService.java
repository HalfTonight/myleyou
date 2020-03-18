package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.propertties.JwtProperties;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.controller.UserController;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String thCreat(String username, String password) {

        //调用远程服务，查询用户

        User user = userClient.queryUser(username, password);
            if(user==null){
                return null;
            }
        try {
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()),
                    jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
