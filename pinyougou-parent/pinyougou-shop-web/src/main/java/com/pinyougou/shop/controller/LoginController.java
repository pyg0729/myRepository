package com.pinyougou.shop.controller;
/*
 *
 *    @苑帅
 *    @时间为：2019-06-27-10-30
 *
 */

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getName")
    public String getName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
