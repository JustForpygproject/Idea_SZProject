package cn.itcast.core.controller;

import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginController {

    @Reference
    private UserService userService;



    @RequestMapping("name")
    public Map getUserName(){
        //获取当前登陆用户名
        String loginUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,String> loginName = new HashMap<>(16);
        if (loginUserName != null) {
            loginName.put("loginName", loginUserName);
        }else {
            loginName.put("loginName", "未登录");
        }
        return loginName;
    }
}
