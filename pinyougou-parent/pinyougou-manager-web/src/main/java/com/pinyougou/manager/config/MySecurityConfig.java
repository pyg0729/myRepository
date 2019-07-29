package com.pinyougou.manager.config;
/*
 *
 *    @苑帅
 *    @时间为：2019-06-26-20-04
 *
 */

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}123456").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //1.拦截请求  admin/**
        http.authorizeRequests()
                //2.放行 css js login.html
                .antMatchers("/css/**","/img/**","/js/**","/plugins/**","/login.html").permitAll()
                .anyRequest().authenticated();
        //3.配置自定义的表单的登录
        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/login.html?error");
        //禁用Csrf
        http.csrf().disable();

        //同源访问策略
        http.headers().frameOptions().sameOrigin();

        //配置退出
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);

    }
}
