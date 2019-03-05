package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限管理实现类
 * 根据用户名到数据库的seller表中获取用户详细信息
 * 返回springSecurity规定的User对象, 给springSecurity用户名和密码, 还有如果登录成功应该具有的访问权限集合.
 */
public class UserDetailServiceImpl implements UserDetailsService {

    //get set方法注入
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     *
     * @param username 用户名, springSecurity传入进来的, springSecurity从登陆用户页面输入的获取
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //创建权限集合
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        //给权限集合加入对应的访问权限
        authList.add(new SimpleGrantedAuthority("ROLE_SELLER"));


        //1. 根据用户名, 到数据库中查询对应的商家对象
        Seller seller = sellerService.findOne(username);

        //2. 如果商家对象能找到, 则判断商家审核状态是否为已审核,
        if (seller != null) {

            //3. 如果商家状态为已审核, 则返回springSecurity的User对象
            if ("1".equals(seller.getStatus())) {
                return new User(username, seller.getPassword(), authList);
            }
        }

        return null;
    }
}
