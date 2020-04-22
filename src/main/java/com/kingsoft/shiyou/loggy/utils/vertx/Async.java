package com.kingsoft.shiyou.loggy.utils.vertx;

import io.vertx.core.Promise;

/**
 * @author taoshuang on 2020/4/22.
 */
public interface Async {

    default void start(Promise<Void> startPromise) {
        startPromise.complete();
    }

    default void stop(Promise<Void> stopPromise) {
        stopPromise.complete();
    }
}
