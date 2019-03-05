package cn.itcast.core.service;

public interface SolrManagerService {

    public void importItemToSolr(Long goodsId);

    public void deleteItemByGoodsId(Long goodsId);
}
