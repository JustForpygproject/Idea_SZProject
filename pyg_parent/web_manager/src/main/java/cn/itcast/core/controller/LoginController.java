package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/showName")
    public Map<String, String> showName() {
        //获取springSecurity中当前登录用户的用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String, String> map = new HashMap<>();
        map.put("username", name);
        return map;
    }
}
