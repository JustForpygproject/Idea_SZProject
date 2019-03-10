package cn.itcast.core.controller;


import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("sales_volume")
public class SalesVolumeController {

    @Reference
    private OrderService orderService;

    @RequestMapping("findSalesVolume")
    public Map<String,Object> findSalesVolume(Date startDate,Date endDate){
        //获取卖家id
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //创建结果集合
        HashMap<String, Object> resultMap = new HashMap<>();
        //根据卖家id,起止时间,查询order集合
        List<Order> orderList = orderService.findOrderList(sellerId,startDate,endDate);
        //获取日期集合
        List<String> days = getDays(startDate, endDate);
        resultMap.put("days",days);
        //根据日期集合,获取每个日期的销售额
        List<Double> salesVolumeList = orderService.findSalesVolume(days,orderList);
        resultMap.put("salesVolumeList",salesVolumeList);
        return resultMap;
    }

    /**
     * 获取两个日期之间的天数集合
     * @param startDate
     * @param endDate
     * @return
     */
    private List<String> getDays(Date startDate,Date endDate) {

        // 返回的日期集合
        List<String> days = new ArrayList<String>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String startTime = dateFormat.format(startDate);
            String endTime = dateFormat.format(endDate);
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

}
