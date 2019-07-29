package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import entity.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.impl.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;  

import com.pinyougou.sellergoods.service.TypeTemplateService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl extends CoreServiceImpl<TbTypeTemplate>  implements TypeTemplateService {

	
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
    TbSpecificationOptionMapper optionMapper;

	@Autowired
	public TypeTemplateServiceImpl(TbTypeTemplateMapper typeTemplateMapper) {
		super(typeTemplateMapper, TbTypeTemplate.class);
		this.typeTemplateMapper=typeTemplateMapper;
	}

	
	

	
	@Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbTypeTemplate> all = typeTemplateMapper.selectAll();
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Autowired
    private RedisTemplate redisTemplate;
	

	 @Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        if(typeTemplate!=null){			
						if(StringUtils.isNotBlank(typeTemplate.getName())){
				criteria.andLike("name","%"+typeTemplate.getName()+"%");
				//criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getSpecIds())){
				criteria.andLike("specIds","%"+typeTemplate.getSpecIds()+"%");
				//criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getBrandIds())){
				criteria.andLike("brandIds","%"+typeTemplate.getBrandIds()+"%");
				//criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getCustomAttributeItems())){
				criteria.andLike("customAttributeItems","%"+typeTemplate.getCustomAttributeItems()+"%");
				//criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
        List<TbTypeTemplate> all = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);

        //获取模板所有数据
         List<TbTypeTemplate> list = this.findAll();
         //循环所有数据
         for (TbTypeTemplate tbTypeTemplate : list) {
             //品牌列表
             List<Map> brandList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
             redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),brandList);

             //存储规格列表
             List<Map> specList = findSpecList(tbTypeTemplate.getId());
             redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);

         }


         return pageInfo;
    }

    /**
     * 根据模板id  获取模板的对象  中的规格的数  拼接成格式  [{id:27,"text",'网络'}，{}]
     * @param typeTmplateId
     * @return
     */
    @Override
    public List<Map> findSpecList(Long typeTmplateId) {
        //获取模板对象
        TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(typeTmplateId);
        //获取模板对象中规格列表数据字符串
        String specIds = tbTypeTemplate.getSpecIds();
        List<Map> maps = JSON.parseArray(specIds, Map.class);

        //根据规格id  获取规格的选项列表

        for (Map map : maps) {
            Integer id = (Integer) map.get("id");//获取到规格id

            TbSpecificationOption option = new TbSpecificationOption();
            option.setSpecId(Long.valueOf(id));
            List<TbSpecificationOption> options = optionMapper.select(option);

            //拼接数据
            map.put("options", options);
        }


        return maps;
    }

    @Override
    public void updateStatus(String status, Long Id) {
        TbTypeTemplate typeTemplate = findOne(Id);
        typeTemplate.setTemplateStatus(status);
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }

}
