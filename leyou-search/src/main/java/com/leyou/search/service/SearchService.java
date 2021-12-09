package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.bo.SearchResult;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecParamClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecParamClient specParamClient;

    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {

        // 创建goods对象
        Goods goods = new Goods();

        // 查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        // 查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        // 查询spu下的所有sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        // 遍历skus，获取价格集合
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });

        // 查询出所有的搜索规格参数
        List<SpecParam> params = this.specParamClient.querySpecParams(null, spu.getCid3(), null, true);
        // 查询spuDetail。获取规格参数值
        SpuDetail spuDetail = this.goodsClient.querySupDetailBySpuId(spu.getId());
        // 获取通用的规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });
        // 获取特殊的规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });
        // 定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()) {
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(), value);
            } else {
                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }
        });

        // 设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);

        return goods;
    }

    /**
     * 查看参数落在哪一个区间，并且返回
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 进行分页搜索
     *
     * @param searchRequest
     * @return
     */
    public PageResult<Goods> search(SearchRequest searchRequest) {
        //判断搜索条件是不是为空，如果是，直接返回null
        if (StringUtils.isBlank(searchRequest.getKey())) {
            return null;
        }
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //设置bool查询器,因为要对结果集进行过滤，比如品牌只要华为的，内存只要8G的
        BoolQueryBuilder boolQuery = this.buildBoolQuery(searchRequest);
        queryBuilder.withQuery(boolQuery);
        //设置查询结果集字段过滤，只需要这3个字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        //设置分类和品牌聚合信息
        queryBuilder.addAggregation(AggregationBuilders.terms("agg_category").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("agg_brand").field("brandId"));
        //设置分页信息
        queryBuilder.withPageable(PageRequest.of(
                //注意在es里面，索引0才是第一页，所以要page-1
                searchRequest.getPage() - 1,
                searchRequest.getSize()));
        //进行查询并且返回
        AggregatedPage<Goods> pageGoods = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        //封装方法，从查询结果集获取聚合信息并且返回分类和品牌集合
        List<Map<String, Object>> category = this.getCategoryByAggPage(pageGoods);
        List<Brand> brand = this.getBrandByAggPage(pageGoods);
        //判断查询出来的品牌有多少，如果只有一个，就进行参数聚合查询
        Long categoryId = (Long) category.get(0).get("id");
        List<Map<String, Object>> params = null;
        if (!CollectionUtils.isEmpty(category) && category.size() == 1) {
            params = this.aggSpecParamSearch(categoryId, boolQuery);
        }
        return new SearchResult(
                pageGoods.getTotalElements(),
                pageGoods.getTotalPages(),
                pageGoods.getContent(),
                category,
                brand,
                params);
    }

    private BoolQueryBuilder buildBoolQuery(SearchRequest searchRequest) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //设置查询all字段,match会把查询语句进行分词再查询
        boolQuery.must(QueryBuilders.matchQuery("all", searchRequest.getKey()));
        //遍历前端传来的过滤条件，如果是品牌和分类过滤就单独设置一下过滤字段，如果是参数过滤就可以通用
        searchRequest.getFilter().forEach((k, v) -> {
            String key = k;
            if ("品牌".equals(k)) {
                key = "brandId";
            } else if ("分类".equals(k)) {
                key = "cid3";
            } else {
                key = "specs." + key + ".keyword";
            }
            boolQuery.filter(QueryBuilders.termQuery(key, v));
        });
        return boolQuery;
    }

    /**
     * 根据cid进行参数聚合，参数是可搜索参数,返回处理过的specParam聚合结果集
     *
     * @param categoryId 分类3 ID
     * @param query      搜索器
     * @return
     */
    private List<Map<String, Object>> aggSpecParamSearch(Long categoryId, QueryBuilder query) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(query);
        //设置什么字段都不需要，也就间接取消document结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        //根据分类ID查询所有可搜索参数
        List<SpecParam> params = this.specParamClient.querySpecParams(null, categoryId, null, true);
        //给所有参数都添加聚合搜索
        params.forEach(specParam -> {
            String specParamName = specParam.getName();
            String aggField = "specs." + specParamName + ".keyword";
            queryBuilder.addAggregation(AggregationBuilders.terms(specParamName).field(aggField));
        });
        //执行搜索
        AggregatedPage<Goods> searchResult = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        List<Map<String, Object>> specParam = new ArrayList<>();
        //遍历聚合结果集,拿到每一个聚合对象进行处理
        searchResult.getAggregations().forEach(aggregation -> {
            HashMap<String, Object> specNameAndValues = new HashMap<>();
            //获取agg聚合对象的名字，因为聚合名就是参数名，所以直接拿来当参数名保存
            String specName = aggregation.getName();
            //获取桶
            List<StringTerms.Bucket> buckets = ((StringTerms) aggregation).getBuckets();
            //这句话的意思是遍历buckets获取桶内每个元素的key并返回到新的list集合存储，list存储的是每个桶的所有元素，比如cpu核数这个桶存放八核、四核等等
            List<String> specValues = buckets.stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
            //用k存放参数名也就是桶名，options存放桶内所有的元素
            specNameAndValues.put("k", specName);
            specNameAndValues.put("options", specValues);
            specParam.add(specNameAndValues);
        });
        return specParam;
    }

    /**
     * 根据聚合对象获取对应的brand对象集合
     *
     * @param pageGoods
     * @return
     */
    private List<Brand> getBrandByAggPage(AggregatedPage<Goods> pageGoods) {
        LongTerms agg_brands = (LongTerms) pageGoods.getAggregation("agg_brand");
        List<Brand> brands = agg_brands.getBuckets().stream().map(bucket -> {
            long brandId = bucket.getKeyAsNumber().longValue();
            Brand brand = this.brandClient.queryBrandById(brandId);
            return brand;
        }).collect(Collectors.toList());
        return brands;
    }

    /**
     * 根据聚合对象获取对应的category对象的map表现形式
     *
     * @param pageGoods
     * @return
     */
    private List<Map<String, Object>> getCategoryByAggPage(AggregatedPage<Goods> pageGoods) {
        //获取名称为agg_category的聚合对象
        LongTerms agg_category = (LongTerms) pageGoods.getAggregation("agg_category");
        //获取品牌聚合对象上所有的桶
        List<LongTerms.Bucket> buckets = agg_category.getBuckets();
        List<Map<String, Object>> categoryMap = buckets.stream().map(bucket -> {
            HashMap<String, Object> category = new HashMap<>();
            //获取分类ID，去查询对应的分类名称
            long categoryId = bucket.getKeyAsNumber().longValue();
            String categoryName = this.categoryClient.queryNamesByIds(Arrays.asList(categoryId)).get(0);
            category.put("id", categoryId);
            category.put("name", categoryName);
            return category;
        }).collect(Collectors.toList());
        return categoryMap;
    }

    /**
     * 通过spuId删除搜索索引库里的内容
     *
     * @param spuId
     * @throws IOException
     */
    public void deleteGoodsBySpuId(Long spuId) {
        this.goodsRepository.deleteById(spuId);
    }

    /**
     * 通过spuId查询数据库更新搜索索引库里的内容
     *
     * @param spuId
     * @throws IOException
     */
    public void insertOrUpdateGoods(Long spuId) throws IOException {
        Spu spu = this.goodsClient.queryOneSpuById(spuId);
        Goods goods = this.buildGoods(spu);
        this.goodsRepository.save(goods);
    }
}
