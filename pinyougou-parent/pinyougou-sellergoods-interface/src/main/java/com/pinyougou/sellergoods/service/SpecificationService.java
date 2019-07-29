package com.pinyougou.sellergoods.service;
import com.pinyougou.pojo.TbSpecification;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import entity.Specification;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService extends CoreService<TbSpecification> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification Specification);

	/**
	 * 新增规格
	 * @param specification
	 */
    void add(Specification specification);

	/**
	 * 根据id查询，并回显数据
	 * @param id
	 * @return
	 */
	Specification findOne(Long id);

	/**
	 * 批量删除
	 * @param ids
	 */
	@Override
	void delete(Object[] ids);


	void updateStatus(String status,Long Id);
}
