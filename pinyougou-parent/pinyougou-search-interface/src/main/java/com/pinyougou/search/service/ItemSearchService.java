package com.pinyougou.search.service;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-02-20-24
 *
 */

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    void updataIndex(List<TbItem> tbItemList);

    /**
     * 根据搜索条件搜索内容展示数据返回
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String,Object> searchMap);


    /**
     * 更新数据到索引库中
     * @param itemList  就是数据
     */
    public void updateIndex(List<TbItem> itemList);

    /**
     *
     * @param ids
     */
    void deleteByIds(Long[] ids);
}
