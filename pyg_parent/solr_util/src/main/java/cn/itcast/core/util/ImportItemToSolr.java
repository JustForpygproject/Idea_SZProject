package cn.itcast.core.util;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ImportItemToSolr {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemDao itemDao;


    public void importItem() {
        //1. 查询库存表所有static为1审核通过的数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo("1");
        List<Item> items = itemDao.selectByExample(query);
        //解析规格
        if (items != null) {
            for (Item item : items) {
                //获取规格就送字符串
                String specJsonStr = item.getSpec();
                Map<String, String> map = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(map);
            }
        }
        //2. 将查询到的数据导入到solr索引库
        solrTemplate.saveBeans(items);
        //3. 提交
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        //1. 创建spring运行环境对象, 加载spring配置文件
        ApplicationContext application = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        //2. 获取当前类对象
        ImportItemToSolr importItemSolr = (ImportItemToSolr)application.getBean("importItemToSolr");
        //3. 调用方法
        importItemSolr.importItem();

    }
}
