package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;


    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return  this.specGroupMapper.select(specGroup);
    }

    public List<SpecParam> queryParam(Long gid,Long cid,Boolean generic,Boolean serching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(serching);
        return this.specParamMapper.select(specParam);
    }

    public List<SpecGroup> queryGroupWithParam(Long cid) {
        //查询组
        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        //查询组下规格参数
        groups.forEach(group ->{
            List<SpecParam> params = this.queryParam(group.getId(), null, null, null);
            group.setParams(params);
        } );
        return groups;
    }


}
