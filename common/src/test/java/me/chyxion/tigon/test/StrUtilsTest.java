package me.chyxion.tigon.test;

import org.junit.Test;
import lombok.extern.slf4j.Slf4j;
import me.chyxion.tigon.mybatis.util.StrUtils;
import me.chyxion.tigon.mybatis.util.AssertUtils;

/**
 * @author Donghuang
 * @date Sep 02, 2020 11:55:43
 */
@Slf4j
public class StrUtilsTest {

    @Test
    public void testCapitalize() {
        AssertUtils.state("Donghuang".equals(StrUtils.capitalize("donghuang")), "Failed");
        AssertUtils.state("donghuang".equals(StrUtils.uncapitalize("Donghuang")), "Failed");
    }
}
