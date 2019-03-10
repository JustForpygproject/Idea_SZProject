package cn.itcast.core.service;

import cn.itcast.core.common.Constants;
import cn.itcast.core.common.IdWorker;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.entity.BuyerCart;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements  OrderService {

    @Autowired
    private PayLogDao payLogDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;


    @Override
    public void add(Order pageOrder) {
        //1. 根据订单对象转入的用户名, 获取redis中购物车集合对象
        List<BuyerCart> cartList = (List<BuyerCart>)redisTemplate.boundHashOps(Constants.REDIS_CART_LIST).get(pageOrder.getUserId());

        List<String> orderIdList=new ArrayList();//订单ID列表
        double total_money=0;//总金额 （元）

        //2. 遍历购物车集合对象
        if (cartList != null) {
            for (BuyerCart cart : cartList) {
                long orderId = idWorker.nextId();
                System.out.println("sellerId:"+cart.getSellerId());
                Order tborder=new Order();//新创建订单对象
                tborder.setOrderId(orderId);//订单ID
                tborder.setUserId(pageOrder.getUserId());//用户名
                tborder.setPaymentType(pageOrder.getPaymentType());//支付类型
                tborder.setStatus("1");//状态：未付款
                tborder.setCreateTime(new Date());//订单创建日期
                tborder.setUpdateTime(new Date());//订单更新日期
                tborder.setReceiverAreaName(pageOrder.getReceiverAreaName());//地址
                tborder.setReceiverMobile(pageOrder.getReceiverMobile());//手机号
                tborder.setReceiver(pageOrder.getReceiver());//收货人
                tborder.setSourceType(pageOrder.getSourceType());//订单来源
                tborder.setSellerId(cart.getSellerId());//商家ID
                //循环购物车明细
                double money=0;


                //4. 从购物车对象中获取购物项集合对象
                List<OrderItem> orderItemList = cart.getOrderItemList();
                if (orderItemList != null) {
                    //5. 遍历购物项集合对象
                    for (OrderItem orderItem : orderItemList) {
                        orderItem.setId(idWorker.nextId());
                        orderItem.setOrderId( orderId  );//订单ID
                        orderItem.setSellerId(cart.getSellerId());
                        money+=orderItem.getTotalFee().doubleValue();//金额累加

                        //6. 根据购物项对象保存订单详情数据
                        orderItemDao.insertSelective(orderItem);
                    }
                }

                //保存订单独享
                tborder.setPayment(new BigDecimal(money));
                orderDao.insertSelective(tborder);
                orderIdList.add(orderId+"");//添加到订单列表
                total_money+=money;//累加到总金额

            }
        }

        //8.最后根据需要支付的总金额保存支付日志数据
        if("1".equals(pageOrder.getPaymentType())){//如果是微信支付
            PayLog payLog=new PayLog();
            String outTradeNo=  idWorker.nextId()+"";//支付订单号
            payLog.setOutTradeNo(outTradeNo);//支付订单号
            payLog.setCreateTime(new Date());//创建时间
            //订单号列表，逗号分隔
            String ids=orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(ids);//订单号列表，逗号分隔
            payLog.setPayType("1");//支付类型
            payLog.setTotalFee( (long)(total_money*100 ) );//总金额(分)
            payLog.setTradeState("0");//支付状态
            payLog.setUserId(pageOrder.getUserId());//用户ID
            payLogDao.insertSelective(payLog);//插入到支付日志表
            //将支付日志保存到redis中一份
            redisTemplate.boundHashOps(Constants.REDIS_PAYLOG).put(pageOrder.getUserId(), payLog);
        }

        //9. 清除redis中支付后的购物车
        redisTemplate.boundHashOps(Constants.REDIS_CART_LIST).delete(pageOrder.getUserId());
    }

    @Override
    public List<Order> fallAll(String username) {
        OrderQuery query =  new OrderQuery();
        OrderQuery.Criteria criteria = query.createCriteria();
        criteria.andSellerIdEqualTo(username);
        List<Order> orderList = orderDao.selectByExample(query);
        return orderList;
    }
}
