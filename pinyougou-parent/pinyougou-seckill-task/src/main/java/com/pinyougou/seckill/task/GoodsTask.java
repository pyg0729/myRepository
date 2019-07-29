package com.pinyougou.seckill.task;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-18-20-27
 *
 */




import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class GoodsTask {

    @Autowired
    private TbSeckillGoodsMapper goodsMapper;


    @Autowired
    private RedisTemplate redisTemplate;


    @Scheduled(cron = "0/5 * * * * ?")
    public void pushGoods() {
        //1.注入dao

        //2.执行查询语句  符合条件的查询语句
        //select * from tb_seckill_good where status=1 and stock_count>0 and   开始<当前时间<结束时间 and id not in (12,3,3,4)


        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");//审核过的
        criteria.andGreaterThan("stockCount",0);//剩余库存>0
        Date date = new Date();
        criteria.andLessThan("startTime",date);
        criteria.andGreaterThan("endTime",date);

        //排除redis中已有的商品的列表
        Set<Long> seckillGoods = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).keys();
        if (seckillGoods != null && seckillGoods.size() > 0) {
            criteria.andNotIn("id", seckillGoods);
        }

        List<TbSeckillGoods> seckillGoodsList = goodsMapper.selectByExample(example);

        //3.注入redisTemplate

        //4.存储到redis中  key  value   hash   bigkey  field  value

        for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
            pushGoodsList(tbSeckillGoods);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(tbSeckillGoods.getId(),tbSeckillGoods);
        }
    }


    /**
     * 一个队列 就是一种商品
     * 队列的长度 就是 商品的库存两
     * @param tbSeckillGoods
     */
    private void pushGoodsList(TbSeckillGoods tbSeckillGoods) {
        //向同一个队列中压入商品数据
        for (int i = 0; i < tbSeckillGoods.getStockCount(); i++) {
            //库存为多少就是多少个SIZE 值就是id即可
            redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + tbSeckillGoods.getId()).leftPush(tbSeckillGoods.getId());
        }
    }
}

