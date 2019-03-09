package cn.itcast.core.controller;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.item.Item;

import cn.itcast.core.service.CollectionService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/collect")
public class CollectController {
    @Reference
    private CollectionService collectionService;

    /**
     * 将购物车商品添加到收藏列表
     * @param itemId
     * @return
     */
    @RequestMapping("/addItemToCollectList")
    @CrossOrigin(origins="http://localhost:8086",allowCredentials="true")
    public Result addItemToCollectList(Long itemId){
        //获取当前登录用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        //4. 判断当前用户是否登录, 未登录用户名为"anonymousUser"
        if ("anonymousUser".equals(userName)) {
            return new Result(false,"请您登陆");
        }
            //2. 获取收藏列表
            List<Item> collectList = findCollectList();

            if (collectList!=null){
                //3. 将当前商品加入到收藏列表
                collectList = collectionService.addItemToCollectList(collectList, itemId);
            }else{
                collectList = new ArrayList<Item>();
              Item item= collectionService.createitem(itemId);
                collectList.add(item);
            }
            //4.将收藏列表存入redis中
        collectionService.setCollectListToRedis(collectList, userName);
            return new Result(true, "添加收藏成功!");

    }
    /**
     * 查询当前用户收藏列表数据并返回到收藏列表页面
     * @return
     */
    @RequestMapping("/findCollectList")
    public List<Item> findCollectList() {
        //1. 获取当前登录用户名称
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

            List<Item> redisCollectList = collectionService.getCollectListFromRedis(userName);

            return  redisCollectList;
        }

    }

