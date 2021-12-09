package com.leyou.user.mapper;

import com.leyou.user.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int checkUserNameOrPhone(@Param("data") String data, @Param("type") Integer type);

    int insertUser(User user);

    User queryUser(String username);
}
