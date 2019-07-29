package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *
 *    @苑帅
 *    @时间为：2019-07-11-21-34
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name")
    public String getName() {
        //获取用户名
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
