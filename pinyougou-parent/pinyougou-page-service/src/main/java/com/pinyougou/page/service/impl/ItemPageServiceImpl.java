package com.pinyougou.page.service.impl;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-08-09-00
 *
 */




import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import tk.mybatis.mapper.entity.Example;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper goodsMapper;


    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Value("${pageDir}")
    private String pageDir;



    @Override
    public void genItemHtml(Long goodsId) {
        //查询数据库的商品的数据   生成静态页面

        //1.根据SPU的ID 查询商品的信息（goods  goodsDesc  ）
        TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

        //2.使用freemarker 创建模板  使用数据集 生成静态页面 (数据集 和模板)
        genHTML("item.ftl",goods,goodsDesc);
    }



    private void genHTML(String templateName, TbGoods goods, TbGoodsDesc goodsDesc) {

        FileWriter writer = null;
        try {
            //1.创建configruation
            //2.设置编码和模板所在的目录
            Configuration configuration = configurer.getConfiguration();
            //3.创建模板文件

            Map<String,Object> model = new HashMap<>();
            model.put("tbGoods",goods);
            model.put("tbGoodsDesc",goodsDesc);

            //根据分类的ID 获取分类的对象里面的名称 设置到数据集中 返回给页面显示

            TbItemCat cat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
            TbItemCat cat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
            TbItemCat cat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());

            model.put("cat1",cat1.getName());
            model.put("cat2",cat2.getName());
            model.put("cat3",cat3.getName());

            //获取该SPU的所有的SKU的列表数据 存储到model中

            //select * from tb_item where goods_id = 1 and status=1 order by is_default desc
            Example exmaple = new Example(TbItem.class);
            Example.Criteria criteria = exmaple.createCriteria();
            criteria.andEqualTo("goodsId",goods.getId());
            criteria.andEqualTo("status","1");

            exmaple.setOrderByClause("is_default desc");//order by
            List<TbItem> itemList = itemMapper.selectByExample(exmaple);

            model.put("skuList",itemList);

            //4.加载模板对象
            Template template = configuration.getTemplate(templateName);

            //5.创建输出流 指定输出的目录文件
            writer = new FileWriter(new File(pageDir+ goods.getId()+".html"));
            //6.执行输出的动作生成静态页面
            template.process(model,writer);
            //7.关闭流


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
