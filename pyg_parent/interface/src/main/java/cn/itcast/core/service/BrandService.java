package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<Brand> findAll();

    public PageResult findPage(Brand brand ,Integer page, Integer rows);

    public void add(Brand brand);

    public Brand findOne(Long id);

    public void update(Brand brand);

    public void delete(Long[] ids);

    public List<Map> selectOptionList();

    PageResult search(Integer page,Integer rows,Brand brand, String sellerId);

    void sellerAdd(String sellerId,Brand brand);

    boolean checkBrand(Brand brand);

    Brand findById(String sellerId, Long id);

    void updateSellerBrand(String sellerId, Brand brand);

    PageResult ManagerSearch(Integer page, Integer rows, Brand brand, String sellerId);

    void updateStatus(Long[] ids, String status);
}
