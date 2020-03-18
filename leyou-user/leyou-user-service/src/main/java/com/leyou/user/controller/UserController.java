package com.leyou.user.controller;

import com.leyou.commom.utils.CookieUtils;
import com.leyou.user.pojo.User;

import com.leyou.user.propertties.JwtProperties;
import com.leyou.user.service.UserCodeService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpCookie;

@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class UserController {

    @Autowired
    private UserCodeService userService;

    @Autowired
    JwtProperties jwtProperties;

    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data,@PathVariable("type")Integer type){
        Boolean bool= this.userService.checkData(data,type);
        if(bool==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(bool);
    }
    @PostMapping("code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone")String phone){
        Boolean bool=this.userService.createAndSend(phone);
        if(bool==null||!bool){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("register")
    public ResponseEntity<Void> userRegister(@Validated User user, @RequestParam("code")String code){
        Boolean bool=this.userService.userRegidter(user,code);
        if(bool==null||!bool){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }
    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username")String username,@RequestParam("password")String password){
        User user=this.userService.queryByUser(username,password);
        int i=1;
        if(user==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
//    @PostMapping("acreat")
//    public ResponseEntity<Void> tkCreat(@RequestParam("username")String username,
//                                        @RequestParam("password")String password,
//                                        HttpServletRequest request,
//                                        HttpServletResponse response){
//        String token=this.userService.thCreat(username,password);
//        if(StringUtils.isBlank(token)){
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//        CookieUtils.setCookie(request,response,jwtProperties.getCookeName(),token,jwtProperties.getExpire()*60);
//        return ResponseEntity.ok().build();
//    }


}
