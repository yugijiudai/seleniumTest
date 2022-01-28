package com.lml.selenium.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote 运行js脚本的dto
 * @since 2022-01-28
 */
@Data
@Accessors(chain = true)
public class RunScriptDto {

    /**
     * 要执行的js脚本
     */
    private String script;


    /**
     * 要执行的js脚本的参数
     */
    private Object[] args;


    /**
     * 执行脚本后的回调函数(json格式),格式参考:
     *
     * @see RunMethodDto
     */
    private String callFn;

}
