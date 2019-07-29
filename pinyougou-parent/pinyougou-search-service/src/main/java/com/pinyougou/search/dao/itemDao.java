package com.pinyougou.search.dao;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-05-20-48
 *
 */

import com.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface itemDao extends ElasticsearchRepository<TbItem,Long> {
}
