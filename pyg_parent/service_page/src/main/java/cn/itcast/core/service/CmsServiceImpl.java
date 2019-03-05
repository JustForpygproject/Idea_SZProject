package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService , ServletContextAware{

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao descDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao catDao;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    private ServletContext servletContext;

    @Override
    public void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws Exception {
        //1. 获取freemarker的初始化对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        //2. 获取模板对象, 指定加载的模板名称
        Template template = configuration.getTemplate("item.ftl");
        //3. 定义生成后的静态化页面存放的位置 使用商品id+.html组成页面名称
        String path = goodsId + ".html";
        //相对路径转成绝对路径, 例如: goodsId.html 转成 http://localhost:8086/goodsId.html
        String url = getRealPath(path);
        //4. 定义输出流
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(url)), "utf-8");
        //5. 生成
        template.process(rootMap, out);
        //6. 关闭流
        out.close();

    }

    /**
     * 获取当前项目运行的绝对路径
     * path : 相对路径
     * 返回绝对路径
     *
     */
    private String getRealPath(String path) {
        String realPath = servletContext.getRealPath(path);
        return realPath;
    }

    @Override
    public Map<String, Object> findGoods(Long goodsId) {
        Map<String, Object> rootMap = new HashMap<>();

        //1. 根据商品id获取商品对象
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        //2. 根据商品id获取商品详情对象
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(goodsId);
        //3. 根据商品id获取库存集合对象
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> items = itemDao.selectByExample(query);
        //4. 根据分类id获取对应的三级分类对象
        if (goods != null) {
            ItemCat itemCat1 = catDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = catDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = catDao.selectByPrimaryKey(goods.getCategory3Id());
            rootMap.put("itemCat1", itemCat1.getName());
            rootMap.put("itemCat2", itemCat2.getName());
            rootMap.put("itemCat3", itemCat3.getName());
        }

        //5. 将以上获取的对象封装到Map中返回
        rootMap.put("goods", goods);
        rootMap.put("goodsDesc", goodsDesc);
        rootMap.put("itemList", items);
        return rootMap;
    }

    /**
     * 本类实现了servletContextAware接口, 这个接口是spring框架负责实例化,
     * 实现这个接口的目的是为了给当前类中全局变量this.servletContext赋值, 也可以说是实例化
     * 由于spring实例化了servletContextAware接口中的servletContext对象, 所以实现servletContextAware接口,
     * 使用servletContextAware接口中的servletContext对象给当前类中的servletContextAware接口赋值就实例化了当前类中的
     * servletContext接口
     * @param servletContext
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
