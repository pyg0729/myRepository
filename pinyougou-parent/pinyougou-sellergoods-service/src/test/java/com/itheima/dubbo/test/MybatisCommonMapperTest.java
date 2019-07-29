package com.itheima.dubbo.test;


import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/*
 *
 *    @苑帅
 *    @时间为：2019-06-22-19-59
 *
 */
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class MybatisCommonMapperTest {
/*

    @Autowired
    TbBrandMapper tbBrandMapper;

    //增
    @Test
    public void test() {

        TbBrand tbBrand = new TbBrand();
        tbBrand.setName("IC");
        tbBrand.setFirstChar("I");
        tbBrandMapper.insert(tbBrand);
    }

    //删
    @Test
    public void test1(){
        tbBrandMapper.deleteByPrimaryKey(33L);
    }

    //改
    @Test
    public void test2(){
        TbBrand tbBrand = new TbBrand();
        tbBrand.setName("IQ");
        tbBrand.setFirstChar("I");
        tbBrand.setId(33L);
        //有值就更新没有就不赋空
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    //查
    @Test
    public void test3(){
        //  查询所有数据，Example就是表示where条件

        //selectByExample 就类似 select * from tb_brand
        //Example 就是where条件
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo("IQ");

        //List<TbBrand> tbBrands = tbBrandMapper.selectByExample(example);

        for (TbBrand tbBrand : tbBrands) {
            System.out.println(tbBrand.getName());
        }
    }
*/


    @Autowired
    TbBrandMapper tbBrandMapper;

    @Test
    public void Test() {
        List<TbBrand> all = tbBrandMapper.findAll();
        for (TbBrand tbBrand : all) {
            System.out.println(tbBrand.getName());
        }
    }





}
