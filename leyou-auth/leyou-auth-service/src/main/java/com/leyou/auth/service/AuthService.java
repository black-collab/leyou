package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthService {

    @Resource
    private UserClient userClient;

    @Resource
    private JwtProperties jwtProperties;

    /**
     * 用户登录认证
     * @param username
     * @param password
     * @return
     */
    public String authUser(String username, String password) {
        try {
            //验证用户密码正确与否
            User user = this.userClient.queryUser(username, password);
            if (user == null){
                return null;
            }
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
            //生成token
            String token = JwtUtils.generateToken(
                    userInfo,
                    this.jwtProperties.getPrivateKey(),
                    this.jwtProperties.getExpire()
            );
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
