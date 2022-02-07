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
     * 等待的时间(单位:毫秒)
     */
    private Long waitTime;


    /**
     * 预留扩展字段
     */
    private String ext;

    /**
     * 处理js脚本的dto
     */
    private RunScriptDto runScriptDto;

    /**
     * 处理调用方法的dto
     */
    private RunMethodDto runMethodDto;

    /**
     * 切换到父级或者default的frame
     */
    private SwitchFrameActionEnum switchFrameAction;
}
