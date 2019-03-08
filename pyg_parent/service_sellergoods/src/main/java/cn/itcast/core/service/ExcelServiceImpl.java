package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private GoodsDao goodsDao;

    @Override
    public List<User> findAll() {

        return  userDao.selectByExample(null);

    }

    @Override
    public List<Order> findOrderList() {
        return orderDao.selectByExample(null);
    }

    @Override
    public List<Goods> findGoodsList() {
        return goodsDao.selectByExample(null);
    }
}
