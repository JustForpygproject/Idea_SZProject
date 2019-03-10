package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.SeckillEntity;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillOrder;

public interface SeckillService {

    /*商家申请秒杀商品*/
    void add(SeckillGoods seckillGoods);

    PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods);

    void updateStatus(Long[] ids, String status);

    /**
     * 商家查询秒杀订单
     * @param sellerId 商家id
     * @param page 当前页
     * @param rows 每页行数
     * @param seckillOrder 条件对象
     * @return 分页结果集
     */
    PageResult searchOrderList(String sellerId, Integer page, Integer rows, SeckillOrder seckillOrder);

    /**
     * 运营商查询秒杀订单
     * @param page 当前页
     * @param rows 每页行数
     * @param seckillOrder 条件对象
     * @return
     */
    PageResult searchOrderList(Integer page, Integer rows, SeckillOrder seckillOrder);
}
