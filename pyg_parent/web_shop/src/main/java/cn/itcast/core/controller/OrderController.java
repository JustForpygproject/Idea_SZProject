package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @RequestMapping("findAll")
    public List<Order> findAll(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Order> orderList = orderService.fallAll(username);
        return orderList;
    }
    @RequestMapping("sendGood")
    public Result sendGood(Long orderId){
        try {
            orderService.update(orderId);
            return  new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"修改失败");
        }
    }
}
