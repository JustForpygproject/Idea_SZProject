package cn.itcast.core.service;

import cn.itcast.core.pojo.log.PayLog;

import java.util.Map;

public interface PayService {

    /**
     * 根据支付单号和总价格生成支付链接
     * @param out_trade_no  支付单号
     * @param total_fee     总价格
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);

    /**
     * 根据支付单号查询是否支付成功
     * @param out_trade_no  支付单号
     * @return
     */
    public Map queryPayStatus(String out_trade_no);

    /**
     * 从redis中根据用户名获取支付日志对象
     * @param userName
     * @return
     */
    public PayLog findPageLogFromRedis(String userName);
}
