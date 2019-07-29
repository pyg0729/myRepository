package com.pinyougou.shop.config;
/*
 *
 *    @苑帅
 *    @时间为：2019-06-26-20-04
 *
 */

import com.pinyougou.shop.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //1.拦截请求  admin/**
        http.authorizeRequests()
                //2.放行 css js login.html
                .antMatchers("/css/**","/img/**","/js/**","/plugins/**","/*.html","/seller/add.shtml").permitAll()
                .anyRequest().authenticated();
        //3.配置自定义的表单的登录
        http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/shoplogin.html?error");
        //禁用Csrf
        http.csrf().disable();

        //同源访问策略
        http.headers().frameOptions().sameOrigin();

        //配置退出
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);

    }
}
