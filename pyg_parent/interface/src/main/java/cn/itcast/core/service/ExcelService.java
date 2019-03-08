package cn.itcast.core.service;


import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.user.User;


import java.util.List;

public interface ExcelService {

    public List<User> findAll();

    public List<Order> findOrderList();

    public List<Goods> findGoodsList();
}
