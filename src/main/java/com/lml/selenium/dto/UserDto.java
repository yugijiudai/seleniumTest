package com.lml.selenium.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote 用户传输对象
 * @since 2019-06-21
 */
@Data
@Accessors(chain = true)
public class UserDto {

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String pass;
}
