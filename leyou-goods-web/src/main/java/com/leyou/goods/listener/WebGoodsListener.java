package com.leyou.goods.listener;

import com.leyou.goods.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class WebGoodsListener {

    @Resource
    private GoodsHtmlService goodsHtmlService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "leyou.Web.updateGoods",
                    durable = "true"
            ),
            exchange = @Exchange(
                    name = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"leyou.item.update"}
    ))
    public void UpdateGoods(Long spuId){
        this.goodsHtmlService.createHtml(spuId);
    }
    public void InsertOrUpdateGoods(Long spuId){

    }
}
