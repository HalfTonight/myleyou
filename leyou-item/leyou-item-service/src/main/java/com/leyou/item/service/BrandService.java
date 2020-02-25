package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.leyou.commom.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@Service
public class BrandService {
    @Autowired
    BrandMapper brandMapper;

    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化Example对象
        Example example = new Example(Brand.class);

        Example.Criteria criteria = example.createCriteria();


        //key:name或者letter(品牌首字母)
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+ key+"%").orEqualTo("letter");
        }
        //添加分页条件
        PageHelper.startPage(page,rows);

        //添加排序条件
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }
        //查询结果
        List<Brand> brands = this.brandMapper.selectByExample(example);

        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());


    }
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //先新增Brand
        this.brandMapper.insertSelective(brand);

        //在新增中间表
        cids.forEach(cid -> {
            this.brandMapper.insertBrandAndCategory(cid, brand.getId());
        });
    }

    public List<Brand> queryBrandByCid(Long cid) {

       return this.brandMapper.selectBandsByCid(cid);
    }

    public Brand queryBrandByid(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
