package cn.itcast.core.service;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressDao addressDao;

    @Override
    public List<Address> findListByLoginUser(String userName) {
        AddressQuery query = new AddressQuery();
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(userName);
        List<Address> addresses = addressDao.selectByExample(query);
        return addresses;
    }

    /**
     * 添加用户 地址
     *
     * @param address
     */
    @Override
    public void addUserAddress(Address address) {
        addressDao.insertSelective(address);
    }


    /**
     * 根据用户地址编号删除
     *
     * @param
     */
    @Override
    public void dele(long id) {
        addressDao.deleteByPrimaryKey(id);
    }

    /**
     * 根据编号查询地址
     *
     * @param id 编号
     * @return
     */
    @Override
    public Address findOne(Long id) {
        return addressDao.selectByPrimaryKey(id);

    }

    /**
     * 修改地址
     *
     * @param address
     */
    @Override
    public void updateAddress(Address address) {
        addressDao.updateByPrimaryKeySelective(address);
    }
}
