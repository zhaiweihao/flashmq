package com.ace.flashmq.remoting.protocol;

import com.alibaba.fastjson.JSON;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassAdapter;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author:ace
 * @date:2020-09-01
 */
public class RemotingSerializable {

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    public static byte[] encode(final Object obj) {
        final String json = toJson(obj, false);
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    public static String toJson(final Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }

    public static <T> T decode(final byte[] data, Class<T> classOf) {
        String json = new String(data, CHARSET_UTF8);
        return fromJson(json, classOf);
    }

    public static <T> T fromJson(String json, Class<T> classOf) {
        return JSON.parseObject(json, classOf);
    }

}

