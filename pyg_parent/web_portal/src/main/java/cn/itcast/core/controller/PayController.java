package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.service.OrderService;
import cn.itcast.core.service.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付业务
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    @Reference
    private OrderService orderService;

    /**
     * 生成支付链接
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //1. 获取当前登录用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //2. 根据当前用户名到redis中获取支付日志对象
        PayLog payLog = payService.findPageLogFromRedis(userName);
        //3. 根据支付日志的支付单号和总价格调用微信生成支付链接接口, 生成支付链接返回
        if (payLog != null) {
            //Map map = payService.createNative(payLog.getOutTradeNo(), String.valueOf(payLog.getTotalFee()));
            Map map = payService.createNative(payLog.getOutTradeNo(), "1");
            return map;
        } else {
            return new HashMap();
        }

    }

    /**
     * 根据支付单号, 查询是否支付成功
     * @param out_trade_no  支付单号
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) throws Exception {
        Result result = null;

        Integer flag = 1;

        //1. 死循环, 不停的查询是否支付成功
        while(true) {
            //2. 根据支付单号, 调用查询接口, 查询是否支付成功
            Map map = payService.queryPayStatus(out_trade_no);
            if (map == null) {
                result  = new Result(false, "二维码超时");
                break;
            }

            //3. 如果支付成功,
            if ("SUCCESS".equals(map.get("trade_state"))){
                //修改支付日志为已支付, 修改订单为已支付, redis中的待支付日志对象要删除
                orderService.updatePayLogAndOrderStatus(out_trade_no);
                //4. 返回支付成功信息
                result = new Result(true, "支付成功!");
                break;
            }

            //5. 每次查询后, 睡三秒
            Thread.sleep(3000);
            flag++;

            //半小时后如果还不支付, 则超时, 重新生成二维码, 继续扫描
            if (flag >= 600) {
                result  = new Result(false, "二维码超时");
                break;
            }
        }
        return result;
    }
}
