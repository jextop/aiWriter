package com.writer.util;

import org.junit.Assert;
import org.junit.Test;

public class FontUtilTest {
    @Test
    public void testGetFonts() {
        String[] ret = FontUtil.getFonts();
        LogUtil.info(ret);
        Assert.assertNotNull(ret);
    }
}
