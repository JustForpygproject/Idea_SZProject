package cn.itcast.core.service;

import cn.itcast.core.common.Constants;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        /**
         * 查询
         */
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<ItemCat> itemCats = itemCatDao.selectByExample(query);

        /**
         * 缓存所有分类数据到redis中, 供portal系统搜索使用
         */
        List<ItemCat> catList = itemCatDao.selectByExample(null);
        if (catList != null) {
            for (ItemCat itemCat : catList) {
                redisTemplate.boundHashOps(Constants.REDIS_CATEGORY).put(itemCat.getName(), itemCat.getTypeId());
            }
        }


        return itemCats;
    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    @Override
    public List<ItemCat> findItemCatList() {
        List<ItemCat> itemCatList = (List<ItemCat>) redisTemplate.boundHashOps(Constants.REDIS_CATEGORY).get(Constants.INDEX_ITEMCAT_LIST);
        if (itemCatList == null) {
            List<ItemCat> itemCatList1 = findByParentId(0L);
            if (itemCatList1 != null){
                for (ItemCat itemCat1 : itemCatList1) {
                    List<ItemCat> itemCatList2 = findByParentId(itemCat1.getId());
                    if (itemCatList2 != null){
                        for (ItemCat itemCat2 : itemCatList2) {
                            List<ItemCat> itemCatList3 = findByParentId(itemCat2.getId());
                            itemCat2.setItemCatList(itemCatList3);
                        }
                    }
                    itemCat1.setItemCatList(itemCatList2);
                }
            }
            redisTemplate.boundHashOps(Constants.REDIS_CATEGORY).put(Constants.INDEX_ITEMCAT_LIST,itemCatList1);
            return itemCatList1;
        }

        return itemCatList;
    }
}
