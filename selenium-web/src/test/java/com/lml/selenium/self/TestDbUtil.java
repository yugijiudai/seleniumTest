package com.lml.selenium.self;

import com.lml.selenium.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2019-05-15
 */
@Slf4j
public class TestDbUtil {


    @Test
    public void testDbToExcel() {
        String[] names = new String[]{"demo_test"};
        for (String name : names) {
            DbUtil.dbToExcel(name, "F:\\" + name + ".xls");
        }
    }

}



