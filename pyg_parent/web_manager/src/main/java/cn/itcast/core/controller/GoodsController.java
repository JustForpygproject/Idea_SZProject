package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.CmsService;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

//    @Reference
//    private SolrManagerService solrManagerService;
//
//    @Reference
//    private CmsService cmsService;

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(userName);
        PageResult pageResult = goodsService.findPage(page, rows, goods);
        return pageResult;
    }

    /**
     * 修改商品状态
     * @param ids   商品id数组
     * @param status    状态码为1审核通过, 状态码为2驳回
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            //1. 根据商品id改变数据库中商品的上架状态
            goodsService.updateStatus(ids, status);
//            //判断审核通过的
//            if ("1".equals(status)) {
//                if (ids != null) {
//                    for (Long goodsId : ids) {
//                        //2. 根据商品id, 查询对应的库存集合数据放入solr索引库
//                        solrManagerService.importItemToSolr(goodsId);
//
//                        //3. 根据商品id, 获取商品详情, 商品, 库存集合等数据然后根据模板生成静态化页面
//                        Map<String, Object> goods = cmsService.findGoods(goodsId);
//                        //生成
//                        cmsService.createStaticPage(goodsId, goods);
//                    }
//                }
//            }
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }
}
