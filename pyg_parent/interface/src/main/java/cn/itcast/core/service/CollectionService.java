package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;

import java.util.List;

public interface CollectionService {
    /**
     * 将购买的商品加入到用户当前所拥有的收藏列表中
     * @param collectList  用户现在拥有的收藏列表
     * @param itemId    用户购买的商品库存id
     * @return
     */
    public List<Item> addItemToCollectList(List<Item> collectList, Long itemId);
    /**
     * 将收藏列表根据用户名存入redis
     * @param collectList  存入的收藏列表
     * @param userName  用户名
     */
    public void setCollectListToRedis(List<Item> collectList, String userName);

    /**
     * 根据用户名到redis中获取收藏列表
     * @param userName  用户名
     * @return
     */
    public List<Item> getCollectListFromRedis(String userName);


    Item createitem(Long itemId);

}
