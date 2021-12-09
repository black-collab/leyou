package com.leyou.item.pojo;

//@Table(name = "tb_category")
public class Category {

    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnType(column = "id",jdbcType = JdbcType.BIGINT)*/
    private long id;
    private String name;
    private long parentId;
    //@ColumnType(column = "is_parent",jdbcType = JdbcType.TINYINT)
    private boolean isParent;
    private Integer sort;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(boolean parent) {
        isParent = parent;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", isParent=" + isParent +
                ", sort=" + sort +
                '}';
    }
}
