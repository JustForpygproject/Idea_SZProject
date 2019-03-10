package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.entity.SeckillEntity;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("seckill")
public class SeckillController {

    @Reference
    private SeckillService seckillService;
    @Reference
    private GoodsService goodsService;

    /**
     * 商家申请秒杀商品
     * @param seckillGoods
     * @return
     */
    @RequestMapping("addSeckillGoods")
    public Result addSeckillGoods(@RequestBody SeckillGoods seckillGoods){
        try {
            seckillService.add(seckillGoods);
            return new Result(true,"申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"申请失败");
        }
    }

    /**
     * 商家请求库存列表,用来选择作为秒杀商品的库存对象
     * @return
     */
    @RequestMapping("searchItemForSeckill")
    public List<Item> searchItemForSeckill(){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Item> itemList = goodsService.searchItemsBySellerId(sellerId);
        return itemList;
    }

}
