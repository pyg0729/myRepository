package com.pinyougou;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-02-10-52
 *
 */


import com.pinyougou.es.service.ImportService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {


    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-es.xml");
        ImportService itemService = context.getBean(ImportService.class);
        itemService.ImportDataToEs();
    }

}
