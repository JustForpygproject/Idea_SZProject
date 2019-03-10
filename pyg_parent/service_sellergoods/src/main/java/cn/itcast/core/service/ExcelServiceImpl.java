package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.util.ReadExcel;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private BrandDao brandDao;

    @Override
    public List<User> findAll() {

        return  userDao.selectByExample(null);

    }

    @Override
    public List<Order> findOrderList() {
        return orderDao.selectByExample(null);
    }

    @Override
    public List<Goods> findGoodsList() {
        return goodsDao.selectByExample(null);
    }

    @Override
    public List<Brand> findBrandList() {
        return brandDao.selectByExample(null);

    }

    @Override
    public void importExcel() throws IOException {
        ReadExcel readExcel = new ReadExcel();
        List<Brand> brandList = readExcel.readXls();
        for (Brand brand : brandList) {
            brandDao.insertSelective(brand);

        }

    }
}
