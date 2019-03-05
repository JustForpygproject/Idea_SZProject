package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

//    @Reference
//    private SolrManagerService solrManagerService;

    /**
     * 商品添加
     * @param goodsEntity
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsEntity goodsEntity) {
        try {
            //获取当前登录用户的用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsEntity.getGoods().setSellerId(userName);
            goodsService.add(goodsEntity);
            return new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败!");
        }
    }

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(userName);
        PageResult pageResult = goodsService.findPage(page, rows, goods);
        return pageResult;
    }

    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id) {
        return goodsService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity) {
        try {
            //1. 获取当前登录用户的用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //2. 判断当前登录用户的用户名和商品所属的用户名是否一致, 一致才允许修改
            if (!goodsEntity.getGoods().getSellerId().equals(userName)) {
                return new Result(false, "您没有权限修改此商品!");
            }
            //3. 保存修改
            goodsService.update(goodsEntity);
            return new Result(true, "修改成功!");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }

    @RequestMapping("/delete")
    public Result dele(Long[] ids) {
        try {
            //1. 到数据库中逻辑删除商品数据
            goodsService.dele(ids);
            //2. 根据商品id, 到solr索引库中删除对应的库存数据
//            if (ids != null) {
//                for (Long goodsId : ids) {
//                    solrManagerService.deleteItemByGoodsId(goodsId);
//                }
//            }
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }
}
