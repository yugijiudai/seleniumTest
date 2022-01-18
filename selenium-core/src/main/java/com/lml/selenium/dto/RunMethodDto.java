package com.lml.selenium.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote 执行方法dto
 * @since 2019-05-07
 */
@Data
@Accessors(chain = true)
public class RunMethodDto {

    /**
     * 要执行方法所在的类的名字
     */
    private String className;

    /**
     * 要执行的方法名字
     */
    private String methodName;

    /**
     * 要执行的方法参数列表
     */
    private Object[] args;

}
