package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 广告分类管理
 */
@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody ContentCategory category) {
        return contentCategoryService.search(page, rows, category);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody ContentCategory category) {
        try {
            contentCategoryService.add(category);
            return  new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "添加失败!");
        }
    }

    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id) {
        ContentCategory one = contentCategoryService.findOne(id);
        return one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody ContentCategory category) {
        try {
            contentCategoryService.update(category);
            return  new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "修改失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            contentCategoryService.delete(ids);
            return  new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/findAll")
    public List<ContentCategory> findAll() {
        return contentCategoryService.findAll();
    }
}
