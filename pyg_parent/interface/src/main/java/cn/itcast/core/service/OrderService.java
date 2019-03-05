package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;

public interface OrderService {

    /**
     * 保存订单
     * 涉及到三张表, payLog支付日志, order订单表, orderItem订单详情表
     * @param order  页面提交的订单对象数据, 这个对象不直接保存到数据库中, 只是需要页面提交过来的
     *                 收货人地址, 收货人姓名, 收货人电话, 支付方式等信息.
     */
    public void add(Order order);

    /**
     * 根据支付单号修改, 支付状态为已支付
     * @param out_trade_no 支付单号
     */
    public void  updatePayLogAndOrderStatus(String out_trade_no);
}
