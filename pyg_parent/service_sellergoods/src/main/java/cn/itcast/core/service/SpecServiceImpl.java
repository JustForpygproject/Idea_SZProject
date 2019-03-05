package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.SpecEntity;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecServiceImpl implements SpecService {

    @Autowired
    private SpecificationDao specDao;

    @Autowired
    private SpecificationOptionDao optionDao;

    @Override
    public PageResult findPage(Integer page, Integer rows, Specification spec) {
        //创建查询对象
        SpecificationQuery query = new SpecificationQuery();
        //创建条件对象
        SpecificationQuery.Criteria criteria = query.createCriteria();
        if (spec != null) {
            if (spec.getSpecName() != null && !"".equals(spec.getSpecName())) {
                criteria.andSpecNameLike("%"+spec.getSpecName()+"%");
            }
        }

        PageHelper.startPage(page, rows);
        Page<Specification> specList = (Page<Specification>)specDao.selectByExample(query);
        return new PageResult(specList.getTotal(), specList.getResult());
    }

    @Override
    public void add(SpecEntity specEntity) {
        //1. 保存规格对象
        specDao.insertSelective(specEntity.getSpecification());
        //2. 遍历规格选项集合
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption option : specEntity.getSpecificationOptionList()) {
                //设置规格选项对象中的规格对象的主键id
                option.setSpecId(specEntity.getSpecification().getId());
                //3. 遍历过程中保存规格选项对象
                optionDao.insertSelective(option);
            }
        }


    }

    @Override
    public SpecEntity findOne(Long id) {
        //1. 根据规格id查询规格表数据
        Specification specification = specDao.selectByPrimaryKey(id);

        //2. 根据规格id查询规格选项表数据
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<SpecificationOption> optionList = optionDao.selectByExample(query);

        //3. 将规格数据和规格选项数据封装到实体类中返回
        SpecEntity entity = new SpecEntity();
        entity.setSpecification(specification);
        entity.setSpecificationOptionList(optionList);
        return entity;
    }

    @Override
    public void update(SpecEntity specEntity) {
        //1. 根据规格实体更新保存
        specDao.updateByPrimaryKeySelective(specEntity.getSpecification());

        //2. 根据规格id删除规格选项对应的数据
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpecification().getId());
        optionDao.deleteByExample(query);

        //3. 遍历页面传入的新的规格选项集合数据
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption option : specEntity.getSpecificationOptionList()) {
                //设置规格选项的外键
                option.setSpecId(specEntity.getSpecification().getId());
                //4. 保存新的规格选项对象
                optionDao.insertSelective(option);
            }
        }

    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                //1.根据规格id删除规格表数据
                specDao.deleteByPrimaryKey(id);
                //2. 根据规格id删除规格选项表数据
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(id);
                optionDao.deleteByExample(query);
            }
        }

    }

    @Override
    public List<Map> selectOptionList() {
        return specDao.selectOptionList();
    }
}
