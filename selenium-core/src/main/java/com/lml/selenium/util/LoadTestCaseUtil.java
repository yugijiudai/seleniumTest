package com.lml.selenium.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.lml.selenium.entity.Selenium;
import com.lml.selenium.enums.DataSourceEnum;
import com.lml.selenium.enums.ValidEnum;
import com.lml.selenium.exception.InitException;
import com.lml.selenium.factory.SeleniumFactory;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yugi
 * @apiNote 用来加载用例的工具类, 暂时只支持从数据库和excel加载用例
 * @since 2022-01-22
 */
@UtilityClass
@Slf4j
public class LoadTestCaseUtil {


    /**
     * 从excel或者数据库里面加载相关的用例,从第一行开始,初始化SeleniumBo业务对象
     *
     * @param name excel或者数据库table的名字
     * @param step 从几个开始
     * @param end  运行到第几个
     * @return 返回加载好的数据
     */
    public List<Selenium> loadTestCase(String name, int step, int end) {
        log.info("执行:{}的用例...................", name);
        if (SeleniumFactory.getSetDto().getUseDb()) {
            return loadDbCase(name, step, end);
        }
        return loadExcelCase(name, step, end);
    }


    /**
     * 从数据里面加载相关的数据,指定开始行数,初始化SeleniumBo业务对象
     *
     * @param name 表的名字
     * @param step 从第几步开始(0表示从头开始)
     * @param end  运行到第几步(0表示运行到最后)
     * @return 返回加载好的数据
     */
    private List<Selenium> loadDbCase(String name, int step, int end) {
        try {
            if (end > 0) {
                if (end < step) {
                    throw new InitException("结束的步骤不正确");
                }
                String sql = String.format("select * from %s where id >= ? and id <= ? and valid = 'Y' order by id", name);
                return DbUtil.getDb(DataSourceEnum.SELENIUM).query(sql, Selenium.class, step, end);
            }
            String sql = String.format("select * from %s where id >= ? and valid = 'Y' order by id", name);
            return DbUtil.getDb(DataSourceEnum.SELENIUM).query(sql, Selenium.class, step);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InitException(e.getMessage());
        }
    }


    /**
     * 从数据里面加载相关的数据,指定对应的模块,初始化SeleniumBo业务对象
     *
     * @param name       表的名字
     * @param modelNames 对应的模块数组
     * @return 返回加载好的用例列表
     */
    public List<Selenium> loadDbCase(String name, Object[] modelNames) {
        StringBuilder sqlIn = new StringBuilder();
        for (int i = 0; i < modelNames.length; i++) {
            sqlIn.append("?,");
        }
        try {
            String sql = String.format("select * from %s where model in (%s) and valid = 'Y' order by id", name, sqlIn.substring(0, sqlIn.length() - 1));
            return DbUtil.getDb(DataSourceEnum.SELENIUM).query(sql, Selenium.class, modelNames);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InitException(e.getMessage());
        }
    }


    /**
     * 从excel里面加载相关的数据,指定开始行数,初始化SeleniumBo业务对象
     *
     * @param name excel的名字
     * @param step 从第几步开始(0表示从头开始)
     * @param end  运行到第几步(0表示运行到最后)
     * @return 返回加载好的数据
     */
    private List<Selenium> loadExcelCase(String name, int step, int end) {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream(String.format("case/%s.xls", name)));
        int size = reader.getSheet().getLastRowNum();
        if (step > size) {
            throw new InitException("开始的步骤不正确");
        }
        if (end > 0) {
            if (end < step) {
                throw new InitException("结束的步骤不正确");
            }
            size = end;
        }
        // 这里不能使用readAll直接映射成实体类,因为下拉窗口有可能读到是空字符串,这样会导致报错
        List<Map<String, Object>> read = reader.read(0, step, size);
        List<Selenium> list = read.stream().map(map -> {
            handleEmptyString(map);
            return copyAndCheckContent(map);
        }).filter(seleniumBo -> ValidEnum.Y.equals(seleniumBo.getValid())).collect(Collectors.toList());
        log.info("要操作的情况如下:{}", list);
        return list;
    }

    /**
     * 将map的属性复制到bo类并且校验数据的完整性
     *
     * @param map 要复制的map
     * @return 返回复制和校验好的bo
     */
    private Selenium copyAndCheckContent(Map<String, Object> map) {
        Selenium selenium = new Selenium();
        BeanUtil.copyProperties(map, selenium);
        try {
            ValidationUtils.validate(selenium);
        }
        catch (Exception e) {
            String msg = String.format("id:%s,%s", selenium.getId(), e.getMessage());
            throw new InitException(msg);
        }
        return selenium;
    }


    /**
     * 处理空字符串
     *
     * @param map excel里的数据
     */
    private void handleEmptyString(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // 处理下拉窗口读到可能是空字符串
            Object value = entry.getValue();
            if (value != null && StrUtil.isBlank(value.toString())) {
                map.put(entry.getKey(), null);
            }
        }
    }


}
