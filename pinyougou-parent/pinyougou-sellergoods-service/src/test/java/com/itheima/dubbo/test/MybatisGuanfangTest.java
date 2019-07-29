package com.itheima.dubbo.test;


import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/*
 *
 *    @苑帅
 *    @时间为：2019-06-22-19-59
 *
 */
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class MybatisGuanfangTest {
/*

    @Autowired
    TbBrandMapper tbBrandMapper;

    //增
    @Test
    public void Test() {
        TbBrand tbBrand = new TbBrand();
        tbBrand.setName("IC");
        tbBrand.setFirstChar("I");
        tbBrandMapper.insert(tbBrand);
    }

    @Test
    public void Test1() {

        TbBrand tbBrand = new TbBrand();
        tbBrand.setName("IC");

        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name","IC");
        criteria.andEqualTo("id","35L");


        List<TbBrand> select = tbBrandMapper.selectByExample(example);
        System.out.println(select);
    }
*/


}
