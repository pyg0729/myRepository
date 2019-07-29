package com.pinyougou.core.service.impl;
/*
 *
 *    @苑帅
 *    @时间为：2019-06-25-09-47
 *
 */

import com.pinyougou.core.service.CoreService;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public abstract class CoreServiceImpl<T> implements CoreService<T> {

    //定义一个父类的Mapper
    protected Mapper<T> baseMapper;
    //定义一个要操作的POJO对应class
    protected Class<T> clazz;

    public CoreServiceImpl(Mapper<T> baseMapper, Class<T> clazz) {
        this.baseMapper = baseMapper;
        this.clazz = clazz;
    }

    @Override
    public List<T> findAll() {
        return selectAll();
    }

    @Override
    public int insert(T record) {
        return baseMapper.insert(record);
    }

    @Override
    public void add(T record) {
        baseMapper.insert(record);
    }

    @Override
    public int insertSelective(T record) {
        return baseMapper.insertSelective(record);
    }

    @Override
    public int delete(T record) {
        return baseMapper.delete(record);
    }

    @Override
    public void delete(Object[] ids) {


        Example example = new Example(clazz);
        Example.Criteria criteria = example.createCriteria();
        //反射
        Field[] declaredFields = clazz.getDeclaredFields();

        String name = "id";
        //获取字段名称
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(Id.class)) {
                name = declaredField.getName();
                break;
            }
        }
        criteria.andIn(name, Arrays.asList(ids));
        baseMapper.deleteByExample(example);

    }

    @Override
    public int deleteByPrimaryKey(Object key) {
        return baseMapper.deleteByPrimaryKey(key);
    }

    @Override
    public int deleteByExample(Object example) {
        return baseMapper.deleteByExample(example);
    }

    @Override
    public T selectOne(T record) {
        return baseMapper.selectOne(record);
    }

    @Override
    public T findOne(Object id) {
        return baseMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T> select(T record) {
        return baseMapper.select(record);
    }

    @Override
    public List<T> selectAll() {
        return baseMapper.selectAll();
    }

    @Override
    public void update(T record) {
        updateByPrimaryKey(record);
    }

    @Override
    public T selectByPrimaryKey(Object key) {
        return baseMapper.selectByPrimaryKey(key);
    }

    @Override
    public List<T> selectByExample(Object example) {
        return baseMapper.selectByExample(example);
    }

    @Override
    public int updateByPrimaryKey(T record) {
        return baseMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(T record) {
        return baseMapper.updateByPrimaryKeySelective(record);
    }
}
