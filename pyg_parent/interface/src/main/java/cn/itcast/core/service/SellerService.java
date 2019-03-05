package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;

public interface SellerService {

    public void add(Seller seller);

    public PageResult findPage(Seller seller, Integer page, Integer rows);

    public Seller findOne(String  id);

    public void updateStatus(String sellerId, String status);
}
