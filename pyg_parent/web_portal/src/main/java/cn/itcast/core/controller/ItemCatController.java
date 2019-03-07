package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("itemcat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("findItemCatList")
    public List<ItemCat> findItemCatList(){
        return itemCatService.findItemCatList();
    }

}
