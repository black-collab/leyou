package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 校验手机号或者用户名是否可用注册新账号
     * @param data 校验的数据
     * @param type 为1代表校验的数据为用户名，2代表手机号
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserNameOrPhone(
            @PathVariable("data") String data,
            @PathVariable(value = "type", required = false) Integer type) {
        if (type != 2) {
            type = 1;
        }
        Boolean bol = this.userService.checkUserNameOrPhone(data, type);
        if (bol == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bol);
    }

    /**
     * 给指定手机号发送验证码
     * @param phone
     * @return
     */
    @PostMapping("send")
    public ResponseEntity<Void> sendCheckCode(@RequestParam("phone") String phone) {
        boolean matches = phone.matches("^1[358]\\d{9}$");
        if (!matches) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        this.userService.sendCheckCode(phone);
        return ResponseEntity.noContent().build();
    }

    /**
     * 注册用户
     * @param code 验证码
     * @param user
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestParam("code") String code, @Valid User user) {
        Boolean bool = this.userService.register(user, code);
        if (bool){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * 查询用户名和密码是否正确
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username")String username,
            @RequestParam("password")String password
    ) {
        User user = this.userService.queryUser(username,password);
        if(user == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }
}
