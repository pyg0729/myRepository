package com.pinyougou.es.dao;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-02-10-51
 *
 */

import com.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemDao extends ElasticsearchRepository<TbItem,Long> {
}
