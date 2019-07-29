package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.core.service.impl.CoreServiceImpl;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.*;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl extends CoreServiceImpl<TbSeckillGoods>  implements SeckillGoodsService {

	
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Autowired
	public SeckillGoodsServiceImpl(TbSeckillGoodsMapper seckillGoodsMapper) {
		super(seckillGoodsMapper, TbSeckillGoods.class);
		this.seckillGoodsMapper=seckillGoodsMapper;
	}

	
	

	
	@Override
    public PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSeckillGoods> all = seckillGoodsMapper.selectAll();
        PageInfo<TbSeckillGoods> info = new PageInfo<TbSeckillGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize, TbSeckillGoods seckillGoods) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();

        if(seckillGoods!=null){			
						if(StringUtils.isNotBlank(seckillGoods.getTitle())){
				criteria.andLike("title","%"+seckillGoods.getTitle()+"%");
				//criteria.andTitleLike("%"+seckillGoods.getTitle()+"%");
			}
			if(StringUtils.isNotBlank(seckillGoods.getSmallPic())){
				criteria.andLike("smallPic","%"+seckillGoods.getSmallPic()+"%");
				//criteria.andSmallPicLike("%"+seckillGoods.getSmallPic()+"%");
			}
			if(StringUtils.isNotBlank(seckillGoods.getSellerId())){
				criteria.andLike("sellerId","%"+seckillGoods.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+seckillGoods.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(seckillGoods.getStatus())){
				criteria.andLike("status","%"+seckillGoods.getStatus()+"%");
				//criteria.andStatusLike("%"+seckillGoods.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(seckillGoods.getIntroduction())){
				criteria.andLike("introduction","%"+seckillGoods.getIntroduction()+"%");
				//criteria.andIntroductionLike("%"+seckillGoods.getIntroduction()+"%");
			}
	
		}
        List<TbSeckillGoods> all = seckillGoodsMapper.selectByExample(example);
        PageInfo<TbSeckillGoods> info = new PageInfo<TbSeckillGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	@Override
	public void updateStatus(String status, Long[] ids) {
		//update tb_seckill_goods set stauts=? where id in ()
		TbSeckillGoods seckillGoods = new TbSeckillGoods();
		seckillGoods.setStatus(status);
		Example example = new Example(TbSeckillGoods.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", Arrays.asList(ids));

		seckillGoodsMapper.updateByExampleSelective(seckillGoods,example);
	}


    @Override
    public Map getGoodsById(Long id) {
		Map result = new HashMap();
		TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(id);
		Integer stockCount = tbSeckillGoods.getStockCount();
		long endTime = tbSeckillGoods.getEndTime().getTime();
		long now = new Date().getTime();
		long haomiao = endTime - now;
		result.put("count",stockCount);
		result.put("time",haomiao);

		return result;
    }

    @Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<TbSeckillGoods> findAll() {
		return redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).values();
	}
}
