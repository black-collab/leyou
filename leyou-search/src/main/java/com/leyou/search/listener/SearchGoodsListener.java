package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class SearchGoodsListener {

    @Resource
    private SearchService searchService;

    /**
     * 根据spuid更新搜索索引库里的spu
     *
     * @param spuId
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "leyou.search.insertOrUpdate", //队列名
                    durable = "true"    //是否持久化存储
            ),
            exchange = @Exchange(
                    name = "leyou.item.exchange",   //设置交换机名
                    //本来如果已经有这个交换机，会使用已有交换机
                    // 如果交换机参数设置和已有的不一样，会报错，这里设置为true，直接使用已有交换机，不报错
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"leyou.item.insert", "leyou.item.update"}
    ))
    //会创建一个监听者对象放入监听者容器，监听者对象监听这个队列，一旦有消息就会运行此方法
    public void InsertOrUpdateGoods(Long spuId) throws IOException {
        this.searchService.insertOrUpdateGoods(spuId);
    }

    /**
     * 通过spuId删除搜索索引库里的商品
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "leyou.search.delete",
                    durable = "true"),
            exchange = @Exchange(
                    name = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"leyou.item.delete"}
    ))
    public void GoodsDelete(Long spuId) {
        this.searchService.deleteGoodsBySpuId(spuId);
    }
}
