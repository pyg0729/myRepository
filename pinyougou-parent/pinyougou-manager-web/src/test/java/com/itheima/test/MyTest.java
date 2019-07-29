package com.itheima.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/*
 *
 *    @苑帅
 *    @时间为：2019-06-23-22-20
 *
 */
@ContextConfiguration("classpath:spring/springmvc.xml")
@RunWith(SpringRunner.class)
public class MyTest {

    @Reference
    BrandService brandService;

    @Test
    public void test() {

        TbBrand brand = new TbBrand();
        PageInfo<TbBrand> list = brandService.findPage(1,10,brand);

        System.out.println(list);


    }
}
