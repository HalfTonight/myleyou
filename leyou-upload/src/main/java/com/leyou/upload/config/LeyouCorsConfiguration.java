package com.leyou.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfiguration {
    @Bean
    public CorsFilter corsFilter(){

        CorsConfiguration configSource=new CorsConfiguration();

        configSource.addAllowedOrigin("Http://manage.leyou.com");//允许当前域名进行跨域请求
        configSource.addAllowedMethod("*");
        configSource.addAllowedHeader("*");//允许携带任何的头信息
        configSource.setAllowCredentials(true);//允许携带cooke

        //初始化Cros对象
        UrlBasedCorsConfigurationSource  configurationSource=new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",configSource);
        CorsFilter corsFilter = new CorsFilter(configurationSource);

        return corsFilter;
    }
}
