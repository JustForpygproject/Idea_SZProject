package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 分页查询 全部订单数据
     *
     * @param page   当前页
     * @param rows   每页显示个数
     * @param status 订单状态
     * @return
     */
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, String status) {
        //获取当前登陆用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userName != null) {
            //查询订单返回结果
            PageResult pageResult = orderService.search(page, rows, userName, status);
            return pageResult;
        }
        return null;
    }

    static final String ORDER_PAY_TYPE_1 = "1";
    static final String ORDER_PAY_TYPE_2 = "2";
    /**
     * 根据订单查询支付日志
     *
     * @param orderIdStr 订单id
     * @return
     */
    @RequestMapping("goToPayPage")
    public Result goToPayPage(String orderIdStr) {
        try {
            //查询并返回结果
            PayLog payType = orderService.findPayLog(orderIdStr);
            //返回支付状态, 状态1 跳转微信页面, 2 跳转货到付款页面
            if (payType != null) {
                if (ORDER_PAY_TYPE_1.equals(payType.getPayType())) {
                    //返回1跳转微信页面
                    return new Result(true, payType.getPayType());
                }else if (ORDER_PAY_TYPE_2.equals(payType.getPayType())){
                    //跳转货到付款页面
                    return new Result(true, payType.getPayType());
                }else{
                    return new Result(false, "未知错误");
                }
            }else {
                //修改订单状态为 超时
                orderService.updateOrderStatus(orderIdStr);
                //redis中获取不到数据返回订单超时
                return new Result(false, "订单超时");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "跳转支付失败");
        }
    }

    /**
     * 取消订单
     * @param orderIdStr 订单号
     * @return
     */
    @RequestMapping("cancelOrder")
    public Result cancelOrder(String orderIdStr){
        try {
            orderService.updateOrderStatus(orderIdStr);
            return new Result(true, "1");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "取消失败了,稍后再来试试吧!");
        }
    }

}
