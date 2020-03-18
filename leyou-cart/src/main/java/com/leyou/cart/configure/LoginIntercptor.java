package com.leyou.cart.configure;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.properties.JwtProperties;
import com.leyou.commom.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginIntercptor extends HandlerInterceptorAdapter {
    private static final ThreadLocal<UserInfo> THREAD_LOCAL=new ThreadLocal<>();
    @Autowired
    private JwtProperties jwtProperties;
    /*
     * @Description: 给访问的用户分配线程，ThreadLocal是Thread的一个变量
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/29
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookeName());
        //获取UserInfo对象
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        if(userInfo==null){
            return false;
        }
        //将userInfo放在THREAD_LOCAL中
        THREAD_LOCAL.set(userInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //释放这个变量，防止线程不安全
        THREAD_LOCAL.remove();
    }
    /*
     * @Description: 供外界获取UserInfo
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/29
    */
    public static UserInfo getUserInfo() {
        return THREAD_LOCAL.get();
    }
}
