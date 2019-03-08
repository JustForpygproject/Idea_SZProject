package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 品牌管理
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询品牌所有数据
     * @return
     */
    @RequestMapping("/findAll")
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    /**
     * 分页查询
     * @param page  当前页
     * @param rows  每页展示多少条数据
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows) {
        PageResult pageResult= brandService.findPage(null ,page, rows);
        return pageResult;
    }

    /**
     * 添加
     * @param brand  品牌对象
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Brand brand) {
        try {
            brandService.add(brand);
            return  new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "添加失败!");
        }
    }

    /**
     * 修改回显数据
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Brand findOne(Long id) {
        Brand one = brandService.findOne(id);
        return one;
    }

    /**
     * 修改
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand) {
        try {
            brandService.update(brand);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            brandService.delete(ids);
            return  new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "删除失败!");
        }
    }

    /**
     * 高级查询(分页, 高级查询)
     * @param page  当前页
     * @param rows  每页展示多少条数据
     * @param brand 需要查询的条件品牌对象
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Brand brand) {
        PageResult pageResult = brandService.findPage(brand, page, rows);
        return pageResult;
    }

    /**
     * 查询品牌所有数据, 返回, 给模板中select2下拉框使用, 数据格式是select2下拉框规定的
     * 例如: $scope.brandList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]}
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }


    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            //1. 根据商品id改变数据库中商品的上架状态
            brandService.updateStatus(ids, status);
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
