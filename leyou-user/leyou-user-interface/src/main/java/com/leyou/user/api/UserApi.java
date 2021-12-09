package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

public interface UserApi {

    /**
     * 校验手机号或者用户名是否可用
     * @param data 校验的数据
     * @param type 为1代表校验的数据为用户名，2代表手机号
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public Boolean checkUserNameOrPhone(
            @PathVariable("data") String data,
            @PathVariable(value = "type", required = false) Integer type
    );

    /**
     * 给指定手机号发送验证码
     * @param phone
     * @return
     */
    @PostMapping("send")
    public Void sendCheckCode(@RequestParam("phone") String phone);

    /**
     * 注册用户
     * @param code 验证码
     * @param user
     * @return
     */
    @PostMapping("register")
    public Void register(@RequestParam("code") String code, @Valid User user);

    /**
     * 查询用户名和密码是否正确
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public User queryUser(
            @RequestParam("username")String username,
            @RequestParam("password")String password
    );
}
