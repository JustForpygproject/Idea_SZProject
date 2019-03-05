package cn.itcast.core.controller;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 收货人地址管理
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    @RequestMapping("/findListByLoginUser")
    public List<Address> findListByLoginUser() {
        //1. 获取当前用户的登录名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //2. 根据用户名获取这个人收货地址列表
        List<Address> addressList = addressService.findListByLoginUser(userName);
        return addressList;
    }
}
