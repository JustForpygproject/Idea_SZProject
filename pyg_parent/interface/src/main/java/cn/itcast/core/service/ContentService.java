package cn.itcast.core.service;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.entity.PageResult;

import java.util.List;

public interface ContentService {

    public PageResult search(Integer page, Integer rows, Content content) ;

    public void add(Content content) ;

    public Content  findOne(Long id) ;

    public void update(Content content);

    public void delete(Long[] ids);

    public List<Content> findByCategoryId(Long categoryId);

    public List<Content> findByCategoryIdFromRedis(Long categoryId);
}
