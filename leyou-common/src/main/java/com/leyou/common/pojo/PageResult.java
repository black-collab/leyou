package com.leyou.common.pojo;

import java.util.List;

public class PageResult<T> {

    private Long total;
    private Integer totalPageCount;
    private List<T> items;

    public PageResult(Long total, Integer totalPageCount, List<T> items) {
        this.total = total;
        this.totalPageCount = totalPageCount;
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "totalCount=" + total +
                ", totalPageCount=" + totalPageCount +
                ", items=" + items +
                '}';
    }
}
