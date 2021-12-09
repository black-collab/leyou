package com.leyou.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class Cors {

    //配置cors拦截器
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration corsConf = new CorsConfiguration();
        //设置cors允许使用所有请求头、所有请求方式、所有网站进行跨域、运行携带cookie
        corsConf.addAllowedHeader("*");
        corsConf.addAllowedMethod("*");
        corsConf.addAllowedOrigin("http://www.leyou.com");
        corsConf.addAllowedOrigin("http://manage.leyou.com");
        corsConf.setAllowCredentials(true);
        //配置cors数据源
        UrlBasedCorsConfigurationSource corsSourceConfig = new UrlBasedCorsConfigurationSource();
        //所有请求都会被拦截
        corsSourceConfig.registerCorsConfiguration("/**",corsConf);
        return new CorsFilter(corsSourceConfig);
    }
}
