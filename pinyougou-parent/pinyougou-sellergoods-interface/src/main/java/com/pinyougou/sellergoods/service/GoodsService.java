package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbItem;
import entity.Goods;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService extends CoreService<TbGoods> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods Goods);

	/**
	 * 接收组合对象，获取KPU 和描述    和SKU列表
	 * @param goods
	 */
    void add(Goods goods);

	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	Goods findOne(Long id);

	/**
	 * 修改更新  商品的主键  一定要有
	 * @param goods
	 */
    void update(Goods goods);

	/**
	 * 批量修改状态
	 * @param ids
	 * @param status
	 */
    void updateStatus(Long[] ids, String status);

	@Override
	void delete(Object[] ids);

	/**
	 * 根据商品SPU的数组对象查询所有的该商品的列表数据
	 * @param ids
	 * @return
	 */
	List<TbItem> findTbItemListByIds(Long[] ids);
}
