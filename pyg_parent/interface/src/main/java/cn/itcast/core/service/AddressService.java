package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;

import java.util.List;

public interface AddressService {

    public List<Address> findListByLoginUser(String userName);

    void addUserAddress(Address address);

    void dele(long id);

    Address findOne(Long id);

    void updateAddress(Address address);
}
