package com.lml.selenium.entity;

import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.enums.ClickActionEnum;
import com.lml.selenium.enums.FindTypeEnum;
import com.lml.selenium.enums.ValidEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author yugi
 * @apiNote 封装selenium操作的实体类
 * @since 2019-05-05
 */
@Data
@Accessors(chain = true)
public class Selenium {

    /**
     * 编号(必填,相当于步骤,默认从1开始)
     */
    private Integer id;

    /**
     * 相关描述(必填)
     */
    @NotEmpty(message = "相关描述不能为空!")
    private String description;

    /**
     * 步骤模块(必填)
     */
    @NotEmpty(message = "步骤模块不能为空!")
    private String model;

    /**
     * 查找这个元素后操作的动作(必填) {@link ActionEnum}
     */
    @NotNull(message = "操作的动作不能为空!")
    private ActionEnum elementAction;


    /**
     * 要查找的元素(非必填)
     */
    private String element;


    /**
     * 元素查询的方式(非必填){@link FindTypeEnum}
     */
    private FindTypeEnum findType;


    /**
     * 自定义查询这个dom节点需要等待的时间(非必填,单位:秒)
     */
    @Min(value = 1, message = "等待时间不能小于{value}秒")
    private Integer wait;

    /**
     * 自定义查询这个dom节点重试次数(非必填)
     */
    @Min(value = 1, message = "重试次数不能小于{value}")
    private Integer retry;

    /**
     * 点击使用的方法{@link ClickActionEnum}
     */
    private ClickActionEnum clickAction;

    /**
     * 是否有效(必填){@link ValidEnum}
     */
    private ValidEnum valid;

    /**
     * 预留字段
     */
    private String ext;

    /**
     * 执行这个操作后需要的回调(如果为空,则不执行)
     */
    private String callBack;

}

