package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.core.service.impl.CoreServiceImpl;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.*;
import entity.Cart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder>  implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	TbGoodsMapper tbGoodsMapper;

	@Autowired
	public OrderServiceImpl(TbOrderMapper orderMapper) {
		super(orderMapper, TbOrder.class);
		this.orderMapper=orderMapper;
	}






	@Override
	public void deleteOrder(String userId) {
       redisTemplate.boundHashOps("CART_REDIS_KEY").delete(userId);

       TbOrder tbOrder=new TbOrder();
       tbOrder.setUserId(userId);
		orderMapper.delete(tbOrder);




	}


	
	@Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbOrder> all = orderMapper.selectAll();
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder order) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if(order!=null){			
						if(StringUtils.isNotBlank(order.getPaymentType())){
				criteria.andLike("paymentType","%"+order.getPaymentType()+"%");
				//criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(StringUtils.isNotBlank(order.getPostFee())){
				criteria.andLike("postFee","%"+order.getPostFee()+"%");
				//criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(StringUtils.isNotBlank(order.getStatus())){
				criteria.andLike("status","%"+order.getStatus()+"%");
				//criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(order.getShippingName())){
				criteria.andLike("shippingName","%"+order.getShippingName()+"%");
				//criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(StringUtils.isNotBlank(order.getShippingCode())){
				criteria.andLike("shippingCode","%"+order.getShippingCode()+"%");
				//criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(StringUtils.isNotBlank(order.getUserId())){
				criteria.andLike("userId","%"+order.getUserId()+"%");
				//criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerMessage())){
				criteria.andLike("buyerMessage","%"+order.getBuyerMessage()+"%");
				//criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerNick())){
				criteria.andLike("buyerNick","%"+order.getBuyerNick()+"%");
				//criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerRate())){
				criteria.andLike("buyerRate","%"+order.getBuyerRate()+"%");
				//criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverAreaName())){
				criteria.andLike("receiverAreaName","%"+order.getReceiverAreaName()+"%");
				//criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverMobile())){
				criteria.andLike("receiverMobile","%"+order.getReceiverMobile()+"%");
				//criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverZipCode())){
				criteria.andLike("receiverZipCode","%"+order.getReceiverZipCode()+"%");
				//criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiver())){
				criteria.andLike("receiver","%"+order.getReceiver()+"%");
				//criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(StringUtils.isNotBlank(order.getInvoiceType())){
				criteria.andLike("invoiceType","%"+order.getInvoiceType()+"%");
				//criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(StringUtils.isNotBlank(order.getSourceType())){
				criteria.andLike("sourceType","%"+order.getSourceType()+"%");
				//criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(StringUtils.isNotBlank(order.getSellerId())){
				criteria.andLike("sellerId","%"+order.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
        List<TbOrder> all = orderMapper.selectByExample(example);
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


    @Override
    public TbPayLog getPayLogByUserId(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("TbPayLog").get(userId);
    }

    @Override
    public void updateStatus(String out_trade_no, String transaction_id) {
		//1.根据订单号 查询支付日志对象  更新他的状态
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);

		payLog.setPayTime(new Date());
		payLog.setTransactionId(transaction_id);
		payLog.setTradeState("1");//
		payLogMapper.updateByPrimaryKey(payLog);

		//* 2.根据支付日志 获取到商品订单列表 更新商品订单状态
		String orderList = payLog.getOrderList();
		String[] split = orderList.split(",");
		for (String orderidstring : split) {
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderidstring));
			tbOrder.setStatus("2");//已经付完款
			tbOrder.setUpdateTime(new Date());
			tbOrder.setPaymentTime(tbOrder.getUpdateTime());
			orderMapper.updateByPrimaryKey(tbOrder);
		}

		//* 3.根据支付日志 获取到USER_id 删除reids中的记录
		redisTemplate.boundHashOps("TbPayLog").delete(payLog.getUserId());
	}

    @Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbPayLogMapper payLogMapper;

	@Override
	public void add(TbOrder order) {

		//1.获取页面传递的数据

		//2.插入到订单表中         拆单(一个商家就是一个订单)  订单的ID 要生成

		//2.1 获取reids中的购物车数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART_REDIS_KEY").get(order.getUserId());

		double totalMoney = 0;

		List<String> orderListiD = new ArrayList<>();

		//2.2 循环遍历 每一个Cart 对象  就是一个商家
		for (Cart cart : cartList) {

			//使用雪花算法生成随机数
			long orderId = idWorker.nextId();
			orderListiD.add(orderId+"");
			TbOrder tbOrder = new TbOrder();
			tbOrder.setOrderId(orderId);

			tbOrder.setPaymentType(order.getPaymentType());
			tbOrder.setPostFee("0");
			tbOrder.setStatus("1");//付款状态  1 未支付

			tbOrder.setCloseTime(new Date());
			tbOrder.setUpdateTime(tbOrder.getCreateTime());//订单修改时间
			tbOrder.setUserId(order.getUserId());//用户Id
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());//地址
			tbOrder.setReceiverMobile(order.getReceiverMobile());//电话
			tbOrder.setReceiverZipCode("518000");//邮政编码
			tbOrder.setReceiver(order.getReceiver());///收货人
			tbOrder.setSourceType("2");//PC端来源
			tbOrder.setSellerId(cart.getSellerId());//商家id

			double money = 0;

			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				money += orderItem.getTotalFee().doubleValue();
				//插入到订单选项表中
				long orderItemId = idWorker.nextId();
				orderItem.setId(orderItemId);
				orderItem.setOrderId(orderId);
				orderItemMapper.insert(orderItem);
			}



			tbOrder.setPayment(new BigDecimal(money));

			totalMoney += money;
			orderMapper.insert(tbOrder);
		}


		//添加支付的日志
		TbPayLog payLog = new TbPayLog();
		payLog.setOutTradeNo(idWorker.nextId()+"");
		payLog.setCreateTime(new Date());
		long fen =(long) (totalMoney * 100);
		payLog.setTotalFee(fen);
		payLog.setUserId(order.getUserId());
		payLog.setTradeState("0");//未支付
		payLog.setOrderList(orderListiD.toString().replace("[","").replace("]",""));
		payLog.setPayType(order.getPaymentType());

		payLogMapper.insert(payLog);

		//存储到redis中  bigkey field  value
		redisTemplate.boundHashOps("TbPayLog").put(order.getUserId(), payLog);



		//移除掉redis的购物车数据
		redisTemplate.boundHashOps("CART_REDIS_KEY").delete(order.getUserId());
	}


}
