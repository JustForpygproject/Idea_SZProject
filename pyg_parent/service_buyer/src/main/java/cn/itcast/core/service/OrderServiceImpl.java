package cn.itcast.core.service;

import cn.itcast.core.common.Constants;
import cn.itcast.core.common.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.entity.BuyerCart;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.log.PayLogQuery;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private PayLogDao payLogDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;


    @Override
    public void add(Order pageOrder) {
        //1. 根据订单对象转入的用户名, 获取redis中购物车集合对象
        List<BuyerCart> cartList = (List<BuyerCart>) redisTemplate.boundHashOps(Constants.REDIS_CART_LIST).get(pageOrder.getUserId());
        //订单ID列表
        List<String> orderIdList = new ArrayList();
        //总金额 （元）
        double total_money = 0;

        //2. 遍历购物车集合对象
        if (cartList != null) {
            for (BuyerCart cart : cartList) {
                long orderId = idWorker.nextId();
                System.out.println("sellerId:" + cart.getSellerId());
                //新创建订单对象
                Order tborder = new Order();
                //订单ID
                tborder.setOrderId(orderId);
                //用户名
                tborder.setUserId(pageOrder.getUserId());
                //支付类型
                tborder.setPaymentType(pageOrder.getPaymentType());
                //状态：未付款
                tborder.setStatus("1");
                //订单创建日期
                tborder.setCreateTime(new Date());
                //订单更新日期
                tborder.setUpdateTime(new Date());
                //地址
                tborder.setReceiverAreaName(pageOrder.getReceiverAreaName());
                //手机号
                tborder.setReceiverMobile(pageOrder.getReceiverMobile());
                //收货人
                tborder.setReceiver(pageOrder.getReceiver());
                //订单来源
                tborder.setSourceType(pageOrder.getSourceType());
                //商家ID
                tborder.setSellerId(cart.getSellerId());
                //循环购物车明细
                double money = 0;


                //4. 从购物车对象中获取购物项集合对象
                List<OrderItem> orderItemList = cart.getOrderItemList();
                if (orderItemList != null) {
                    //5. 遍历购物项集合对象
                    for (OrderItem orderItem : orderItemList) {
                        orderItem.setId(idWorker.nextId());
                        //订单ID
                        orderItem.setOrderId(orderId);
                        orderItem.setSellerId(cart.getSellerId());
                        //金额累加
                        money += orderItem.getTotalFee().doubleValue();

                        //6. 根据购物项对象保存订单详情数据
                        orderItemDao.insertSelective(orderItem);
                    }
                }

                //保存订单对象
                tborder.setPayment(new BigDecimal(money));
                orderDao.insertSelective(tborder);
                //添加到订单列表
                orderIdList.add(orderId + "");
                //累加到总金额
                total_money += money;

                //将订单号存入redis
                redisTemplate.boundValueOps(orderId).set(orderId, 30, TimeUnit.MINUTES);
                //设置key的超时时间
                //redisTemplate.boundHashOps(Constants.ORDER_TIME_OUT).expire(30, TimeUnit.MINUTES);

                /*redisTemplate.opsForHash().put(Constants.ORDER_TIME_OUT, orderId, orderId);
                redisTemplate.expire(orderId, 30, TimeUnit.MINUTES);*/

            }
        }

        //8.最后根据需要支付的总金额保存支付日志数据
        //如果是微信支付
        if ("1".equals(pageOrder.getPaymentType())) {
            PayLog payLog = new PayLog();
            //支付订单号
            String outTradeNo = idWorker.nextId() + "";
            //支付订单号
            payLog.setOutTradeNo(outTradeNo);
            //创建时间
            payLog.setCreateTime(new Date());
            //订单号列表，逗号分隔
            String ids = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
            //订单号列表，逗号分隔
            payLog.setOrderList(ids);
            //支付类型
            payLog.setPayType("1");
            //总金额(分)
            payLog.setTotalFee((long) (total_money * 100));
            //支付状态
            payLog.setTradeState("0");
            //用户ID
            payLog.setUserId(pageOrder.getUserId());
            //插入到支付日志表
            payLogDao.insertSelective(payLog);
            //将支付日志保存到redis中一份
            redisTemplate.boundHashOps(Constants.REDIS_PAYLOG).put(pageOrder.getUserId(), payLog);
        }

        //9. 清除redis中支付后的购物车
        redisTemplate.boundHashOps(Constants.REDIS_CART_LIST).delete(pageOrder.getUserId());
    }

    @Override
    public void updatePayLogAndOrderStatus(String out_trade_no) {
        //1. 根据支付单号修改支付日志表, 支付状态为已支付
        PayLog payLog = new PayLog();
        payLog.setOutTradeNo(out_trade_no);
        payLog.setTradeState("1");
        payLogDao.updateByPrimaryKeySelective(payLog);
        //2. 根据支付单号查询对应的支付日志对象
        payLog = payLogDao.selectByPrimaryKey(out_trade_no);

        //3. 获取支付日志对象的订单号属性
        String orderListStr = payLog.getOrderList();
        //4. 根据订单号修改订单表的支付状态为已支付
        if (orderListStr != null) {
            String[] orderIdArray = orderListStr.split(",");
            if (orderIdArray != null) {
                for (String orderId : orderIdArray) {
                    Order order = new Order();
                    order.setOrderId(Long.parseLong(orderId));
                    order.setStatus("2");
                    orderDao.updateByPrimaryKeySelective(order);
                }
            }
        }

        //5. 根据用户名清除redis中未支付的支付日志对象
        redisTemplate.boundHashOps(Constants.REDIS_PAYLOG).delete(payLog.getUserId());
        //删除redis中订单超时的依据订单号
        redisTemplate.delete(payLog.getOrderList());
    }

    /**
     * 初始化订单(order)中的值
     *
     * @param order         订单
     * @param orderItemList 订单详细
     * @return
     */
    private void setOrder(Order order, List<OrderItem> orderItemList) {
        if (orderItemList != null) {
            for (OrderItem orderItem : orderItemList) {
                String orderIdStr = String.valueOf(order.getOrderId());
                order.setOrderIdStr(orderIdStr);
                order.setTitle(orderItem.getTitle());
                order.setPrice(orderItem.getPrice());
                order.setNum(orderItem.getNum());
                order.setTotalFee(orderItem.getTotalFee());
                order.setPicPath(orderItem.getPicPath());
                order.setPrice(orderItem.getPrice());
                //根据goodsId 查找规格
                ItemQuery itemQuery = new ItemQuery();
                ItemQuery.Criteria criteria = itemQuery.createCriteria();
                criteria.andGoodsIdEqualTo(orderItem.getGoodsId());
                List<Item> itemList = itemDao.selectByExample(itemQuery);
                for (Item item : itemList) {
                    String spec = item.getSpec().replace("{", "").
                            replace("}", "");
                    //规格封装给order订单
                    order.setGoodsSpec(spec);
                }
            }
        }
    }

    /**
     * 订单状态
     */
    static final String ORDER_STATUS_0 = "0";

    /**
     * 查询订单集合
     *
     * @param userName 用户名
     * @param status   订单状态
     * @return
     */
    private List<Order> getOrderList(String userName, String status) {
        //订单查询条件
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        //查询用户名下的订单
        if (userName != null) {
            criteria.andUserIdEqualTo(userName);
        }
        //订单状态查询
        if (status != null && !"".equals(status) && !ORDER_STATUS_0.equals(status)) {
            criteria.andStatusEqualTo(status);
        }
        //条件查询(当前用户的订单, 状态不为空订单支付状态查询)返回结果
        List<Order> orderList = orderDao.selectByExample(orderQuery);
        for (Order order : orderList) {
            //获取邮费
            String postFee = order.getPostFee();
            //判断邮费
            if (postFee == null) {
                //邮费为空初始化未0
                order.setPostFee("0");
            }
        }
        return orderList;
    }

    /**
     * 分页查询 全部订单数据
     *
     * @param page     当前页
     * @param rows     每页显示个数
     * @param userName 当前登陆用户名
     * @param status   订单状态
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, String userName, String status) {
        //数据异常,初始化为第一页
        if (page == 0) {
            page = 1;
        }
        //数据异常初始化为 显示3个
        if (rows == 0) {
            rows = 3;
        }
        //分页助手
        PageHelper.startPage(page, rows);
        //返回分页数据
        Page<Order> orderList = (Page<Order>) getOrderList(userName, status);
        //封装需要的数据返回
        if (orderList != null) {
            for (Order order : orderList) {
                //获取订单id
                Long orderId = order.getOrderId();
                //订单详细查询条件
                OrderItemQuery query = new OrderItemQuery();
                OrderItemQuery.Criteria criteria = query.createCriteria();
                criteria.andOrderIdEqualTo(orderId);
                //根据订单id获取订单详细信息
                List<OrderItem> orderItemList = orderItemDao.selectByExample(query);
                //order赋值
                setOrder(order, orderItemList);
            }
        }
        //分页数据
        return new PageResult(orderList.getTotal(), orderList.getResult());
    }

    /**
     * 根据订单号,查询payLog支付日志,
     *
     * @param orderIdStr 订单号
     * @return
     */
    @Override
    public PayLog findPayLog(String orderIdStr) {
        //redis中获取订单号
        String orderId = (String) redisTemplate.opsForHash().get(Constants.ORDER_TIME_OUT, orderIdStr);
        //获取不到订单超时
        if (orderId != null) {
            //支付日志查询条件
            PayLogQuery payQuery = new PayLogQuery();
            PayLogQuery.Criteria criteria = payQuery.createCriteria();
            if (orderIdStr != null) {
                criteria.andOrderListEqualTo(orderIdStr);
            }
            //根据订单id查询支付日志,返回结果
            List<PayLog> payLogList = payLogDao.selectByExample(payQuery);
            //遍历返回 支付日志
            if (payLogList != null) {
                for (PayLog payLog : payLogList) {
                    return payLog;
                }
            }
        }
        return null;
    }

    /**
     * 订单超时,修改订单状态
     *
     * @param orderIdStr 订单号
     */
    @Override
    public void updateOrderStatus(String orderIdStr) {
        Order order = new Order();
        order.setOrderId(Long.parseLong(orderIdStr));
        order.setStatus("6");
        orderDao.updateByPrimaryKeySelective(order);
    }


}
