package com.pinyougou.es.service.impl;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-02-10-43
 *
 */

import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.es.service.ImportService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;



public class ImportServiceImpl implements ImportService {


    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private ItemDao itemDao;



    @Override
    public void ImportDataToEs() {
        //1.使用dao 根据条件查询数据库的中(tb_item)的数据
        //select * from tb_item where status=1
        TbItem item = new TbItem();
        item.setStatus("1");//状态为正常的数据
        List<TbItem> itemList = itemMapper.select(item);

        //循环遍历集合  获取里面的规格的数据 字符串 {"网络":"移动4G","机身内存":"16G"}
        for (TbItem tbItem : itemList) {
            String spec = tbItem.getSpec();
            //判断spec是否为空
            if (StringUtils.isNotBlank(spec)) {//{"网络":"移动4G","机身内存":"16G"}
                //转成json对象（Map对象）
                Map<String,String> map = JSON.parseObject(spec, Map.class);
                //map对象设置 规格的属性中specMap
                tbItem.setSpecMap(map);
            }
        }

        //保存数据
        itemDao.saveAll(itemList);


    }
}
