package com.lml.selenium.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Maps;
import com.lml.selenium.enums.DataSourceEnum;
import com.lml.selenium.exception.BizException;
import com.lml.selenium.exception.InitException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yugi
 * @apiNote
 * @since 2019-05-14
 */
@UtilityClass
@Slf4j
public class DbUtil {


    /**
     * 获取对应的数据源
     *
     * @param dataSourceEnum 数据源枚举
     * @return 返回对应的数据源
     */
    public Db getDb(DataSourceEnum dataSourceEnum) {
        return Db.use(dataSourceEnum.getDataSource());
    }

    /**
     * 从excel入库
     *
     * @param name excel的名字(excel的名字要对应表的名字)
     */
    public void excelToDb(String name) {
        String excelName = "case/" + name + ".xls";
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream(excelName));
        List<Map<String, Object>> read = reader.read(0, 0, reader.getSheet().getLastRowNum());
        List<Entity> records = read.stream().map(map -> {
            Entity entity = Entity.create(name);
            entity.addFieldNames(ArrayUtil.toArray(map.keySet(), String.class));
            BeanUtil.copyProperties(map, entity);
            return entity;
        }).collect(Collectors.toList());
        for (Entity record : records) {
            log.info("{}", record);
        }
        try {
            getDb(DataSourceEnum.SELENIUM).insert(records);
        }
        catch (SQLException e) {
            log.error("插入发生异常,{}", e);
        }
    }

    /**
     * 重构表的行数(因为现在表的主键是自增,如果后面想在表中间插入几条数据,就需要手动把后面数据的id调大,使用此方法可以一次过把后面的id调大)
     * 此方法会删除原来的数据,使用的时候请小心！
     *
     * @param table 要重构的表
     * @param start 从第几条数据开始
     * @param step  id自增的步长
     */
    public void refactorDbRow(String table, int start, int step) {
        // 倒序
        String querySql = StrUtil.format("select * from {} where id >= ? order by id desc", table);
        String delSql = StrUtil.format("delete from {} where id >= ?", table);
        try {
            List<Entity> query = getDb(DataSourceEnum.SELENIUM).query(querySql, start);
            DbUtil.getDb(DataSourceEnum.SELENIUM).execute(delSql, start);
            for (Entity entity : query) {
                entity.set("id", entity.getInt("id") + step);
                DbUtil.getDb(DataSourceEnum.SELENIUM).insert(entity);
            }
        }
        catch (Exception e) {
            log.error("插入发生异常,{}", e);
            throw new BizException(e);
        }
    }

    /**
     * 数据库到处到excel
     *
     * @param table  表的名字
     * @param outPut 文件的输出路径
     */
    public void dbToExcel(String table, String outPut) {
        String sql = String.format("select * from %s order by id", table);
        String excelFile = "case/template.xls";
        try {
            List<Entity> query = getDb(DataSourceEnum.SELENIUM).query(sql);
            URL uri = ResourceUtil.getResource(excelFile);
            if (uri == null) {
                throw new InitException("文件:" + excelFile + "未找到!");
            }
            try (ExcelWriter writer = ExcelUtil.getWriter(FileUtil.file(uri))) {
                writer.passCurrentRow();
                for (Entity entity : query) {
                    Map<String, Object> map = Maps.newLinkedHashMap();
                    BeanUtil.copyProperties(entity, map);
                    writer.writeRow(map, false);
                }
                if (StrUtil.isNotBlank(outPut)) {
                    writer.flush(FileUtil.file(outPut));
                    return;
                }
                writer.flush();
            }
        }
        catch (Exception e) {
            log.error("导出到excel发生异常,{}", e);
        }
    }

}
