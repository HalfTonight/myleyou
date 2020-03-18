//package com.leyou.gateway.filter;
//
//import com.leyou.auth.entity.UserInfo;
//import com.leyou.auth.utils.JwtUtils;
//import com.leyou.commom.utils.CookieUtils;
//import com.leyou.gateway.properties.JwtProperties;
//import com.leyou.gateway.properties.WhiteListProperties;
//import com.netflix.zuul.ZuulFilter;
//import com.netflix.zuul.context.RequestContext;
//import com.netflix.zuul.exception.ZuulException;
//import jdk.management.resource.ResourceContext;
//import org.apache.http.protocol.RequestContent;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//
//@Component
//@EnableConfigurationProperties({JwtProperties.class, WhiteListProperties.class})
//public class LoginFilter extends ZuulFilter {
//
//    @Autowired
//    private JwtProperties jwtProperties;
//
//    @Autowired
//    private WhiteListProperties whiteListProperties;
//
//
//    //过滤器类型
//    @Override
//    public String filterType() {
//        return "pre";
//    }
//
//    //优先级 设置注意保证可扩展性
//    @Override
//    public int filterOrder() {
//        return 2;
//    }
//
//    //是否要执行过滤器 也就是下个方法里的run方法
//    @Override
//    public boolean shouldFilter() {
//
//        RequestContext context = RequestContext.getCurrentContext();
//        // 获取request
//        HttpServletRequest req = context.getRequest();
//        // 获取路径
//        String requestURI = req.getRequestURI();
//         //判断白名单
//         //遍历允许访问的路径
//        for (String path : this.whiteListProperties.getWhiteList()) {
//            // 然后判断是否是符合
//            if(requestURI.startsWith(path)){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Object run() throws ZuulException {
//               //获取zuul网关运行上下文
//        RequestContext context = RequestContext.getCurrentContext();
//        HttpServletRequest request = context.getRequest();
//        //获取token
//        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookeName());
//        try {
//            //校验通过放行
//            JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
//        } catch (Exception e) {
//            // 校验出现异常，返回403
//            context.setSendZuulResponse(false);
//            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
//            e.printStackTrace();
//        }
//
//  return null;
//    }
//}
