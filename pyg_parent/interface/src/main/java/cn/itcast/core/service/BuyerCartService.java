package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.BuyerCart;

import java.util.List;

public interface BuyerCartService {

    /**
     * 将购买的商品加入到用户当前所拥有的购物车列表中
     * @param cartList  用户现在拥有的购物车列表
     * @param itemId    用户购买的商品库存id
     * @param num       购买数量
     * @return
     */
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num);

    /**
     * 将购物车列表根据用户名存入redis
     * @param cartList  存入的购物车列表
     * @param userName  用户名
     */
    public void setCartListToRedis(List<BuyerCart> cartList, String userName);

    /**
     * 根据用户名到redis中获取购物车列表
     * @param userName  用户名
     * @return
     */
    public List<BuyerCart> getCartListFromRedis(String userName);

    /**
     * 将cookie中的购物车列表合并到redis的购物车列表中并返回合并后的购物车列表
     * @param cookieCartList    cookie的购物车列表
     * @param redisCartList     redis的购物车列表
     * @return  合并后的购物车列表
     */
    public List<BuyerCart> mergeCookieCartToRedisCart(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList);
}
