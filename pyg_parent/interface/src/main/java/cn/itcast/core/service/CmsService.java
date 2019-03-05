package cn.itcast.core.service;

import java.util.Map;

public interface CmsService {

    public void createStaticPage(Long goodsId, Map<String, Object> rootMap)throws Exception;

    public Map<String, Object> findGoods(Long goodsId) ;
}
