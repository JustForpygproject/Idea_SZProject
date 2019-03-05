package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.util.List;
import java.util.Map;

@Service
public class SolrManagerServiceImpl implements SolrManagerService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemDao itemDao;

    @Override
    public void importItemToSolr(Long goodsId) {
        //1. 查询库存表所有static为1审核通过的数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        //查询状态为已审核的
        criteria.andStatusEqualTo("1");
        //查询商品id为指定商品的库存数据
        criteria.andGoodsIdEqualTo(goodsId);
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

    @Override
    public void deleteItemByGoodsId(Long goodsId) {
        //创建查询对象
        Query query = new SimpleQuery();
        //创建条件对象
        Criteria criteria = new Criteria("item_goodsid").is(goodsId);
        query.addCriteria(criteria);
        //根据查询条件删除, 这里的删除条件是根据商品id删除
        solrTemplate.delete(query);
        //提交
        solrTemplate.commit();
    }
}
