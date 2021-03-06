package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
public interface UserApi {
    @RequestMapping(value = "query" ,method = RequestMethod.GET)
    public User queryUser(@RequestParam("username")String username,@RequestParam("password")String password);
}


