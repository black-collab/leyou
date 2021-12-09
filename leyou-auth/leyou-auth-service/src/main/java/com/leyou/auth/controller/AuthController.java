package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private AuthService authService;

    /**
     * 用户登录认证
     *
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @PostMapping("accredit")
    public ResponseEntity<Void> authUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = this.authService.authUser(username, password);
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //设置JwtToken到Cookie
        CookieUtils.setCookie(
                request,
                response,
                this.jwtProperties.getCookieName(),
                token,
                this.jwtProperties.getCookieMaxAge() * 60
        );
        return ResponseEntity.ok().build();
    }

    /**
     * 根据cookie中的token查询用户
     *
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(
            @CookieValue("ly_token") String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        //如果验证不通过，会报错，成功就会返回user
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
        //重新获取token，为了刷新token和cookie的有效时间
        String newToken = JwtUtils.generateToken(userInfo, this.jwtProperties.getPrivateKey(), 30);
        CookieUtils.setCookie(
                request,
                response,
                this.jwtProperties.getCookieName(),
                newToken,
                this.jwtProperties.getCookieMaxAge() * 60
        );
        return ResponseEntity.ok(userInfo);
    }
}
