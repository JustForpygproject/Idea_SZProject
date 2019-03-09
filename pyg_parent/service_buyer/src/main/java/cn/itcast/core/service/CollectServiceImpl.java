package cn.itcast.core.service;

import cn.itcast.core.common.Constants;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.entity.BuyerCart;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@Transactional
public class CollectServiceImpl implements CollectionService {
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 将购买的商品加入到用户当前所拥有的收藏列表中
     * @param collectList  用户现在拥有的收藏列表
     * @param itemId    用户购买的商品库存id
     * @return
     */
    @Override
    public List<Item> addItemToCollectList(List<Item> collectList, Long itemId) {
        //1. 根据商品SKU ID查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        for (Item item1 : collectList) {
            if (item!=item1){
                collectList.add(item);
            }
        }
        //7. 返回收藏列表对象
        return collectList;
    }

    /**
     * 将收藏列表根据用户名存入redis
     * @param collectList  存入的收藏列表
     * @param userName  用户名
     */

    @Override
    public void setCollectListToRedis(List<Item> collectList, String userName) {
        redisTemplate.boundHashOps(Constants.REDIS_COLLECT_LIST).put(userName, collectList);
//        SetOperations<String, List<BuyerCollect>> set = redisTemplate.opsForSet();
//        redisTemplate.opsForValue().set(userName,collectList);
//        set.add(userName,collectList);
    }
    /**
     * 根据用户名到redis中获取收藏列表
     * @param userName  用户名
     * @return
     */

    @Override
    public List<Item> getCollectListFromRedis(String userName) {
        List<Item> collectList= (List<Item>)redisTemplate.boundHashOps(Constants.REDIS_COLLECT_LIST).get(userName);
//        List<BuyerCollect> collectList = (List<BuyerCollect>)redisTemplate.opsForSet().members(userName);
//        List<BuyerCollect> collectList = (List<BuyerCollect>)redisTemplate.opsForValue().get(userName);
        return collectList;
    }

    @Override
    public Item createitem(Long itemId) {
        //1. 根据商品SKU ID查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        return item;
    }


    /**
     * 在购物车集合中找到, 指定卖家的购物车对象返回, 如果找不到返回null
     * @param collectList  购物车集合
     * @param sellerId  卖家id
     * @return
     */
   /* private BuyerCollect searchCollectBySellerId(List<BuyerCollect> collectList, String sellerId) {
        if (collectList != null) {
            for (BuyerCollect collect : collectList) {
                //判断传入的卖家id参数和收藏对象中的卖家id是否相等
                if (collect.getSellerId().equals(sellerId)) {
                    return collect;
                }
            }
        }
        return null;
    }*/
    /**
     * 创建购物项对象, 也叫购物明细对象
     * @param item  库存对象
     * @return
     */
    private OrderItem createOrderItem(Item item) {
//        if (num < 1) {
//            throw new RuntimeException("购买数量非法!");
//        }
        OrderItem orderItem = new OrderItem();
        //购买数量
//        orderItem.setNum(num);
        //商品标题
        orderItem.setTitle(item.getTitle());
        //卖家id
        orderItem.setSellerId(item.getSellerId());
        //商品单价
        orderItem.setPrice(item.getPrice());
        //商品示例图片
        orderItem.setPicPath(item.getImage());
        //库存id
        orderItem.setItemId(item.getId());
        //商品id
        orderItem.setGoodsId(item.getGoodsId());
//        //总价 = 单价乘以购买数量
//        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }
    /**
     * 查找购物项集合中指定, 库存id的购物项对象, 有就返回, 没有就返回null
     * @param orderItemList 购物项集合
     * @param itemId        库存id
     * @return
     */
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {
        if (orderItemList != null) {
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(itemId)) {
                    return orderItem;
                }
            }
        }
        return null;
    }

}
