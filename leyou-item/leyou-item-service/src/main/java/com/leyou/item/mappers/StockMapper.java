package com.leyou.item.mappers;

import com.leyou.item.pojo.Stock;

import java.util.List;

public interface StockMapper {
    void saveStock(Stock stock);

    void deleteStockBySkuIds(List<Long> skuIds);
}
