package com.writer.audio;

import com.alibaba.fastjson.JSONArray;
import com.writer.http.BaiduUtil;
import com.writer.util.B64Util;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class WordUtil {
    static Map<String, Integer> langMap;

    static {
        langMap = new HashMap<String, Integer>() {{
            put("普通话", BaiduUtil.CN_ID);
            put("English", BaiduUtil.EN_ID);
        }};
    }

    public static String getWord(String lang) {
        Integer langId = langMap.get(lang);
        if (langId == null) {
            langId = BaiduUtil.CN_ID;
        }

        RecordHelper recordHelper = RecordHelper.getInst();
        final ByteArrayOutputStream data = recordHelper.save(new ByteArrayOutputStream());

        JSONArray ret = BaiduUtil.asr("wav", B64Util.encode(data.toByteArray()), data.size(), langId);
        System.out.println(ret);
        return ret == null ? null : ret.getString(0);
    }
}
