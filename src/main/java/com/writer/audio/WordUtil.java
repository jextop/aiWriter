package com.writer.audio;

import com.alibaba.fastjson.JSONArray;
import com.writer.ai.BaiduUtil;
import com.writer.util.B64Util;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class WordUtil {
    public static final String AI_BAIDU = "百度AI";

    public static final String LANG_CN = "普通话";
    public static final String LANG_EN = "English";

    static Map<String, Integer> langMap;

    static {
        langMap = new HashMap<String, Integer>() {{
            put(LANG_CN, BaiduUtil.CN_ID);
            put(LANG_EN, BaiduUtil.EN_ID);
        }};
    }

    public static String getWord(String service, String lang) {
        Integer langId = langMap.get(lang);
        if (langId == null) {
            langId = BaiduUtil.CN_ID;
        }

        // 读取音频数据
        RecordHelper recordHelper = RecordHelper.getInst();
        ByteArrayOutputStream data = recordHelper.save(new ByteArrayOutputStream());

        // 调用AI服务
        String word = null;
        if (AI_BAIDU.equals(service)) {
            JSONArray wordArr = BaiduUtil.asr("wav", B64Util.encode(data.toByteArray()), data.size(), langId);
            System.out.println(wordArr);
            word = wordArr == null ? null : wordArr.getString(0);
        }

        return word;
    }
}
