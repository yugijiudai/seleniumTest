package com.lml.selenium.dto;

import com.lml.selenium.enums.SwitchFrameActionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote 不需要查找节点的数据传输类
 * @since 2019-05-05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class NoEleHandlerDto extends BaseSeleniumDto {

    /**
     * 等待的时间
     */
    private Integer waitTime;


    /**
     * 要执行的js脚本
     */
    private String script;


    /**
     * 要执行的脚本的参数
     */
    private Object[] args;

    /**
     * 预留扩展字段
     */
    private String ext;

    /**
     * 执行脚本后的回调函数(json格式),格式参考:
     *
     * @see RunMethodDto
     */
    private String callFn;

    /**
     * 切换到父级或者default的frame
     */
    private SwitchFrameActionEnum switchFrameAction;
}
