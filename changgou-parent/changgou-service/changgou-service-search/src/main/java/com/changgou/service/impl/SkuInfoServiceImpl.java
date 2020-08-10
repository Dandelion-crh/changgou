package com.changgou.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.changgou.dao.SkuInfoDao;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.service.SkuInfoService;
import com.changgou.util.Result;
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
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 类名: SkuInfoServiceImpl
 * 作者: crh
 * 日期: 2020/6/1 0001下午 9:17
 */
@Service
public class SkuInfoServiceImpl implements SkuInfoService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    /**
     * 搜索实现
     * 优化完后需要调用 2 次查询es 后面写有极致优化程序1次调用es
     * 下面改方法屏蔽了buildBasicQuery(searchMap) 封装条件方法
     *                 getSearch(builder); 调用条件查询的方法
     *                 groupList(builder,searchMap) 调用品牌,分类列表查询的方法
     * @param searchMap
     */
    /* @Override
     public Map<String, Object> search(Map<String, String> searchMap) {
        Map<String, Object> map = null;
        //1.条件构造器
        //NativeSearchQueryBuilder builder = getBaseQuery(searchMap);

        NativeSearchQueryBuilder builder = buildBasicQuery(searchMap);
        //2.执行查询获取查询结果
        map = getSearch(builder);

        //刚开始写的查询,未进行程序优化

        //3.获取类别列表
//        if (searchMap ==null || searchMap.get("category") == null) {
//            List<String> category = getCategory(builder);
//            map.put("categoryList", category);
//        }
//
//        //4.获取品牌列表
//        if (searchMap ==null || searchMap.get("brandList") == null) {
//            List<String> brandList = searchBrandList(builder);
//            map.put("brandList", brandList);
//        }
//
//        //5.获取规格列表
//        Map<String, Set<String>> specMap = searchSpec(builder);
//        map.put("specList",specMap);

        //分组查询分类列表、品牌列表、规格列表
        map.putAll(groupList(builder,searchMap));

        //6.查询返回结果
        return map;
    }*/

    /***
     * 搜索条件构建
     * @param searchMap
     * @return
     */
   /* private NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap){
        //构造条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //构造布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //判断搜索条件searchMap

        //关键词
        if (!StringUtils.isEmpty(searchMap.get("keywords"))){
            queryBuilder.must(QueryBuilders.matchQuery("name",searchMap.get("keywords")));
        }

        //分类查询
        if (!StringUtils.isEmpty(searchMap.get("category"))){
            queryBuilder.must(QueryBuilders.termQuery("categoryName",searchMap.get("category")));
        }

        //商品查询
        if (!StringUtils.isEmpty(searchMap.get("brand"))){
            queryBuilder.must(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
        }

        //规格
        for(String key:searchMap.keySet()){
            //如果是规格参数
            if(key.startsWith("spec_")){
                queryBuilder.must(QueryBuilders.matchQuery("specMap."+key.substring(5)+".keyword", searchMap.get(key)));
            }
        }

        //价格查询
        String price = searchMap.get("price");
        if(!StringUtils.isEmpty(price)){
            //1. 0-500元   2.3000元以上
            price = price.replace("元","").replace("以上", "");
            //1.0-500   2.3000
            String[] split = price.split("-");
            queryBuilder.must(QueryBuilders.rangeQuery("price").gt(split[0]));
            //如果有第二个值
            if(split.length > 1){
                queryBuilder.must(QueryBuilders.rangeQuery("price").lte(split[1]));
            }
        }


        //分页
        Integer pageNo = pageConvert(searchMap.get("pageNum"));//页码
        Integer pageSize = 3;//页大小
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);
        nativeSearchQueryBuilder.withPageable(pageRequest);

        //排序实现
        String sortRule = searchMap.get("sortRule");//排序规则 ASC DESC
        String sortField = searchMap.get("sortField");//排序字段 price
        if (!StringUtils.isEmpty(sortField)){
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
        }

        //添加筛选条件
        nativeSearchQueryBuilder.withQuery(queryBuilder);
        return nativeSearchQueryBuilder;
    }*/

    /**
     *执行查询
     */
    /*private Map getSearch(NativeSearchQueryBuilder builder){
        Map<String, Object> map = new HashMap<>();
        //执行高亮查询的相关的条件 1:对哪个域进行高亮查询 2:前缀 3 :后缀
        HighlightBuilder.Field field = new HighlightBuilder
                .Field("name")//指定哪个域进行高亮
                .preTags("<span style='color:red'>")//前缀设置
                .postTags("</span>")//设置后缀
                .fragmentSize(100);//高度数据的长度
        //设置高亮查询
        builder.withHighlightFields(field);
        //执行查询获取查询结果
        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                ArrayList<T> list = new ArrayList<>();
                *//**
                 * 目的:
                 * 1.将所有命中的数据取出来
                 * 2.将所有高亮的数据取出来
                 * 3.使用高亮的数据替换掉没有高亮的旧数据
                 * 4.将替换好的数据返回
                 *//*
                //获取所有的查询结果
                SearchHits hits = searchResponse.getHits();
                //获取数据的迭代器
                Iterator<SearchHit> iterator = hits.iterator();
                //循环获取每条数据
                while (iterator.hasNext()){
                    //获取每一条数据
                    SearchHit next = iterator.next();
                    String sourceAsString = next.getSourceAsString();
                    //数据转换:获取到每一条skuinfo数据以后,name是没有高亮
                    SkuInfo skuInfo = JSONObject.parseObject(sourceAsString, SkuInfo.class);
                    //获取高亮的数据
                    Text[] names = next.getHighlightFields().get("name").getFragments();
                    String name = "";
                    //循环获取全部高亮的数据
                    if (names != null){
                        for (Text text : names) {
                            name = name + text;
                        }
                        //使用高亮的数据替换非高亮的数据
                        skuInfo.setName(name);
                    }
                    list.add((T)skuInfo);
                }
                Aggregations aggregations = searchResponse.getAggregations();
                *//**
                 * 1.要返回的查询结果的列表
                 * 2.分页的信息
                 * 3.总命中的数据量--一共有多少条数据
                 *//*
                return new AggregatedPageImpl<>(list,pageable,hits.getTotalHits(),aggregations);
            }
        });
        map.put("rows",skuPage.getContent());
        map.put("totalPages",skuPage.getTotalPages());
        //查询返回结果
        return map;
    }*/

    /****
     * 分组搜索实现
     * @param nativeSearchQueryBuilder
     * @return
     */
   /* public Map groupList(NativeSearchQueryBuilder nativeSearchQueryBuilder,Map<String,String> searchMap){
        *//****
         * 根据分类名字|品牌名字|规格进行分组查询
         * 1:给当前分组取一个别名
         * 2:分组的域的名字
         *//*
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword"));

        if (searchMap==null || StringUtils.isEmpty(searchMap.get("category"))){
            //分类
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        }

        if (searchMap==null || StringUtils.isEmpty(searchMap.get("brand"))){
            //品牌合并分组查询
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        }
        //实现分组搜索
        AggregatedPage<SkuInfo> categoryPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        //获得所有分组数据
        Aggregations aggregations = categoryPage.getAggregations();
        //存储所有数据
        Map groupMap = new HashMap<>();

        //获取规格数据
        List<String> specList = getGroupData(aggregations, "skuSpec");
        Map<String,Set<String>> specMap = specPutAll(specList);
        groupMap.put("specList",specMap);

        //分类分组实现
        if (searchMap==null || StringUtils.isEmpty(searchMap.get("brand"))){
            List<String> categoryList = getGroupData(aggregations, "skuCategory");
            groupMap.put("categoryList",categoryList);
        }

        //品牌分组实现
        if (searchMap==null || StringUtils.isEmpty(searchMap.get("brand"))){
            List<String> brandList = getGroupData(aggregations,"skuBrand");
            groupMap.put("brandList",brandList);
        }
        return groupMap;
    }*/

    /**
     *程序极致优化,调用一次es查询
     * 重新写调用程序
     */
    /**
     * 搜索实现
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        Map<String,Object> map = new HashMap<>();

        //条件构造器,构建查询条件:把所有的查询条件和聚合查全部设置好了
        NativeSearchQueryBuilder builder = getBaseQuery(searchMap);
        //执行查询,获取查询结果:查询的结果获取 1.列表获取 2.聚合结果获取
        map = getSearch(builder,searchMap);
        return map;
    }

    /**
     * 条件封装
     */
    private NativeSearchQueryBuilder getBaseQuery(Map<String,String> searchMap){
      /*  //条件构造器
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //获取搜索的参数
        String keywords = searchMap.get("keywords");
        if (!StringUtils.isEmpty(keywords)){
            //参数进行封装,构建查询条件
            builder.withQuery(QueryBuilders.matchQuery("name",keywords));
        }
        return builder;*/
        //条件构造器
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //bool查询的条件构造器
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //获取搜索条件
        String keywords = searchMap.get("keywords");
        //判断关键字是否为空
        if (!StringUtils.isEmpty(keywords)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("name",keywords));
        }

        //品牌名称
        String brand = searchMap.get("brand");
        if (!StringUtils.isEmpty(brand)){
            boolQueryBuilder.must(QueryBuilders.termQuery("brandName",brand));
        }

        //类别名称查询
        String category = searchMap.get("category");
        if (!StringUtils.isEmpty(category)){
            boolQueryBuilder.must(QueryBuilders.termQuery("categoryName",category));
        }

        //规格条件封装:对请求的参数进行循环,凡是以spec_开头的,都是规格参数
        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("spec_")){
                String value = entry.getValue();
                boolQueryBuilder.must(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword",value));
            }
        }

        //价格查询
        String price = searchMap.get("price");
        if (!StringUtils.isEmpty(price)){
            //1. 0-500元   2.3000元以上
            String[] split = price.replace("元", "").replace("以上", "").split("-");
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gt(split[0]));
            //如果有第二个值
            if (split.length==2){
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(split[1]));
            }
        }

        //分页查询
        Integer pageSize = 20;//每页显示多少条数据
        Integer pageNum = pageConvert(searchMap.get("pageNum"));//当前显示第几页
        //注意:1.分页查询的设置是设置NativeSearchQueryBuilder条件构造器,不是设置bool条件构造器
        //注意:2.es中,页面是从第0页开始
        builder.withPageable(PageRequest.of(pageNum -1,pageSize));

        //排序的域是哪个
        String sortField = searchMap.get("sortField");
        //排序的规则是什么
        String sortRule = searchMap.get("sortRule");
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
            //注意:1.分页查询的设置是设置NativeSearchQueryBuilder条件构造器,不是设置bool条件构造器
            builder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
        }
        //保存查询条件
        builder.withQuery(boolQueryBuilder);
        /**
         * 聚合条件封装
         */
        //------------------------------------------------------------------------
        //类别聚合
        if(StringUtils.isEmpty(category)){
            builder.addAggregation(AggregationBuilders.terms("category").field("categoryName"));
        }

        //品牌聚合
        if(StringUtils.isEmpty(brand)){
            //品牌聚合
            builder.addAggregation(AggregationBuilders.terms("brand").field("brandName"));
        }

        //规格聚合
        builder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword"));

        //------------------------------------------------------------------------
        return builder;

    }

    /**
     * 执行查询
     */
    private  Map getSearch(NativeSearchQueryBuilder builder,Map<String,String> searchMap){
        Map<String, Object> map = new HashMap<>();
        //设置高亮查询的相关的条件1:对哪个域进行高亮查询  2.前缀  3.后缀
        HighlightBuilder.Field field = new HighlightBuilder
                .Field("name")//指定哪个域进行高亮
                .preTags("<span style='color:red'>")//前缀设置
                .postTags("</span>")//设置后缀
                .fragmentSize(100);//高亮数据的长度
        //设置高亮查询
        builder.withHighlightFields(field);

        //执行查询获取查询结果
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<T> list = new ArrayList<>();
                /**
                 * 目的:
                 * 1.将所有命中的数据取出来
                 * 2.将所有高亮的数据取出来
                 * 3.使用高亮的数据替换掉没有高亮的旧数据
                 * 4.将替换好的数据返回
                 */
                //获取所有的查询结果
                SearchHits hits = searchResponse.getHits();
                //获取数据的迭代器
                Iterator<SearchHit> iterator = hits.iterator();
                //循环获取每条数据
                while (iterator.hasNext()){
                    //获取每一条数据
                    SearchHit next = iterator.next();
                    String sourceAsString = next.getSourceAsString();
                    //数据转换:获取到每一条skuinfo的数据以后,name是没有高亮
                    SkuInfo skuInfo = JSONObject.parseObject(sourceAsString, SkuInfo.class);
                    //获取高亮的数据
                    Text[] names = next.getHighlightFields().get("name").getFragments();
                    String name = "";
                    //循环获取全部高亮的数据
                    if(names != null){
                        for (Text text : names) {
                            name = name + text;

                        }
                        //使用高亮的数据替换非高亮的数据
                        skuInfo.setName(name);
                    }
                    list.add((T)skuInfo);
                }
                Aggregations aggregations = searchResponse.getAggregations();
                /**
                 * 1.要返回的查询结果的列表
                 * 2.分页的信息
                 * 3.总命中的数据量--一共有多少条数据
                 */
                return new AggregatedPageImpl<>(list, pageable, hits.getTotalHits(), aggregations);
            }
        });

        List<SkuInfo> content = skuInfos.getContent();//获取商品列表
        long totalElements = skuInfos.getTotalElements();//总共命中多少条数据,符合条件的数据有多少
        int totalPages = skuInfos.getTotalPages();//一共有多少页数据
        /**
         * 获取聚合条件的结果
         */
        //获取全部的聚合的结果--------------------------------------------------------------------
        Aggregations aggregations = skuInfos.getAggregations();

        //获取品牌聚合查询的结果
        String brand = searchMap.get("brand");
        if (StringUtils.isEmpty(brand)){
            //品牌聚合
            List<String> brandList = getGroupData(aggregations, "brand");
            map.put("brandList",brandList);
        }
        //获取类别聚合查询的结果
        String category = searchMap.get("category");
        if (StringUtils.isEmpty(category)){
            //类别聚合
            List<String> categoryList = getGroupData(aggregations, "category");
            map.put("categoryList",categoryList);
        }
        //规格的聚合结果
        List<String> skuSpec = getGroupData(aggregations, "skuSpec");
        Map<String, Set<String>> specMap = specPutAll(skuSpec);
        map.put("specList",specMap);

        //获取全部的聚合的结果--------------------------------------------------------------------
        map.put("rows",content);//查找出来的商品数据
        map.put("totalElements",totalElements);//总共多少条商品数据
        map.put("totalPages",totalPages);//总共多少页
        map.put("pageNum",builder.build().getPageable().getPageNumber());//当前页
        map.put("pageSize",builder.build().getPageable().getPageSize());//每页多少条数据
        return map;
    }


    /****
     * 获取分组后的数据集合
     * @param aggregations
     * @param name
     * @return
     */
    public List<String> getGroupData(Aggregations aggregations,String name){
        //获取分组结果
        StringTerms terms = aggregations.get(name);
        //返回指定预的分组名称集合
        List<String> fieldList = new ArrayList<>();
        for (StringTerms.Bucket bucket : terms.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            fieldList.add(keyAsString);
        }
        return fieldList;
    }

    /***
     * 获取当前页
     * 如果不发生异常，则直接转换成数字
     * 如果发生异常，则默认从第1页查询
     * @param searchMap
     * @return
     */
    public Integer pageConvert(String searchMap){
        try {
            return Integer.parseInt(searchMap);
        }catch (Exception e){
            //默认显示第一页
            return 1;
        }
    }

    /**
     * 查询符合条件的商品的类别信息,并且出重
     */
    private List<String> getCategory(NativeSearchQueryBuilder builder){
        //目的:查询出符合条件的所有数据的categoryName的值,并且对这个值进行去重
        List<String> list = new ArrayList<>();
        //设置聚合的对象,要对哪个域进行去重
        /**
         * 1.取一个别名
         * 2.对哪个域进行去重聚合查询
         *
         * 解释: select categoryName as category, brandName as brand group by categoryName,brandName
         */
        builder.addAggregation(AggregationBuilders.terms("category").field("categoryName"));

        //执行查询
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        //获取全部的聚合的结果
        Aggregations aggregations = skuInfos.getAggregations();
        //取出类别的聚合结果
        StringTerms aggregation = aggregations.get("category");
        //将结果进行遍历,取出每一个类别的名字
        for (StringTerms.Bucket bucket : aggregation.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            list.add(keyAsString);
        }
        return list;
    }

    /**
     * 查询品牌列表
     */
    private List<String> searchBrandList(NativeSearchQueryBuilder builder){
        List<String> list = new ArrayList<>();
        //查询聚合品牌 skuBrandGroupby给聚合分组结果起个别名
        builder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));

        //执行查询
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        //获取全部的聚合的结果
        Aggregations aggregations = skuInfos.getAggregations();
        //取出类别的聚合结果
        StringTerms aggregation = aggregations.get("skuBrand");
        //将结果进行遍历,取出每一个类别的名字
        for (StringTerms.Bucket bucket : aggregation.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            list.add(keyAsString);
        }
        return list;
    }

    /**
     *查询规格列表
     */
    private Map<String,Set<String>> searchSpec(NativeSearchQueryBuilder builder){
        //查询聚合品牌  skuBrandGroupby给聚合分组结果起个别名
        builder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword"));
        //执行搜索
        AggregatedPage<SkuInfo> result = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        //获取聚合规格数据结果
        Aggregations aggs = result.getAggregations();
        //获取分组结果
        StringTerms terms = aggs.get("skuSpec");
        //返回规格数据名称
        List<String> specList = new ArrayList<String>();
        for (StringTerms.Bucket bucket : terms.getBuckets()) {
            //规格数据添加到集合中
            specList.add(bucket.getKeyAsString());
        }
        //将规格转换成Map
        Map<String,Set<String>> specMap = specPutAll(specList);
        return specMap;
    }

    /***
     * 将所有规格数据转入到Map中
     * @param specList
     * @return
     */
    private Map<String,Set<String>> specPutAll(List<String> specList){
        //新建一个Map
        Map<String,Set<String>> specMap = new HashMap<>();
        //将集合数据存到Map中
        for (String spec : specList) {
            //将Map数据转成Map
            Map<String,String> map = JSONObject.parseObject(spec, Map.class);
            //循环转换后的Map
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();//规格名字
                String value = entry.getValue(); //规格选项值
                Set<String> specValues = specMap.get(key);
                if (null == specValues) {
                   specValues = new HashSet<String>();
                }
                //将当前规格加入到集合中
                specValues.add(value);
                //将数据存入到specMap中
                specMap.put(key,specValues);
            }
        }
        return specMap;
    }

    /**
     * 将商品数据从数据库导入到es中去
     */
    @Override
    public void importData() {
        //远程调用微服务,获取状态为1的所有商品
        Result<List<Sku>> result = skuFeign.findByStatus("1");
        List<Sku> list = result.getData();
        //将商品数转换为es存储的数据格式
        String json = JSONObject.toJSONString(list);

        //将商品数据转换为es存储数据格式JSONObject
        List<SkuInfo> skuInfoList = JSONObject.parseArray(json,SkuInfo.class);

        //规格数据处理
        for (SkuInfo skuInfo : skuInfoList) {
            String spec = skuInfo.getSpec();
            Map<String,Object> map = JSONObject.parseObject(spec, Map.class);
            skuInfo.setSpecMap(map);
        }
        //将数据存入es
        skuInfoDao.saveAll(skuInfoList);
    }

}
