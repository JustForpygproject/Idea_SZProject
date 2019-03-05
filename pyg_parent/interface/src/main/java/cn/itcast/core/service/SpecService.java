package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.SpecEntity;
import cn.itcast.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecService {

    public PageResult findPage(Integer page, Integer rows, Specification spec);

    public void add(SpecEntity specEntity);

    public SpecEntity findOne(Long id);

    public void update(SpecEntity specEntity);

    public void delete(Long[] ids);

    public List<Map> selectOptionList();
}
