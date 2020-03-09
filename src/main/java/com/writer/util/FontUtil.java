package com.writer.util;

import java.awt.*;

public class FontUtil {
    public static String[] getFonts() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return e.getAvailableFontFamilyNames();
    }
}
