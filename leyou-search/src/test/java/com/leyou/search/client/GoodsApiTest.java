package com.leyou.search.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.search.LeyouSearchApplication;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = LeyouSearchApplication.class)
@RunWith(SpringRunner.class)
public class GoodsApiTest {

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private SearchService searchService;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private GoodsRepository goodsRepository;

    @Test
    public void querySkuBySpuId() {
        List<Sku> skus = this.goodsClient.querySkuBySpuId(2L);
        System.out.println(skus);
    }

    @Test
    public void createElasticsearchIndex() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }

    /**
     * 批量查询spu并且转换成Goods存入es服务器
     */
    @Test
    public void buildGoods() {
        int page = 1;
        int rows = 100;
        do {
            //分页查询spuBo
            PageResult<SpuBo> spuBoPageResult = this.goodsClient.querySpuBoByPage(null, null, page, rows);
            List<SpuBo> spuBos = spuBoPageResult.getItems();
            //把查询到的spuBo转换成Goods
            List<Goods> goodsList = spuBos.stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            //把Goods存入es服务器
            this.goodsRepository.saveAll(goodsList);
            page++;
            rows = spuBos.size();
            //当查询到最后一页，spuBo的数量不足100，就会停止循环
        } while (rows == 100);
    }
}