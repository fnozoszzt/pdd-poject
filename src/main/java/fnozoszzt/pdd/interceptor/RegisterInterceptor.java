//package com.netease.bdms.dsweb.interceptor;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//import java.nio.charset.Charset;
//import java.util.List;
//
//@Configuration
//@Component
//public class RegisterInterceptor extends WebMvcConfigurationSupport{
//
//
//    @Autowired
//    SessionInterceptor sessionInterceptor;
//
//    @Override
//    public void addInterceptors (InterceptorRegistry registry) {
//
//       //registry.addInterceptor(new SessionInterceptor());
//
//        registry.addInterceptor(sessionInterceptor);
//
//       super.addInterceptors(registry);
//    }
//}
