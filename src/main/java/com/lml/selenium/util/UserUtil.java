package com.lml.selenium.util;

import com.lml.selenium.dto.UserDto;
import com.lml.selenium.exception.BizException;
import lombok.experimental.UtilityClass;

/**
 * @author yugi
 * @apiNote 用户的工具类
 * @since 2019-05-13
 */
@UtilityClass
public class UserUtil {

    private final ThreadLocal<UserDto> userHolder = new ThreadLocal<>();

    /**
     * 设置登录用户
     *
     * @param user 登录的用户
     */
    public void setUser(UserDto user) {
        userHolder.set(user);
    }

    /**
     * 获取登录用户
     *
     * @return 返回当前登录的用户
     */
    public UserDto getUser() {
        UserDto userDto = userHolder.get();
        if (userDto == null) {
            throw new BizException("当前用户没有登录!");
        }
        return userDto;
    }

    /**
     * 删除登录的用户
     */
    public void deleteUser() {
        userHolder.remove();
    }


}
