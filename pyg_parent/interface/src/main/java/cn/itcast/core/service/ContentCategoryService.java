package cn.itcast.core.service;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.PageResult;

import java.util.List;

public interface ContentCategoryService {

    public PageResult search(Integer page, Integer rows, ContentCategory category) ;

    public void add(ContentCategory category) ;

    public ContentCategory findOne(Long id) ;

    public void update(ContentCategory category);

    public void delete(Long[] ids);

    public List<ContentCategory> findAll();
}
