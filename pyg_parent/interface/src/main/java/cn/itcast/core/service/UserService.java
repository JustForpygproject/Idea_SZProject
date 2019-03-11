package cn.itcast.core.service;


import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.user.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {

    public void sendCode(String phone);

    public boolean checkSmsCode(String phone, String smsCode);

    /**
     * 用户添加方法
     * @param user
     */
    public  void  add(User user);

    List<User> findAll();

    PageResult findPage(User user, Integer page, Integer rows);

    /**
     * 冻结用户使用户无法登陆
     * @param ids
     * @param status
     */
    void updateStatus(Long [] ids,String status);

    /**
     * 查询用户的状态Y为正常使用代码 N为失败代码
     * @param username
     * @return
     */
    User findStatusByUserName(String username);
    //用户活跃度
    int getActiveUserCount();
    //导入
    String ajaxUploadExcel(HttpServletRequest request, HttpServletResponse response);
}
