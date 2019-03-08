package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody Brand brand) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return brandService.search(page, rows, brand, sellerId);
    }

    @RequestMapping("add")
    public Result add(@RequestBody Brand brand) {
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean checkBrand = brandService.checkBrand(brand);
            if (!checkBrand){
                return new Result(false,"品牌已添加");
            }
            brandService.sellerAdd(sellerId, brand);
            return new Result(true, "申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "申请失败");
        }
    }

    @RequestMapping("findById")
    public Brand findById(Long id){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return brandService.findById(sellerId,id);
    }

    @RequestMapping("update")
    public Result update(@RequestBody Brand brand){
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            brandService.updateSellerBrand(sellerId,brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

}
