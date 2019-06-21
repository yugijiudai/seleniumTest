package com.lml.selenium.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote 数据源枚举
 * @since 2019-06-11
 */
@Getter
public enum DataSourceEnum {

    /**
     * 自动化用例的库
     */
    SELENIUM("selenium"),

    /**
     * oracle的库
     */
    ORACLE("oracle"),

    ;

    private String dataSource;

    DataSourceEnum(String dataSource) {
        this.dataSource = dataSource;
    }}
