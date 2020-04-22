package com.kingsoft.shiyou.loggy.utils.http;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * @author taoshuang on 2020/4/22.
 */
public final class HttpUtils {

    public static <T> T parseRequest(RoutingContext rc, Class<T> clazz) {
        try {
            return rc.getBodyAsJson().mapTo(clazz);
        } catch (DecodeException | IllegalArgumentException e) {
            return null;
        }
    }

    public static <T> T parseRequestFromMap(Map<String, String> map, Class<T> clazz) {
        try {
            return DatabindCodec.mapper().convertValue(map, clazz);
        } catch (DecodeException | IllegalArgumentException e) {
            return null;
        }
    }
}
