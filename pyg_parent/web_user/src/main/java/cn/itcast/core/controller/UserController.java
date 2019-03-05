package cn.itcast.core.controller;

import cn.itcast.core.common.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 生成随机六位以内数字作为验证码, 发送到指定手机号上
     * @param phone 手机号
     * @return
     */
    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {
        try {
            if (phone == null || "".equals(phone)) {
                return new Result(false, "请正确填写手机号!");
            }
            if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                return new Result(false, "手机号不正确!");
            }
            userService.sendCode(phone);
            return new Result(true , "短信发送成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false , "短信发送失败!");
        }
    }

    /**
     * 完成注册, 保存用户
     * @param user      用户对象
     * @param smscode   页面填写的验证码
     */
    @RequestMapping("/add")
    public Result add(@RequestBody User user, String smscode) {
        try {
            //1. 校验验证码是否正确
            boolean isCheck = userService.checkSmsCode(user.getPhone(), smscode);
            //2. 如果验证码或者手机号不正确返回错误信息
            if (!isCheck) {
                return new Result(false, "手机号或者验证码填写错误!");
            }
            //3. 保存用户
            user.setCreated(new Date());
            user.setUpdated(new Date());
            //用户注册来源默认为pc端浏览器注册
            user.setSourceType("1");
            //用户状态默认为正常
            user.setStatus("Y");
            userService.add(user);
            return new Result(true, "注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败!");
        }
    }
}
