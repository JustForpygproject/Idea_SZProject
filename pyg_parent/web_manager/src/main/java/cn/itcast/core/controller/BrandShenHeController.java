package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("brandShenHe")
public class BrandShenHeController {

    @Reference
    private BrandService brandService;

    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody Brand brand) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return brandService.ManagerSearch(page, rows, brand, sellerId);
    }

    @RequestMapping("updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            brandService.updateStatus(ids,status);
            return new Result(true,"审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"审核失败");
        }
    }



}
