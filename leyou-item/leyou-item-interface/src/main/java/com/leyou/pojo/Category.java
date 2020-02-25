package com.leyou.pojo;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import  javax.persistence.Id;
import javax.persistence.Table;

//idbigint(20) NOT NULL类目id
//namevarchar(32) NOT NULL类目名称
//parent_idbigint(20) NOT NULL父类目id,顶级类目填0
//is_parenttinyint(1) NOT NULL是否为父节点，0为否，1为是
//sortint(4) NOT NULL排序指数，越小越靠前
@Table(name="tb_category")
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long parentId;

    private Boolean isParent;

    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean parent) {
        isParent = parent;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
