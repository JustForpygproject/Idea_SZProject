package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;

    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Brand brand, Integer page, Integer rows) {
        //创建查询对象
        BrandQuery query = new BrandQuery();
        //组装条件
        if (brand != null) {
            //创建sql语句中的where条件对象
            BrandQuery.Criteria criteria = query.createCriteria();
            if (brand.getFirstChar() != null && !"".equals(brand.getFirstChar())) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
            if (brand.getName() != null && !"".equals(brand.getName())) {
                criteria.andNameLike("%"+brand.getName()+"%");
            }
        }
        PageHelper.startPage(page, rows);
        //查询
        Page<Brand> brandList = (Page<Brand>)brandDao.selectByExample(query);
        return new PageResult(brandList.getTotal(), brandList.getResult());
    }

    @Override
    public void add(Brand brand) {
        //添加的时候, 传入参数brand对象中的所有属性必须有值, 都参与添加
        //brandDao.insert();
        //添加的时候会判断brand传入参数对象中的属性是否有值, 如果没有值不参与添加, 如果有值再拼接到sql语句中参与添加
        brandDao.insertSelective(brand);
    }

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    /**
     * update tb_brand set xxx=xxx where name=xxx
     * @param brand
     */
    @Override
    public void update(Brand brand) {
        //根据查询条件更新, 这个条件不包括主键条件, 判断传入的参数brand中是否有为null的属性, 如果为null的属性不参与更新
        //brandDao.updateByExampleSelective(, )
        //根据查询条件更新, 这个条件不包括主键条件
        //brandDao.updateByExample(, )
        //根据主键作为条件更新
        //brandDao.updateByPrimaryKey();
        //根据主键作为条件更新, 判断传入的参数brand品牌对象中的属性如果为null不参与更新, 如果不为null才会被拼接到sql语句中执行
        brandDao.updateByPrimaryKeySelective(brand);

    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                brandDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Map> selectOptionList() {
        List<Map> list = brandDao.selectOptionList();
        return list;
    }
}
