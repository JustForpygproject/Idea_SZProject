package cn.itcast.core.service;


import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.SeckillEntity;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.pojo.seckill.SeckillOrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private SeckillOrderDao seckillOrderDao;
    @Autowired
    private ItemDao itemDao;

    @Override
    public void add(SeckillGoods seckillGoods) {

        /*此处封装seckillGoods对象
        1.封装goodsiud
        2.封装title
        3.封装smallpic
        4.封装price 原价格
        5.封装商家id sellerid
        6.封装审核状态
        * */
        //1.获取seckillentity中的itemid
        Long itemId = seckillGoods.getItemId();
        //2.根据itemid查询出item对象,获取goodsid,title,smallpic,price,sellerid
        Item item = itemDao.selectByPrimaryKey(itemId);
        seckillGoods.setGoodsId(item.getGoodsId());
        seckillGoods.setTitle(item.getTitle());
        seckillGoods.setSmallPic(item.getImage());
        seckillGoods.setPrice(item.getPrice());
        seckillGoods.setSellerId(item.getSellerId());
        seckillGoods.setSeller(item.getSeller());
        //审核状态初始化为0 未审核
        seckillGoods.setStatus("0");
        seckillGoods.setCreateTime(new Date());
        seckillGoodsDao.insertSelective(seckillGoods);

    }

    @Override
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {
        SeckillGoodsQuery query = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = query.createCriteria();
        if (seckillGoods.getSeller() != null && !"".equals(seckillGoods.getSeller())) {
            criteria.andTitleLike("%" + seckillGoods.getSeller() + "%");
        }
        criteria.andStatusEqualTo("0");
        PageHelper.startPage(page, rows);
        List<SeckillGoods> seckillGoodsList = seckillGoodsDao.selectByExample(query);
        PageInfo<SeckillGoods> pageInfo = new PageInfo<>(seckillGoodsList);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null) {
            for (Long id : ids) {

                SeckillGoods seckillGoods = new SeckillGoods();
                seckillGoods.setId(id);
                seckillGoods.setStatus(status);
                seckillGoods.setCheckTime(new Date());
                seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);

            }
        }
    }

    @Override
    public PageResult searchOrderList(String sellerId, Integer page, Integer rows, SeckillOrder seckillOrder) {
        SeckillOrderQuery query = new SeckillOrderQuery();
        SeckillOrderQuery.Criteria criteria = query.createCriteria();
        if (seckillOrder.getUserId() != null && !"".equals(seckillOrder.getUserId())){
            criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
        }
        criteria.andSellerIdEqualTo(sellerId);
        PageHelper.startPage(page,rows);
        List<SeckillOrder> seckillOrderList = seckillOrderDao.selectByExample(query);
        PageInfo<SeckillOrder> pageInfo = new PageInfo<>(seckillOrderList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public PageResult searchOrderList(Integer page, Integer rows, SeckillOrder seckillOrder) {
        SeckillOrderQuery query = new SeckillOrderQuery();
        SeckillOrderQuery.Criteria criteria = query.createCriteria();
        if (seckillOrder.getUserId() != null && !"".equals(seckillOrder.getUserId())){
            criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
        }
        if (seckillOrder.getSellerId() != null && !"".equals(seckillOrder.getSellerId())){
            criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
        }
        PageHelper.startPage(page,rows);
        List<SeckillOrder> seckillOrderList = seckillOrderDao.selectByExample(query);
        PageInfo<SeckillOrder> pageInfo = new PageInfo<>(seckillOrderList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }
}
