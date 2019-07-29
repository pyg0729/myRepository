package com.pinyougou.search.service.impl;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-02-20-27
 *
 */

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.dao.itemDao;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private itemDao itemDao;




    @Override
    public void updataIndex(List<TbItem> tbItemList) {
        for (TbItem tbItem : tbItemList) {
            String spec = tbItem.getSpec(); //{"网络":"移动4G","机身内存":"16G"}
            //转成json对象（Map对象）
            Map map = JSON.parseObject(spec, Map.class);
            //map对象设置 规格的属性中specMap
            tbItem.setSpecMap(map);
        }
        itemDao.saveAll(tbItemList);
    }



    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        Map<String, Object> resultMap = new HashMap<>();

        //获取关键字
        String keywords = (String) searchMap.get("keywords");

        //创建查询对象的构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        //设置查询的条件
        //nativeSearchQueryBuilder.withIndices("pinyougou");//指定索引   默认查询所有索引
        //nativeSearchQueryBuilder.withTypes("item");// 指定类型  默认   查询所有类型

        /*nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("keyword",keywords));*/

        //使用多个字段完成搜索   并且高亮显示
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords, "title", "seller", "brand", "category"));


        //聚合函数一定要有关键字才能分组
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));

        //设置高亮显示的域(字段)  设置前缀 后缀
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<em style=\"color:red\">").postTags("</em>");


        nativeSearchQueryBuilder
                .withHighlightFields(new HighlightBuilder.Field("title"))
                .withHighlightBuilder(highlightBuilder);


        //过滤查询 ---------------- 商品分类的过滤查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        String category = (String) searchMap.get("category");
        if (StringUtils.isNotBlank(category)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category",category));
        }

        //过滤查询 ---------------- 商品品牌的过滤查询
        String brand = (String) searchMap.get("brand");
        if (StringUtils.isNotBlank(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand",brand));
        }

        //3.4 过滤查询 ----规格的过滤查询 获取到规格的名称 和规格的值  执行过滤查询
        Map<String,String> spec = (Map<String, String>) searchMap.get("spec");
        if (spec != null) {
            for (String key : spec.keySet()) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap."+key+".keyword",spec.get(key)));
            }
        }

        //3.4 过滤查询 ----价格区间条件过滤查询
        String price = (String) searchMap.get("price");
        if (StringUtils.isNotBlank(price)) {
            String[] split = price.split("-");
            if ("*".equals(split[1])) {
                //大于某一个价格
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            }else {
                //相当于0<= price <= 500
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0],true).to(split[1],true));
            }


        }


        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);

        //构建查询对象
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();



        //排序条件   按照价格进行排序
        String sortField = (String) searchMap.get("sortField");
        String sortType = (String) searchMap.get("sortType");

        if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)) {
            if (sortType.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, sortField);
                searchQuery.addSort(sort);
            }else if (sortType.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, sortField);
                searchQuery.addSort(sort);
            }
        }else {
            //不进行排序
        }



        //设置分页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageNo==null) {pageNo = 1;}
        if (pageSize==null) {pageSize = 40;}
        //第一个参数为  当前页  0为第一页
        //第二个参数为  每页显示的数据条数
        Pageable pageable = PageRequest.of(pageNo - 1,pageSize);

        searchQuery.setPageable(pageable);

        //执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class, new SearchResultMapper() {

            //自定义  数据  放回   获取高亮数据  放回
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                //获取结果集  (有高亮的数据)
                List<T> content = new ArrayList<>();
                //获取分页对象

                //获取查询的总记录数
                SearchHits hits = response.getHits();
                long totalHits = hits.getTotalHits();

                if (hits == null || hits.getTotalHits() <= 0) {
                    return new AggregatedPageImpl<T>(content);
                }

                //获取高亮数据
                for (SearchHit hit : hits) {
                    String sourceAsString = hit.getSourceAsString();//获取到的每一个文档的数据的json字符串  不是高亮的
                    TbItem tbItem = JSON.parseObject(sourceAsString, TbItem.class);//转换  pojo
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    if (highlightFields != null && highlightFields.get("title") != null && highlightFields.get("title").getFragments() != null) {


                        HighlightField highlightField = highlightFields.get("title");//获取高亮 字段为title的高亮的数据


                        Text[] fragments = highlightField.getFragments();//高亮碎片  <em  style
                        StringBuffer sb = new StringBuffer();
                        for (Text fragment : fragments) {
                            String string = fragment.string();//高亮的数据
                            sb.append(string);
                        }

                        tbItem.setTitle(sb.toString());

                        //将数据存储到集合中
                        content.add((T) tbItem);


                    } else {
                        content.add((T) tbItem);
                    }
                }


                //获取聚合查询的对象  (分组  统计  求平均值  )
                Aggregations aggregations = response.getAggregations();
                //获取深度分页的id
                String scrollId = response.getScrollId();


                return new AggregatedPageImpl<T>(content, pageable, totalHits, aggregations, scrollId);
            }
        });

        //设置结果集  到map 中(总数据条数,总页数,当前页集合....)


        //获取分组查询的结果
        StringTerms category_group = (StringTerms) tbItems.getAggregation("category_group");

        //获取bukets
        List<StringTerms.Bucket> buckets = category_group.getBuckets();
        //获取bukets中的key
        List<String> categoryList = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            String keyAsString = bucket.getKeyAsString();//就是商品分类的名称   手机  笔记本
            categoryList.add(keyAsString);
        }


        //搜索之后 默认 展示第一个商品分类的品牌和规格的列表

        //判断 商品分类是否为空 如果不为空 根据点击到的商品分类查询 该分类下的所有的品牌和规格的列表
        if (StringUtils.isNotBlank(category)) {
            Map map = searchBrandAndSpecList(category);
            resultMap.putAll(map);
        }else {
            if (categoryList != null && categoryList.size() > 0) {
                Map map = searchBrandAndSpecList(categoryList.get(0));
                resultMap.putAll(map);
            }else {
                resultMap.put("brandList",new HashMap<>());//返回添加品牌列表
                resultMap.put("specList",new HashMap<>());//返回添加品牌列表
            }
        }


        //将key存储到数组中
        resultMap.put("categoryList",categoryList);


        resultMap.put("total", tbItems.getTotalElements());//总数据条数
        resultMap.put("totalPages", tbItems.getTotalPages());//总页数
        resultMap.put("rows", tbItems.getContent());//当前页集合


        return resultMap;
    }





    /**
     *  根据分类的名称 获取 分类下的品牌的列表 和规格的列表
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category) {

        //1.集成redis
        //2.注入rediTemplate
        //3.获取分类的名称对应的模板的ID
        //hset bigkey field1 value1     hget bigkey field1

        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId != null) {
            //4.根据模板的ID 获取品牌的列表 和规格的列表
            List<Map> brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            List<Map> specList = (List) redisTemplate.boundHashOps("specList").get(typeId);

            Map<String,Object> map = new HashMap<>();
            map.put("brandList",brandList);//返回添加品牌列表
            map.put("specList",specList);//返回添加品牌列表

            return map;
        }
        return new HashMap<>();
    }



    /**
     * 更新数据到索引库中
     * @param itemList  就是数据
     */
    @Override
    public void updateIndex(List<TbItem> itemList) {

        for (TbItem tbItem : itemList) {
            String spec = tbItem.getSpec();
            Map map = JSON.parseObject(spec);
            tbItem.setSpecMap(map);
        }


        itemDao.saveAll(itemList);
    }

    /**
     * 根据SPU的IDs数组 进行删除
     *
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {

        //ids  里面是goods_id的值
        //delte from tb_item where goods_id in (1,2,) 从ES 删除

        DeleteQuery qurey = new DeleteQuery();

        qurey.setQuery(QueryBuilders.termsQuery("goodsId",ids));

        elasticsearchTemplate.delete(qurey,TbItem.class);
    }
}
