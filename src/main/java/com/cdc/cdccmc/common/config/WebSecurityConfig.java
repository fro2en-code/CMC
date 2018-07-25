package com.cdc.cdccmc.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import com.cdc.cdccmc.common.interceptor.MyFilterSecurityInterceptor;
import com.cdc.cdccmc.service.sys.CustomUserService;

//@Configuration
//@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//	@Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;
	
//    @Bean
    UserDetailsService customUserService(){ //注册UserDetailsService 的bean
        return new CustomUserService();
    }
//    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService()); //user Details Service验证
    }
//    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//        		//静态资源
//        		.antMatchers("/css/*","/image/*").permitAll()
//        		//任何请求,登录后可以访问
//                .anyRequest().authenticated() 
//                .and()
//                //登录页面
//                .formLogin().loginPage("/login")
//                //登录成功默认跳转页面
//                .defaultSuccessUrl("/home")
//                //登录失败页面跳转
//                .failureUrl("/login?error")
//                //登录页面用户任意访问
//                .permitAll() 
//                .and()
//                .rememberMe().tokenValiditySeconds(1209600).key("cdccmc")
//                .and()
//                .logout().permitAll(); //注销行为任意访问
//        http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
    }
}