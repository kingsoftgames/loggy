package com.kingsoft.shiyou.loggy.utils.web;

import io.vertx.ext.web.Router;
import io.vertx.micrometer.PrometheusScrapingHandler;

/**
 * @author taoshuang on 2020/4/22.
 */
public final class RouterUtils {

    public static void registerHealthCheck(Router router) {
        router.get("/health").handler(rc -> rc.response().end("OK"));
    }

    public static void registerMetrics(Router router) {
        router.get("/metrics").handler(PrometheusScrapingHandler.create());
    }
}
