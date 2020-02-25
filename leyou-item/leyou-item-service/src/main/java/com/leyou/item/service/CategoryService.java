package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService {

    /**
     *根据父id查询
     *
     */
    @Autowired
    CategoryMapper categoryMapper;
    public List<Category> queryCategoriesById(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return this.categoryMapper.select(category);

    }
    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> list = this.categoryMapper.selectByIdList(ids);
        List<String> names = new ArrayList<>();
        for (Category category : list) {
            names.add(category.getName());
        }
        return names;
        // return list.stream().map(category -> category.getName()).collect(Collectors.toList());
    }
    public List<Category> queryAllByCid3(Long cid3) {
        Category category1 = this.categoryMapper.selectByPrimaryKey(cid3);
        Category category2 = this.categoryMapper.selectByPrimaryKey(category1.getParentId());
        Category category3 = this.categoryMapper.selectByPrimaryKey(category2.getParentId());
        return Arrays.asList(category1,category2,category3);
    }
}
