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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BuyerCartServiceImpl implements BuyerCartService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 将购买的商品加入到用户当前所拥有的购物车列表中
     * @param cartList  用户现在拥有的购物车列表
     * @param itemId    用户购买的商品库存id
     * @param num       购买数量
     * @return
     */
    @Override
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num) {
        //1. 根据商品SKU ID查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //2. 判断商品是否存在不存在, 抛异常
        if (item == null) {
            throw new RuntimeException("购买的商品不存在!");
        }
        //3. 判断商品状态是否为1已审核, 状态不对抛异常
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("不允许购买非法商品!");
        }
        //4.获取商家ID
        String sellerId = item.getSellerId();
        //5.根据商家ID查询购物车列表中是否存在该商家的购物车
        BuyerCart buyerCart = searchCartBySellerId(cartList, sellerId);
        //6.判断如果购物车列表中不存在该商家的购物车
        if (buyerCart == null) {
            //6.a.1 新建购物车对象
            buyerCart = new BuyerCart();
            //创建购物项集合
            List<OrderItem> orderItemList = new ArrayList<>();
            //创建购物项对象
            OrderItem orderItem = createOrderItem(item, num);
            //将购物项对象加入到购物项集合中
            orderItemList.add(orderItem);
            //将购物项集合加入到购物车中
            buyerCart.setOrderItemList(orderItemList);
            //购物车对象中设置卖家名称
            buyerCart.setSellerName(item.getSeller());
            //购物车对象中设置卖家id
            buyerCart.setSellerId(sellerId);
            //6.a.2 将新建的购物车对象添加到购物车列表
            cartList.add(buyerCart);
        } else {
            //6.b.1如果购物车列表中存在该商家的购物车 (查询购物车明细列表中是否存在该商品)
            List<OrderItem> orderItemList = buyerCart.getOrderItemList();
            //查询购物车明细列表中是否存在该商品
            OrderItem orderItem = searchOrderItemByItemId(orderItemList, itemId);
            //6.b.2判断购物车明细是否为空
            if (orderItem == null) {
                //6.b.3为空，新增购物车明细
                orderItem = createOrderItem(item, num);
                //将购物项对象加入到购物项集合中
                orderItemList.add(orderItem);
            } else {
                //6.b.4不为空，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum())));
                //6.b.5如果购物车明细中数量操作后小于等于0，则移除
                if (orderItem.getNum() <= 0) {
                    orderItemList.remove(orderItem);
                }
                //6.b.6如果购物车中购物车明细列表为空,则移除
                if (orderItemList.size() == 0) {
                    cartList.remove(buyerCart);
                }
            }
        }
        //7. 返回购物车列表对象
        return cartList;
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

    /**
     * 创建购物项对象, 也叫购物明细对象
     * @param item  库存对象
     * @param num   购买数量
     * @return
     */
    private OrderItem createOrderItem(Item item, Integer num) {
        if (num < 1) {
            throw new RuntimeException("购买数量非法!");
        }
        OrderItem orderItem = new OrderItem();
        //购买数量
        orderItem.setNum(num);
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
        //总价 = 单价乘以购买数量
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }

    /**
     * 在购物车集合中找到, 指定卖家的购物车对象返回, 如果找不到返回null
     * @param cartList  购物车集合
     * @param sellerId  卖家id
     * @return
     */
    private BuyerCart searchCartBySellerId(List<BuyerCart> cartList, String sellerId) {
        if (cartList != null) {
            for (BuyerCart cart : cartList) {
                //判断传入的卖家id参数和购物车对象中的卖家id是否相等
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }
        return null;
    }

    /**
     * 将购物车列表根据用户名存入redis
     * @param cartList  存入的购物车列表
     * @param userName  用户名
     */
    @Override
    public void setCartListToRedis(List<BuyerCart> cartList, String userName) {
        redisTemplate.boundHashOps(Constants.REDIS_CART_LIST).put(userName, cartList);
    }

    /**
     * 根据用户名到redis中获取购物车列表
     * @param userName  用户名
     * @return
     */
    @Override
    public List<BuyerCart> getCartListFromRedis(String userName) {
        List<BuyerCart> cartList= (List<BuyerCart>)redisTemplate.boundHashOps(Constants.REDIS_CART_LIST).get(userName);
        if (cartList == null) {
            cartList = new ArrayList<BuyerCart>();
        }
        return cartList;
    }

    /**
     * 将cookie中的购物车列表合并到redis的购物车列表中并返回合并后的购物车列表
     * @param cookieCartList    cookie的购物车列表
     * @param redisCartList     redis的购物车列表
     * @return  合并后的购物车列表
     */
    @Override
    public List<BuyerCart> mergeCookieCartToRedisCart(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList) {
        if (cookieCartList != null) {
            //遍历cookie的购物车集合
            for (BuyerCart cart : cookieCartList) {
                if (cart.getOrderItemList() != null) {
                    //遍历cookie的购物项集合
                    for (OrderItem orderItem : cart.getOrderItemList()) {
                        //将cookie的购物项加入到redis的购物车集合中
                        redisCartList = addItemToCartList(redisCartList, orderItem.getItemId(), orderItem.getNum());
                    }
                }
            }
        }

        return redisCartList;
    }
}
