package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *
 *    @苑帅
 *    @时间为：2019-06-26-21-07
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getName")
    public String getLoginName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
