package cn.itcast.core.service;

import cn.itcast.core.common.Constants;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TypeTemplateDao templateDao;

    @Autowired
    private SpecificationOptionDao optionDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult findPage(Integer page, Integer rows, TypeTemplate template) {
        /**
         * 缓存品牌集合和规格集合数据到redis中供portal系统搜索使用
         */
        //查询所有模板数据
        List<TypeTemplate> templates = templateDao.selectByExample(null);
        if (templates != null) {
            for (TypeTemplate typeTemplate : templates) {
                //获取品牌json字符串
                String brandJsonStr = typeTemplate.getBrandIds();
                List<Map> maps = JSON.parseArray(brandJsonStr, Map.class);
                //缓存品牌集合到redis中
                redisTemplate.boundHashOps(Constants.REDIS_BRADND_LIST).put(typeTemplate.getId(), maps);

                //根据模板id获取规格和规格选项集合数据
                List<Map> speList = findBySpecList(typeTemplate.getId());
                redisTemplate.boundHashOps(Constants.REDIS_SPEC_LIST).put(typeTemplate.getId(), speList);
            }
        }

        /**
         * 分页查询
         */
        TypeTemplateQuery query = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = query.createCriteria();
        if (template != null) {
            if (template.getName() != null && !"".equals(template.getName())) {
                criteria.andNameLike("%"+template.getName()+"%");
            }
        }

        PageHelper.startPage(page, rows);
        Page<TypeTemplate> templateList = (Page<TypeTemplate>)templateDao.selectByExample(query);
        return new PageResult(templateList.getTotal(), templateList.getResult());
    }

    @Override
    public void add(TypeTemplate template) {
        templateDao.insertSelective(template);
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return templateDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(TypeTemplate template) {
        templateDao.updateByPrimaryKeySelective(template);
    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                templateDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        //1. 根据模板id查询模板对象
        TypeTemplate typeTemplate = templateDao.selectByPrimaryKey(id);
        //2. 从模板对象中获取规格集合数据
        String specJsonStr = typeTemplate.getSpecIds();
        //3. 解析规格集合数据
        List<Map> specList = JSON.parseArray(specJsonStr, Map.class);

        //4. 遍历解析后的规格集合数据
        if (specList != null) {
            for (Map map : specList) {
                //5. 在遍历过程中, 根据规格id查询规格选项集合数据
                Long specId = Long.parseLong(String.valueOf(map.get("id")));
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(specId);
                List<SpecificationOption> optionList = optionDao.selectByExample(query);
                //6. 将规格选项集合数据封装到规格集合中
                map.put("options", optionList);

            }
        }

        return specList;
    }
}
