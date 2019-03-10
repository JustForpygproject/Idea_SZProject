package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.SeckillEntity;
import cn.itcast.core.pojo.seckill.SeckillGoods;

public interface SeckillService {

    /*商家申请秒杀商品*/
    void add(SeckillGoods seckillGoods);

    PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods);

    void updateStatus(Long[] ids, String status);
}
