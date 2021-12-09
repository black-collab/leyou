package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String KEY_PREFIX = "user:code:phone:";

    /**
     * 校验手机号或者用户名是否可用
     * @param data 校验的数据
     * @param type 为1代表校验的数据为用户名，2代表手机号
     * @return
     */
    public Boolean checkUserNameOrPhone(String data, Integer type) {
        if(StringUtils.isBlank(data)){
            return null;
        }
        int i = this.userMapper.checkUserNameOrPhone(data, type);
        return !(i == 1);
    }

    /**
     * 给指定手机号生成验证码并保存到redis
     * @param phone
     */
    public void sendCheckCode(String phone) {
        String code = NumberUtils.generateCode(6);
        this.stringRedisTemplate.opsForValue().set(UserService.KEY_PREFIX+phone,code,1, TimeUnit.MINUTES);
    }

    /**
     * 注册用户
     * @param user
     * @param code
     * @return
     */
    public Boolean register(User user, String code) {
        //获取redis里保存的验证码和前端传来的验证码进行校对
        String rCode = this.stringRedisTemplate.opsForValue().get(UserService.KEY_PREFIX + user.getPhone());
        if(!StringUtils.equals(code, rCode)){
            return false;
        }
        user.setCreated(new Date());
        String salt = CodecUtils.generateSalt();
        //把密码和盐用工具类进行加密
        String md5AndSaltPassword = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setSalt(salt);
        //加密后的新密码
        user.setPassword(md5AndSaltPassword);
        int i = this.userMapper.insertUser(user);
        if(i == 1){
            this.stringRedisTemplate.delete(UserService.KEY_PREFIX+user.getPhone());
            return true;
        }
        return false;
    }

    /**
     * 查询用户名和密码是否正确
     * @param username
     * @param password
     * @return
     */
    public User queryUser(String username, String password) {
        User user = this.userMapper.queryUser(username);
        if(user == null){
            return null;
        }
        String md5Password = CodecUtils.md5Hex(password, user.getSalt());
        boolean bool = StringUtils.equals(user.getPassword(), md5Password);
        return bool ? user : null;
    }
}
