package com.lml.selenium.dto;

import com.lml.selenium.enums.ClickActionEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author yugi
 * @apiNote 处理元素的数据传输类
 * @since 2019-04-30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Builder
public class EleHandleDto extends BaseSeleniumDto {

    /**
     * 查找的方法
     */
    private By by;

    /**
     * {@link ClickActionEnum}
     */
    private ClickActionEnum actionExecuteMethod;


    /**
     * 找到的的元素
     */
    private WebElement element;

    /**
     * 输入框要输入的东西
     */
    private String keys;

    /**
     * 节点查找失败重试的次数
     */
    private Integer retry;

    /**
     * 查询元素等待的时间(秒)
     */
    private Integer waitTime;

}
