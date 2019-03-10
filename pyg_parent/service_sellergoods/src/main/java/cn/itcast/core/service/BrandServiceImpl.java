package cn.itcast.core.service;

import cn.itcast.core.common.IdWorker;
import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private IdWorker idWorker;

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
                criteria.andNameLike("%" + brand.getName() + "%");
            }
        }
        PageHelper.startPage(page, rows);
        //查询
        Page<Brand> brandList = (Page<Brand>) brandDao.selectByExample(query);
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
     *
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

    @Override
    public PageResult search(Integer page, Integer rows, Brand brand, String sellerId) {
        PageHelper.startPage(page, rows);
        List<Brand> brandList = (List<Brand>) redisTemplate.boundHashOps("sellerBrandList").get(sellerId);
        if (brandList == null) {
            brandList = new ArrayList<Brand>();
        }
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);
        return new PageResult(brandList.size(), brandList);
    }

    @Override
    public void sellerAdd(String sellerId, Brand brand) {
        brand.setStatus("0");
        brand.setId(Long.parseLong("" + ((int) (Math.random() * 10)) + System.currentTimeMillis()));
        List<Brand> brandList = (List<Brand>) redisTemplate.boundHashOps("sellerBrandList").get(sellerId);
        if (brandList == null) {
            brandList = new ArrayList<Brand>();
        }
        brandList.add(brand);
        redisTemplate.boundHashOps("sellerBrandList").put(sellerId, brandList);
    }

    @Override
    public boolean checkBrand(Brand brand) {
        List<Brand> brandList = brandDao.selectByExample(null);
        if (brandList != null) {
            for (Brand brand1 : brandList) {
                if (brand.getName().equals(brand1.getName())) {
                    return false;
                }
            }
        }
        List<Seller> sellers = sellerDao.selectByExample(null);
        if (sellers != null) {
            for (Seller seller : sellers) {
                String sellerId = seller.getSellerId();
                List<Brand> sellerBrandList = (List<Brand>) redisTemplate.boundHashOps("sellerBrandList").get(sellerId);
                if (sellerBrandList != null) {
                    for (Brand sellerBrand : sellerBrandList) {
                        if (brand.getName().equals(sellerBrand.getName())) {
                            return false;
                        }
                    }
                }

            }
        }
        return true;

    }

    @Override
    public Brand findById(String sellerId, Long id) {
        List<Brand> brandList = (List<Brand>) redisTemplate.boundHashOps("sellerBrandList").get(sellerId);
        if (brandList != null) {
            for (Brand brand : brandList) {
                if (id.equals(brand.getId())) {
                    return brand;
                }
            }
        }
        return null;
    }

    @Override
    public void updateSellerBrand(String sellerId, Brand brand) {
        List<Brand> brandList = (List<Brand>) redisTemplate.boundHashOps("sellerBrandList").get(sellerId);
        if (brandList != null) {
            for (Brand sellerBrand : brandList) {
                if (sellerBrand.getId().equals(brand.getId())) {
                    brandList.remove(sellerBrand);
                    brand.setStatus("0");
                    brandList.add(brand);
                }
            }
            redisTemplate.boundHashOps("sellerBrandList").put(sellerId, brandList);
        }
    }

    @Override
    public PageResult ManagerSearch(Integer page, Integer rows, Brand brand, String sellerId) {
        List<Seller> sellers = sellerDao.selectByExample(null);
        ArrayList<Brand> managerBrandList = new ArrayList<>();
        if (sellers != null) {
            for (Seller seller : sellers) {
                String sellerId1 = seller.getSellerId();
                List<Brand> brandList = (List<Brand>) redisTemplate.boundHashOps("sellerBrandList").get(sellerId1);
                if (brandList != null) {
                    managerBrandList.addAll(brandList);
                }
            }
        }
        return new PageResult(managerBrandList.size(), managerBrandList);
    }

    /**
     * 审核品牌
     * @param ids id集合
     * @param status 前台传入的状态
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null){
            for (Long id : ids) {
                List<Seller> sellers = sellerDao.selectByExample(null);
                if (sellers != null) {
                    for (Seller seller : sellers) {
                        String sellerId1 = seller.getSellerId();
                        List<Brand> brandList = (List<Brand>) redisTemplate.boundHashOps("sellerBrandList").get(sellerId1);
                        if (brandList != null) {
                            for (int i = 0;i < brandList.size();i++) {
                                if (id.equals(brandList.get(i).getId())){
                                    if ("1".equals(status)){
                                        add(brandList.get(i));
                                        brandList.remove(brandList.get(i));
                                        redisTemplate.boundHashOps("sellerBrandList").put(sellerId1,brandList);
                                        if (brandList.size() == 0){
                                            return;
                                        }
                                    }
                                    if ("2".equals(status)){
                                        brandList.get(i).setStatus("2");
                                        redisTemplate.boundHashOps("sellerBrandList").put(sellerId1,brandList);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
