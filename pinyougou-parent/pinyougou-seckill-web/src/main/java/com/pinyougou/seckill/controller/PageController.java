package com.pinyougou.seckill.controller;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-19-11-20
 *
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/page/login")
    public String showPage(String url) {
        return "redirect:"+url;
    }

}
