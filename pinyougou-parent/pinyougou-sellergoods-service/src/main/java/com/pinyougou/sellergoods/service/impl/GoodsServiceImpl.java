package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.impl.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.sellergoods.service.GoodsService;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl extends CoreServiceImpl<TbGoods> implements GoodsService {


    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbSellerMapper tbSellerMapper;

    @Autowired
    private TbBrandMapper brandMapper;


    @Autowired
    public GoodsServiceImpl(TbGoodsMapper goodsMapper) {
        super(goodsMapper, TbGoods.class);
        this.goodsMapper = goodsMapper;
    }


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbGoods> all = goodsMapper.selectAll();
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods goods) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("isDelete",false);// false 0  true 1

        if (goods != null) {
            if (StringUtils.isNotBlank(goods.getSellerId())) {
                //criteria.andLike("sellerId","%"+goods.getSellerId()+"%");
                //criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
                criteria.andEqualTo("sellerId",goods.getSellerId());
            }
			if(StringUtils.isNotBlank(goods.getGoodsName())){
				criteria.andLike("goodsName","%"+goods.getGoodsName()+"%");
				//criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(StringUtils.isNotBlank(goods.getAuditStatus())){
				criteria.andEqualTo("auditStatus",goods.getAuditStatus());
				//criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(StringUtils.isNotBlank(goods.getIsMarketable())){
				criteria.andLike("isMarketable","%"+goods.getIsMarketable()+"%");
				//criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(StringUtils.isNotBlank(goods.getCaption())){
				criteria.andLike("caption","%"+goods.getCaption()+"%");
				//criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(StringUtils.isNotBlank(goods.getSmallPic())){
				criteria.andLike("smallPic","%"+goods.getSmallPic()+"%");
				//criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(StringUtils.isNotBlank(goods.getIsEnableSpec())){
				criteria.andLike("isEnableSpec","%"+goods.getIsEnableSpec()+"%");
				//criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}

        }
        List<TbGoods> all = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public void add(Goods goods) {
        //获取SPU的数据
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus("0");//默认就是未审核的状态
        tbGoods.setIsDelete(false);//不删除的状态

        goodsMapper.insert(tbGoods);

        //获取SPU对应的描的数据
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();

        goodsDesc.setGoodsId(tbGoods.getId());

        goodsDescMapper.insert(goodsDesc);

        //获取SKU的列表数据
        List<TbItem> itemList = goods.getItemList();
        saveItems(goods, tbGoods, goodsDesc, itemList);


    }



    @Override
    public Goods findOne(Long id) {

        //根据商品id 获取SPU的数据
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        //根据商品的id  获取描述的数据
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);

        //根据商品的id  获取SKU列表的数据
        TbItem tbItem = new TbItem();
        tbItem.setGoodsId(id);
        List<TbItem> tbItemList = itemMapper.select(tbItem);
        //返回组合对象
        Goods goods = new Goods();
        goods.setGoods(tbGoods);
        goods.setGoodsDesc(tbGoodsDesc);
        goods.setItemList(tbItemList);

        return goods;
    }

    /**
     * 更新数据
     * @param goods
     */
    @Override
    public void update(Goods goods) {
        //获取SPU数据   更新SPU数据
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(tbGoods);

        //获取描述的数据    更新SPU的描述数据
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDescMapper.updateByPrimaryKey(goodsDesc);


        //获取SKU数据  更新SKU数据   先删除数据库的SKU   再新增
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId",tbGoods.getId());
        itemMapper.deleteByExample(example);


        //新增
        List<TbItem> itemList = goods.getItemList();

        saveItems(goods, tbGoods, goodsDesc, itemList);
    }



    /**
     * 更新TbItem数据库表信息
     * @param tbGoods
     * @param goodsDesc
     * @param itemList
     */
    private void saveItems(Goods goods, TbGoods tbGoods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
        //4.插入表中  TODO
        //判断是否启用规格
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem tbItem : itemList) {
                //设置 title  SPU名称+ “ ” + 规格的选项名
                String title = tbGoods.getGoodsName();
                String spec = tbItem.getSpec();//{ "网络": "移动3G", "机身内存": "32G" }
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                tbItem.setTitle(title);

                //设置图片地址
                //[{"color":"白色","url":"http://192.168.25.129/group1/M00/00/00/wKgZhVmOZxyAfH5xAAFa4hmtWek092.jpg"}]
                List<Map> maps = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
                String image = maps.get(0).get("url").toString();
                tbItem.setImage(image);

                //设置分类   就是第三级目录
                tbItem.setCategoryid(tbGoods.getCategory3Id());

                TbItemCat cat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
                tbItem.setCategory(cat.getName());

                //设置时间
                tbItem.setCreateTime(new Date());
                tbItem.setUpdateTime(tbItem.getCreateTime());

                //设置外键
                tbItem.setGoodsId(tbGoods.getId());

                //设置商家
                tbItem.setSellerId(tbGoods.getSellerId());
                TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
                tbItem.setSeller(tbSeller.getNickName());//商家店铺名称
                TbBrand brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
                tbItem.setBrand(brand.getName());

                //添加到数据库
                itemMapper.insert(tbItem);


            }
        }else {
            //不启用规格  SKU数据
            TbItem tbItem = new TbItem();
            tbItem.setTitle(tbGoods.getGoodsName());
            tbItem.setPrice(tbGoods.getPrice());
            tbItem.setNum(999);//默认设置库存


            //设置图片地址
            //[{"color":"白色","url":"http://192.168.25.129/group1/M00/00/00/wKgZhVmOZxyAfH5xAAFa4hmtWek092.jpg"}]
            List<Map> maps = JSON.parseArray(goodsDesc.getItemImages(), Map.class);

            if (maps != null && maps.size() > 0) {
                String image = maps.get(0).get("url").toString();
                tbItem.setImage(image);
            }

            //设置分类
            tbItem.setCategoryid(tbGoods.getCategory3Id());

            TbItemCat cat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            tbItem.setCategory(cat.getName());

            tbItem.setStatus("1");//默认正常

            //设置时间
            tbItem.setCreateTime(new Date());
            tbItem.setUpdateTime(tbItem.getCreateTime());

            tbItem.setIsDefault("1");

            //设置外键
            tbItem.setGoodsId(tbGoods.getId());

            //设置商家
            tbItem.setSellerId(tbGoods.getSellerId());
            TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
            tbItem.setSeller(tbSeller.getNickName());//商家店铺名称
            TbBrand brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
            tbItem.setBrand(brand.getName());


            tbItem.setSpec("{}");

            //添加到数据库
            itemMapper.insert(tbItem);

        }
    }


    @Override
    public void updateStatus(Long[] ids, String status) {
        //创建对象
        TbGoods tbGoods = new TbGoods();
        //修改状态
        tbGoods.setAuditStatus(status);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        //更新
        goodsMapper.updateByExampleSelective(tbGoods,example);
    }



    @Override
    public void delete(Object[] ids) {
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete(true);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);
    }



    @Override
    public List<TbItem> findTbItemListByIds(Long[] ids) {

        Example example = new Example(TbItem.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("goodsId",Arrays.asList(ids)).andEqualTo("status","1");

        return itemMapper.selectByExample(example);
    }
}
