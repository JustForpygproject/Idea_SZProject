package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao descDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ItemCatDao catDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    //上架使用, 上架的商品id发送到这个队列中
    @Autowired
    private ActiveMQTopic topicPageAndSolrDestination;

    //下架使用, 下架的伤心id发送到这个队列中
    @Autowired
    private ActiveMQQueue queueSolrDeleteDestination;

    @Override
    public void add(GoodsEntity goodsEntity) {
        //1. 添加商品对象
        //商品状态默认为0, 未审核状态
        goodsEntity.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsEntity.getGoods());

        //2. 添加详情对象
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        descDao.insertSelective(goodsEntity.getGoodsDesc());

        //3. 添加库存集合对象
        insertItemList(goodsEntity);

    }

    @Override
    public PageResult findPage(Integer page, Integer rows, Goods goods) {
        PageHelper.startPage(page, rows);
        //创建查询对象
        GoodsQuery query = new GoodsQuery();
        //创建where条件对象
        GoodsQuery.Criteria criteria = query.createCriteria();
        if (goods != null) {
            if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getGoodsName() != null && !"".equals(goods.getGoodsName())) {
                criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            //如果不是管理员用户, 根据用户名查询, 如果是管理员用户查询所有数据
            if (!"admin".equals(goods.getSellerId()) && !"wc".equals(goods.getSellerId())
                    && !"".equals(goods.getSellerId()) && goods.getSellerId() != null) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
        }
        Page<Goods> goodsList = (Page<Goods>)goodsDao.selectByExample(query);
        return new PageResult(goodsList.getTotal(), goodsList.getResult());
    }

    @Override
    public GoodsEntity findOne(Long id) {
        //1. 根据商品id查询商品对象
        Goods goods = goodsDao.selectByPrimaryKey(id);
        //2. 根据商品id查询商品详情对象
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(id);
        //3. 根据商品id查询库存集合对象
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);
        //4. 将以上查询到的对象封装到商品自定义实体中返回
        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setGoods(goods);
        goodsEntity.setGoodsDesc(goodsDesc);
        goodsEntity.setItemList(items);
        return goodsEntity;
    }

    @Override
    public void update(GoodsEntity goodsEntity) {
        //1. 保存修改商品对象
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());

        //2. 保存修改商品详情对象
        descDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());

        //3. 根据商品id删除对应的所有商品库存对象数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemDao.deleteByExample(query);

        //4. 从新添加保存商品的库存集合数据
        insertItemList(goodsEntity);
    }

    @Override
    public void dele(Long[] ids) {
        if (ids != null) {
            for (final Long id : ids) {
                /**
                 * 1. 到数据库中对商品进行逻辑删除
                 */
                Goods goods = new Goods();
                goods.setId(id);
                //设置删除状态为1, 已删除
                goods.setIsDelete("1");
                goodsDao.updateByPrimaryKeySelective(goods);

                /**
                 * 2. 将商品id作为消息发送给消息服务器
                 */
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });

            }
        }
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null) {
            for (final Long id : ids) {
                //1. 根据商品id修改商品表状态
                Goods goods = new Goods();
                goods.setId(id);
                goods.setAuditStatus(status);
                goodsDao.updateByPrimaryKeySelective(goods);

                //2. 根据商品id修改库存表状态
                //创建库存修改的对象
                Item item = new Item();
                item.setStatus(status);
                //创建条件对象
                ItemQuery query = new ItemQuery();
                ItemQuery.Criteria criteria = query.createCriteria();
                criteria.andGoodsIdEqualTo(id);
                itemDao.updateByExampleSelective(item, query);

                //3. 将商品id作为消息, 发送给消息服务器
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
            }
        }
    }

    /**
     * 初始化库存对象的属性值
     * @param goodsEntity 页面传入的商品, 商品详情和库存集合对象
     * @param item  需要初始化的库存对象
     * @return
     */
    private Item setItemValue(GoodsEntity goodsEntity, Item item) {
        //库存状态默认为0, 未审核
        item.setStatus("0");

        //卖家id
        item.setSellerId(goodsEntity.getGoods().getSellerId());
        //根据卖家id获取卖家对象
        Seller seller = sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        //卖家名称
        item.setSeller(seller.getName());

        //商品示例图片
        String imgJsonStr = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(imgJsonStr, Map.class);
        if (maps != null) {
            item.setImage(String.valueOf(maps.get(0).get("url")));
        }

        //获取品牌对象
        Brand brand = brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        item.setBrand(brand.getName());

        //品牌id
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        ItemCat itemCat = catDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());
        //分类名
        item.setCategory(itemCat.getName());
        //创建时间
        item.setCreateTime(new Date());
        //修改时间
        item.setUpdateTime(new Date());
        //商品id
        item.setGoodsId(goodsEntity.getGoods().getId());
        return item;
    }

    /**
     * 添加库存集合对象
     * @param goodsEntity
     */
    public void insertItemList(GoodsEntity goodsEntity) {
        if ("1".equals(goodsEntity.getGoods().getIsEnableSpec())) {
            //判断规格是否启用, 如果启用执行以下操作
            if (goodsEntity.getItemList() != null) {
                for (Item item : goodsEntity.getItemList()) {
                    //初始化库存对象的属性值
                    item = setItemValue(goodsEntity, item);

                    //库存标题: 使用商品名+规格组成
                    String title = goodsEntity.getGoods().getGoodsName();
                    //获取规格json字符串
                    String specJsonStr = item.getSpec();
                    //将规格转成java对象 {"机身内存":"16G","网络":"联通3G"}
                    Map specJsonMap = JSON.parseObject(specJsonStr, Map.class);
                    //获取所有的value
                    Collection<String> values = specJsonMap.values();
                    //遍历value集合
                    for (String value : values) {
                        title += " " + value;
                    }
                    //库存标题
                    item.setTitle(title);

                    itemDao.insertSelective(item);
                }
            }
        } else {
            //如果规格没有启用执行以下操作
            Item item = new Item();
            //初始化item对象中的属性值
            item = setItemValue(goodsEntity, item);
            //规格标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            //规格
            item.setSpec("{}");
            //价格
            item.setPrice(new BigDecimal("0"));
            //库存量
            item.setNum(0);
            //是否默认
            item.setIsDefault("1");
            itemDao.insertSelective(item);
        }
    }
}
