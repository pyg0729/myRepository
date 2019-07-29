package com.pinyougou.order.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService extends CoreService<TbOrder> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder Order);

	/**
	 * 调用orderservice的服务   根据userID从Redis中获取数据
	 * @param userId
	 * @return
	 */
    TbPayLog getPayLogByUserId(String userId);

	/**
	 *	跟新信息
	 * @param out_trade_no
	 * @param transaction_id
	 */
	void updateStatus(String out_trade_no, String transaction_id);

	void deleteOrder(String userId);


}
